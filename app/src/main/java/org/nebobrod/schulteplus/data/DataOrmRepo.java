/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.intFromString;
import static org.nebobrod.schulteplus.common.Const.QUERY_COMMON_LIMIT;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.data.DatabaseHelper.getHelper;

import android.database.SQLException;
import android.media.MediaPlayer;
import org.nebobrod.schulteplus.common.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.nebobrod.schulteplus.common.Const;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.AppExecutors;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/** Provides common CRUD methods working on local SQLite DB by ORMLite
 **/
public class DataOrmRepo<TEntity extends Identifiable<String>> implements DataRepository<TEntity, String> {
	private static final String TAG = DataOrmRepo.class.getSimpleName();

	private final Class<TEntity> entityType;
	private final DatabaseHelper helper;
	private final Dao<TEntity, Integer> dao;
	private static final AppExecutors appExecutors = new AppExecutors();
	private final Executor bgRunner;

	/**
	 * easy constructor
	 */
	public DataOrmRepo(Class<TEntity> entityClass) {
		this.entityType = entityClass;
		this.helper = DatabaseHelper.getHelper();
		this.dao = getAnyDao(entityClass.getSimpleName());
		this.bgRunner = new AppExecutors().getDiskIO();
	}

	public Class<TEntity> getEntityType() {
		return entityType;
	}

	/**
	 * Get Dao-object <p> (like {@link DatabaseHelper#getExResultDao()}) for:
	 * @param className defined as a String
	 */
	public<T> Dao<T, Integer>  getAnyDao(String className) {
		Dao<T, Integer> _dao;

		try {
			// getting method name like "getExResultBasicsDao"
			String methodName = "get" + className + "Dao";
			// getting method
			Method daoMethod = helper.getClass().getMethod(methodName);

			// Calling method to get Dao of object
			_dao = (Dao<T, Integer>) daoMethod.invoke(helper);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return _dao;
	}

	/**
	 * Put into a DataRepository
	 * @param result ExResult's child classes
	 */
	@Deprecated
	public<T> void put(T result) {
		Dao<T, Integer> dao = getAnyDao(result.getClass().getSimpleName());

		try {
			// put the object into DB:
			dao.create(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets from a DataRepository <p>
	 * number of rows as defined in: {@link Const#QUERY_COMMON_LIMIT}
	 * Depends on {@link ExerciseRunner} (userHelper object, uak field)
	 */
	@Deprecated
	public<T> List<T> getListLimited(Class<T> clazz, String exType) {
		try {
			// get name of data-class
			String className = clazz.getSimpleName();
			// get dao-method name
			String methodName = "get" + className + "Dao";
			// get dao-method by name
			Method daoMethod = helper.getClass().getMethod(methodName);

			// call method to get Dao of clazz
			Dao<T, Integer> dao = (Dao<T, Integer>) daoMethod.invoke(helper);

			// Prepare the Query
			QueryBuilder<T, Integer> qb = dao.queryBuilder();
			Where where = qb.where();
			Field field;
			String fieldName;

			// uid field must be filtered
			field = clazz.getDeclaredField("UID_FIELD_NAME");
			fieldName = (String) field.get(null);
			where.eq(fieldName, ExerciseRunner.getUserHelper().getUid());

			// and
			try {
				// exType field must be filtered if exists
				field = clazz.getDeclaredField("EXTYPE_FIELD_NAME");
				fieldName = (String) field.get(null);
				where.and();
				where.eq(fieldName, exType);
			} catch (NoSuchFieldException e) {
				/*no op*/
			} catch (IllegalAccessException | IllegalArgumentException | java.sql.SQLException e) {
				throw new RuntimeException(e);
			}

//			PreparedQuery<T> preparedQuery = qb.prepare();
//			return dao.queryBuilder().limit(QUERY_COMMON_LIMIT).query();

			// timeStamp field must be ordered
			field = clazz.getDeclaredField("TIMESTAMP_FIELD_NAME");
			fieldName = (String) field.get(null);

			// return result of query
			return qb.limit(QUERY_COMMON_LIMIT).orderBy(fieldName, false).query();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void unpersonalise(String uid, String unpersonalisedName) {
		; // not need for local data
	}

	/**
	 * Checks the repository for a given id and returns a boolean representing its existence.
	 *
	 * @param documentName the unique id of an entity.
	 * @return A {@link Task} for a boolean which is 'true' if the entity for the given id exists, 'false' otherwise.
	 */
	@Override
	public Task<Boolean> exists(String documentName) {
		final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

		// Convert "uaknum.123123" to "123123" for UserHelper.id (the only 1 uak is possible for 1 uid)
		String _documentName = documentName;
		int dotIndex = documentName.indexOf(".");
		if (dotIndex >= 0) {
			_documentName = documentName.substring(dotIndex); 	// From the dot till end of string
		}
		String _docName = _documentName;

		Callable<Boolean> callable = () -> {
			try {
				Boolean _result = dao.idExists(Integer.valueOf(_docName));
				taskCompletionSource.setResult(_result); 	// Success
				return _result;
			} catch (java.sql.SQLException e) {
				throw new RuntimeException(e); 			// Catch it in runnable below
			}
		};

		// Run callable
		bgRunner.execute(new Runnable() {
			@Override
			public void run() {
				try {
					callable.call();
				} catch (Exception e) {
					Log.e(TAG, "exists: " + documentName + "in" + dao.getDataClass().getSimpleName() + "Err: " + e.getLocalizedMessage(), e);
					taskCompletionSource.setException(e); 	// Rise exception on callable
				}
			}
		});

		return taskCompletionSource.getTask();
	}

	/**
	 * Checks the repository for a given id and returns a boolean representing its existence.
	 *
	 * @param id the unique id of an entity.
	 * @param cb {@link RepoCallback} for a boolean which is <code>true</code> if the entity for the given id exists, <code>false</code> otherwise.
	 */
	@Override
	public void exists(String id, RepoCallback<Boolean> cb) {

	}

	/**
	 * Stores an entity in the repository so it is accessible via its unique id.
	 *
	 * @param tEntity the entity implementing {@link Identifiable} to be stored.
	 * @return An {@link Task} to be notified of failures.
	 */
	@Override
	public Task<Void> create(TEntity tEntity) {
		final TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

		//  Callable, which makes db-operation
		Callable<Void> callable = () -> {
			try {
				dao.createOrUpdate(tEntity);
				taskCompletionSource.setResult(null); // Success
			} catch (java.sql.SQLException e) {
				Log.e(TAG, "create: " + tEntity + "in" + dao.getDataClass().getSimpleName() + "Err: " + e.getLocalizedMessage(), e);
				taskCompletionSource.setException(e); // Error
			}
			return null;
		};

		// Run callable
		bgRunner.execute(new Runnable() {
			@Override
			public void run() {
				try {
					callable.call();
				} catch (Exception e) {
					taskCompletionSource.setException(e); // Rise exception callable
				}
			}
		});

		return taskCompletionSource.getTask();
	}

	/**
	 * Queries the repository for an uniquely identified entity and returns it. If the entity does
	 * not exist in the repository, a new instance is returned.
	 *
	 * @param documentName the unique id of an entity.
	 * @return A {@link Task} for an entity implementing {@link Identifiable}.
	 */
	@Override
	public Task<TEntity> read(String documentName) {
		final TaskCompletionSource<TEntity> taskCompletionSource = new TaskCompletionSource<>();

		// Convert "uaknum.123123" to "123123" for UserHelper.id (the only 1 uak is possible for 1 uid)
		String _documentName = documentName;
		int dotIndex = documentName.indexOf(".");
		if (dotIndex >= 0) {
			_documentName = documentName.substring(dotIndex); 	// From the dot till end of string
		}
		String _docName = _documentName;

		//  Callable, which makes db-operation
		Callable<Void> callable = () -> {
			try {
				taskCompletionSource.setResult(dao.queryForId(intFromString(_docName)));	// Success
			} catch (java.sql.SQLException e) {
				Log.e(TAG, "read id: " + _docName + " in" + dao.getDataClass().getSimpleName() + "Err: " + e.getLocalizedMessage(), e);
				taskCompletionSource.setException(e); // Error
			}
			return null;
		};

		// Run callable
		bgRunner.execute(new Runnable() {
			@Override
			public void run() {
				try {
					callable.call();
				} catch (Exception e) {
					taskCompletionSource.setException(e); // Rise exception callable
				}
			}
		});

		return taskCompletionSource.getTask();
	}

	/**
	 * Updates an entity in the repository
	 *
	 * @param tEntity the new entity to be stored.
	 * @return A {@link Task} to be notified of failures.
	 */
	@Override
	public Task<Void> update(TEntity tEntity) {
		return null;
	}

	/**
	 * Deletes an entity from the repository.
	 *
	 * @param id uniquely identifying the entity.
	 * @return A {@link Task} to be notified of failures.
	 */
	@Override
	public Task<Void> delete(String id) {
		return null;
	}


	public interface OrmGetCallback<R> {
		void onComplete(R result);
	}

	@Deprecated
	public static synchronized void achievePut(String uid, String uak, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark) {
		Achievement achievement = new Achievement();
		achievement.set(uid,  uak, name,  timeStamp,  dateTime,  recordText,  recordValue,  specialMark);
		try {
			DatabaseHelper helper = new DatabaseHelper();
			Dao<Achievement, Integer> dao = helper.getAchievementDao();
			dao.create(achievement);
		} catch (SQLException | java.sql.SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * return result of query to local db in background thread. play sound on success.
	 * @param callback for main thread
	 * @param <R> (check the reason of this)
	 */
	@Deprecated
	public static <R> void achieveGet25(OrmGetCallback<R> callback) {
		final ArrayList[] arrayList = {null};

		appExecutors.getDiskIO().execute(() -> {
			final R result;
			try {
				Log.d(TAG, "query to local db within: " + Thread.currentThread());
//				result = callable.call();
				Dao<Achievement, Integer> dao = getHelper().getAchievementDao();
				QueryBuilder<Achievement, Integer> builder = dao.queryBuilder();
				builder.orderBy(Achievement.DATE_FIELD_NAME, false).limit(QUERY_COMMON_LIMIT);
				result = (R) dao.query(builder.prepare());

				MediaPlayer mp = MediaPlayer.create(getAppContext(), org.nebobrod.schulteplus.R.raw.slow_whoop_bubble_pop);
				mp.start();
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mPlayer) {
						mPlayer.release();
					}
				});
			} catch (Exception e) {
				Log.e(TAG, "achieveGet25: ", e);
				throw new RuntimeException(e);
			}
			appExecutors.mainThread().execute(() -> {
				callback.onComplete(result);
			});
		});
	}

	@Deprecated
	public static ArrayList getAchievementList(){
		Log.i(DataOrmRepo.class.getName(), "Show list again");
		try {
			Dao<Achievement, Integer> dao = getHelper().getAchievementDao();
			QueryBuilder<Achievement, Integer> builder = dao.queryBuilder();
			builder.orderBy(Achievement.DATE_FIELD_NAME, false).limit(QUERY_COMMON_LIMIT);
			return (ArrayList) dao.query(builder.prepare());
		} catch (Exception e) {
			Log.i(DataOrmRepo.class.getName(), e.getMessage());
			return null;
		}
	}
}
