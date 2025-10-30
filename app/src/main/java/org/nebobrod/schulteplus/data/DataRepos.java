/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.Const;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.fbservices.ConditionEntry;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/** Makes one entry point for different places to maintain data
 */
public class DataRepos<TEntity extends Identifiable<String>>  implements z_DataRepository {
	public static final String TAG = "DataRepos";

	private final Class<TEntity> entityClass;
	private final DataOrmRepo ormRepo;
//	private static final FirestoreUtils firestoreDataHandler = new FirestoreUtils();
	private  final DataFirestoreRepo fsRepo;

	public DataRepos(Class<TEntity> entityClass) {

		this.entityClass = entityClass;
		ormRepo = new DataOrmRepo(entityClass);
		fsRepo = new DataFirestoreRepo<>(entityClass);
	}

	/**
	 * Puts into a DataRepositories
	 * @param result
	 */
	@Deprecated
	@Override
	public void put(Object result) {
		ormRepo.put(result);
	}

	/**
	 * Gets from a DataRepository <p>
	 * number of rows as defined in: {@link Const#QUERY_COMMON_LIMIT}
	 */
	@Override
	public<T> List<T> getListLimited(Class<T> clazz, String exType) {
		return null;
	}

	/**
	 * Clean user personal data (after account removal)
	 *
	 * @param uid                user id
	 * @param unpersonalisedName new dummy name for keep ExResults' history
	 */
	@Override
	public void unpersonalise(String uid, String unpersonalisedName) {
		// TODO: 25.03.2024 provide checks to all tables using  name & email
			// UserHelper name email devId (leave uak)
			// ExResult name (leave uak)
			// Achievement (leave uak)
	}

	//////////////////////////////

	/**
	 * Create records in both repos (updates if id exists)
	 * @param entity the entity implementing {@link Identifiable} to be stored.
	 * @return the Task with success or failure state (for both repos)
	 */
	public Task<Void> create(TEntity entity) {
		// Synchronisation signal-object
		final TaskCompletionSource<Void> completionSource = new TaskCompletionSource<>();

		Task<Void> task1 = ormRepo.create(entity).addOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(@NonNull Task task) {
				if (task.isSuccessful()) {

					// Save into second Repo (having id from ORM)
					fsRepo.create(entity).addOnCompleteListener(new OnCompleteListener() {
						@Override
						public void onComplete(@NonNull Task task) {
							if (task.isSuccessful()) {
								completionSource.setResult(null);
							} else {
								completionSource.setException(task.getException());
							}
						}
					});
				} else {
					completionSource.setException(task.getException());
				}
			}
		});

		return completionSource.getTask();
	}

	/** Get two results from both repos and return the newest one
	 * and update the other if it is older to be sure they are same
 	 * @param id key of entity
	 * @return the Task with most fresh (by timeStamp) Result, the entity implementing {@link Identifiable} to be stored.
	 */
	public Task<TEntity> read(String id) {
		Task<TEntity> task1 = ormRepo.read(id);
		Task<TEntity> task2 = fsRepo.read(id);

		return Tasks.whenAll(task1, task2).continueWith(new Continuation<Void, TEntity>() {
			@Override
			public TEntity then(@NonNull Task<Void> task) throws Exception {

				// Compare and update if necessary
				TEntity r1 = task1.getResult();
				TEntity r2 = task2.getResult();
				if (r1.getTimeStamp() == r2.getTimeStamp()) {
					return r1;
				} else if (r1.getTimeStamp() > r2.getTimeStamp()) {
					fsRepo.create(r1); 		// update central repo from newer local data
					return r1;
				} else {
					ormRepo.create(r2); 	// update local data from newer central data
					return r2;
				}
			}
		});
	}


	/**
	 * Checks local and central repos.Analyse if there are different devices data, updates and return
	 * @param id hashCode of uid from fbAuth
	 * @return  actual userHelper (score, name, etc.)
	 */
	public Task<UserHelper> getLatestUserHelper(int id) {
		DataOrmRepo<UserHelper> ormRepo = new DataOrmRepo<>(UserHelper.class);
		DataFirestoreRepo<UserHelper> fsRepo = new DataFirestoreRepo<>(UserHelper.class);
		UserHelper userHelper = null;
		UserHelper latestFsUser = null;

		// Get data from local repository
		CompletableFuture<UserHelper> ormFuture = CompletableFuture.supplyAsync(() -> {
			try {
				return Tasks.await(ormRepo.read(String.valueOf(id)));
			} catch (ExecutionException | InterruptedException e) {
				Log.e(TAG, "ORM Task failed: ", e);
				return null;
			}
		});

		// Get data-set from central repository
		CompletableFuture<List<UserHelper>> fsFuture = CompletableFuture.supplyAsync(() -> {
			try {
				return Tasks.await(fsRepo.getListByField(
						new ConditionEntry("id", DataRepository.WhereCond.EQ, id)));
			} catch (ExecutionException | InterruptedException e) {
				Log.e(TAG, "Firestore Task failed: ", e);
				return null;
			}
		});

		try {
			UserHelper ormUser = ormFuture.get();
			List<UserHelper> fsUsers = fsFuture.get();

			if ((ormUser == null) && fsUsers == null) {
				throw new ExecutionException(new RuntimeException("No actual user record id=" + id + " in any repository!"));
			}

			if (fsUsers.size() > 0) {
				// sort the list to get most fresh user record from central repo (including other devices)
				// 240531 got sorted in getListByField()
				latestFsUser = fsUsers.get(0);
			}


			// local record exists
			if (ormUser != null) {
				String ormUak = ormUser.getUak();
				long ormTimestamp  = ormUser.getTimeStamp();

				for (UserHelper helper : fsUsers) {

					// same uak in central repo
					if (ormUak.equals(helper.getUak())) {
						if (helper.getTimeStamp() < ormTimestamp) {
							userHelper = ormUser;
							fsRepo.create(userHelper);
						} else {
							userHelper = helper;
							ormRepo.create(userHelper);
						}
					}
				}

				// no same uak found in central repo
				if (userHelper == null) {
					if (ormTimestamp == 0) {
						throw new ExecutionException(new RuntimeException("No actual user record in any repository!"));
					} else if (ormTimestamp > latestFsUser.getTimeStamp()) {
						userHelper = ormUser; 			// Used local db offline
						Log.w(TAG, "getLatestUserHelper, fsRepo.create: " +  userHelper);
						fsRepo.create(userHelper);
					} else {
						userHelper = latestFsUser; 		// fresh data from another device
						userHelper.setUak(ormUak);
						Log.w(TAG, "getLatestUserHelper, new UAK, fsRepo.create: " +  userHelper);
						ormRepo.create(userHelper); 	// Copy fresh central data to local DB
					}
				}
			} else {

				// Local user does not exist
				if (latestFsUser == null) {
					throw new ExecutionException(new RuntimeException("No actual user record in any repository!"));
				}
				userHelper = latestFsUser; 				// fresh data from another device
				userHelper.setUak(Utils.generateUak()); 		// first login on this device (new uak)
				ormRepo.create(userHelper); 			// Copy fresh central data to local DB
				fsRepo.create(userHelper); 				// Copy fresh data with new uak to central DB
			}
		} catch (ExecutionException | InterruptedException e) {
			if (e.getCause() instanceof RuntimeException) {
				Log.e(TAG, "getLatestUserHelper, RuntimeException occurred", e);
			} else {
				Log.e(TAG, "getLatestUserHelper, Error occurred", e);
			}
			return Tasks.forException(e);
		}

		return Tasks.forResult(userHelper);
	}

	/** Special for Admin Notes form server*/
	public Task<Void> fetchAdminNotes(long sinceTimeStamp) {
		final TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

		new DataFirestoreRepo<>(AdminNote.class).getListByField(
				new ConditionEntry("uak", DataRepository.WhereCond.EQ, "0")).addOnCompleteListener(new OnCompleteListener<List<AdminNote>>() {
			@Override
			public void onComplete(@NonNull Task<List<AdminNote>> task) {
				if (task.isSuccessful()) {
					List<AdminNote> list = task.getResult();
					if (list == null) {
						Log.w(TAG, "fetchAdminNotes: Error loading data from the server.");
						taskCompletionSource.setException(new RuntimeException("fetchAdminNotes: No data loaded from the server."));
						return;
					}
					Log.d(TAG, "fetchAdminNotes, onComplete, loaded: " + list.size());

					List<AdminNote> filteredList = list.stream()
							.filter(note -> note.getTimeStamp() > sinceTimeStamp)
							.collect(Collectors.toList());
					Log.d(TAG, "fetchAdminNotes, onComplete, after filter: " + filteredList.size());

					new DataOrmRepo<>(entityClass).load((List<TEntity>) filteredList).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								Log.d(TAG, "fetchAdminNotes, onComplete, loaded onto ORM ");
								taskCompletionSource.setResult(null); // Success
							} else {
								Log.w(TAG, "fetchAdminNotes, onComplete, ERROR loading onto ORM ");
								taskCompletionSource.setException(task.getException()); // Error
							}
						}
					});
				} else {
					Log.w(TAG, "fetchAdminNotes: Error loading data from the server.");
					taskCompletionSource.setException(new RuntimeException("fetchAdminNotes: Error loading data from the server."));
				}
			}
		});

		return taskCompletionSource.getTask();
	}

	/** Static method to get local SQL-executable copy of uid achievements*/
	@Deprecated
	public static Task<SQLiteDatabase> fetchAchievementsToTemp(String uid) {

		DataFirestoreRepo<Achievement> _fsRepo = new DataFirestoreRepo<>(Achievement.class);

		SQLiteDatabase db = SQLiteDatabase.create(null);
		String createTableQuery = "CREATE TABLE IF NOT EXISTS temp_table ("
				+ "ex_type_id TEXT NOT NULL, "
				+ "achieve_id TEXT NOT NULL, "
				+ "date TEXT, "
				+ "value INTEGER, "
				+ "PRIMARY KEY(ex_type_id, achieve_id))";  // Composite key
		db.execSQL(createTableQuery);

		//  Task for await getting all procedures
		TaskCompletionSource<SQLiteDatabase> taskCompletionSource = new TaskCompletionSource<>();

		_fsRepo.getListByField(new ConditionEntry("uid", DataRepository.WhereCond.EQ, uid))
				.addOnSuccessListener(new OnSuccessListener<List<Achievement>>() {
					@Override
					public void onSuccess(List<Achievement> achievements) {
						for (Achievement document : achievements) {
							String exTypeId = document.getExType();
							String achieveId = document.getSpecialMark();
							String date = document.getDateTime();
							int value = Integer.parseInt(document.getRecordValue());

							// Prepare record for SQLite
							ContentValues values = new ContentValues();
							values.put("ex_type_id", exTypeId);
							values.put("achieve_id", achieveId);
							values.put("date", date);
							values.put("value", value);

							// Insert record into SQLite
							db.insert("temp_table", null, values);
						}
						// set Task completed, returning prepared database
						taskCompletionSource.setResult(db);
					}
				})
				.addOnFailureListener(e -> {
					// if error Task
					taskCompletionSource.setException(e);
				});

		return taskCompletionSource.getTask();  // return the Task, after data insert finished
	}

	/** Static method to get uid achievements earlier or later of existing in local DB*/
	public static Task<Void> fetchAchievements(String uid) {
		DataOrmRepo<Achievement> _ormRepo = new DataOrmRepo<>(Achievement.class);
		DataFirestoreRepo<Achievement> _fsRepo = new DataFirestoreRepo<>(Achievement.class);

		// Fetch min and max timestamps asynchronously
		Task<Long> timeStampMinTask = _ormRepo.queryForFirst(
						_ormRepo.getDao().queryBuilder().orderBy("timeStamp", true))
				.continueWith(task -> task.isSuccessful() && task.getResult() != null ?
						task.getResult().getTimeStamp() : 0L);

		Task<Long> timeStampMaxTask = _ormRepo.queryForFirst(
						_ormRepo.getDao().queryBuilder().orderBy("timeStamp", false))
				.continueWith(task -> task.isSuccessful() && task.getResult() != null ?
						task.getResult().getTimeStamp() : System.currentTimeMillis());

		// when both are ready
		return Tasks.whenAllSuccess(timeStampMinTask, timeStampMaxTask)
				.continueWithTask(task -> {
					List<Object> results = task.getResult();
					long timeStampMin = (long) results.get(0);
					long timeStampMax = (long) results.get(1);

					// request from Firestore
					return _fsRepo.getListByField(
									new ConditionEntry("uid", DataRepository.WhereCond.EQ, uid),
									new ConditionEntry("timeStamp", DataRepository.WhereCond.LE, timeStampMin),
									new ConditionEntry("timeStamp", DataRepository.WhereCond.GE, timeStampMax))
							.continueWithTask(fsTask -> {
								if (fsTask.isSuccessful()) {
									List<Achievement> achievements = fsTask.getResult();
									// Add all records to the local DB in batch
									_ormRepo.load(achievements);
								} else {
									fsTask.getException().printStackTrace();
								}
								return Tasks.forResult(null);
							});
				});
	}

}
