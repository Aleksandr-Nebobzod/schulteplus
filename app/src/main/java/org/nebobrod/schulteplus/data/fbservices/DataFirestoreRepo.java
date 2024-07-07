/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;


import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.hasFieldName;
import static org.nebobrod.schulteplus.common.Const.QUERY_COMMON_LIMIT;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.Identifiable;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.data.UserHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Looper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Manages data access for Firebase with POJO, the entity implementing {@link Identifiable} to be stored.
 */
public class DataFirestoreRepo<TEntity extends Identifiable<String>> implements DataRepository<TEntity, String> {

	private static final String TAG = "DataFirestoreRepo";
	private static final String DB_ROOT = getRes().getString(R.string.firestore_root); // example: "spdbs/dev/"

	private final Class<TEntity> entityClass;
	private final CollectionReference collectionReference;
	private final String collectionName;
	private final Executor bgRunner;

	/**
	 * Initializes the repository storing the data in the given collection. Should be from POJO annotation.
	 */
	public DataFirestoreRepo(Class<TEntity> entityClass) {
		this.entityClass = entityClass;
		this.collectionName = getTableName(entityClass);

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		this.collectionReference = db.collection(DB_ROOT + this.collectionName);
		this.bgRunner = new AppExecutors().getNetworkIO();
	}

	public CollectionReference getCollectionReference() {
		return collectionReference;
	}

	public Executor getBgRunner() {
		return bgRunner;
	}

	@Override
	public Task<Boolean> exists(final String documentName) {
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Checking existence of '" + documentName + "' in '" + collectionName + "'.");

		return documentReference.get().continueWith(bgRunner, new Continuation<DocumentSnapshot, Boolean>() {
			@Override
			public Boolean then(@NonNull Task<DocumentSnapshot> task) {
				Log.d(TAG,"Checking if '" + documentName + "' exists in '" + collectionName +"'.");
				return task.getResult().exists();
			}
		});
	}

	@Override
	public void exists(final String documentName, RepoCallback<Boolean> cb) {
		Boolean result;
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Checking existence of '" + documentName + "' in '" + collectionName + "'.");

		try {
			result = documentReference.get().continueWith(bgRunner, new Continuation<DocumentSnapshot, Boolean>() {
				@Override
				public Boolean then(@NonNull Task<DocumentSnapshot> task) {
					Log.d(TAG,"Checking if '" + documentName + "' exists in '" + collectionName +"'.");
					return task.getResult().exists();
				}
			}).getResult();
			cb.onSuccess(result);
		} catch (Exception e) {
			cb.onError(e);
		}
	}

	@Override
	public Task<Void> create(TEntity entity) {
		final String documentName = entity.getEntityKey();
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Creating '" + documentName + "' in '" + collectionName + "'.");
		return documentReference.set(entity).addOnFailureListener(bgRunner, new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.e(TAG, "There was an error creating '" + documentName + "' in '" + collectionName + "'!", e);
			}
		});
	}

	@Override
	public Task<TEntity> read(String id) {
		final String documentName = id;
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Getting '" + documentName + "' in '" + collectionName + "'. main " + Looper.getMainLooper().isCurrentThread() + Thread.currentThread());

		return documentReference.get().continueWith(bgRunner, new Continuation<DocumentSnapshot, TEntity>() {
			@Override
			public TEntity then(@NonNull Task<DocumentSnapshot> task) throws Exception {
				DocumentSnapshot documentSnapshot = task.getResult();
				Log.d(TAG,  " inside read.get().continueWith: main " + Looper.getMainLooper().isCurrentThread() + Thread.currentThread());
				if (documentSnapshot.exists()) {
					return documentSnapshot.toObject(entityClass);
				} else {
					Log.d(TAG, "Document '" + documentName + "' does not exist in '" + collectionName + "'.");
					return entityClass.newInstance();
				}
			}
		});
	}

	@Override
	public Task<Void> update(TEntity entity) {
		final String documentName = entity.getEntityKey();
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Updating '" + documentName + "' in '" + collectionName + "'.");

		return documentReference.set(entity).addOnFailureListener(bgRunner, new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.e(TAG, "There was an error updating '" + documentName + "' in '" + collectionName + "'.", e);
			}
		});
	}

	@Override
	public Task<Void> delete(final String documentName) {
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Deleting '" + documentName + "' in '" + collectionName + "'.");

		return documentReference.delete().addOnFailureListener(bgRunner, new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.e(TAG, "There was an error deleting '" + documentName + "' in '" + collectionName + "'.", e);
			}
		});
	}

	public void getListLimited(RepoCallback<List<TEntity>> cb) {
		List<TEntity> result = new ArrayList<>();
		Log.i(TAG, "Applying to  '" + collectionReference.getPath() + " for limited list");

		collectionReference
				.orderBy("timeStamp", Query.Direction.DESCENDING)
				.limit(QUERY_COMMON_LIMIT)
				.get().continueWith(bgRunner, new Continuation<QuerySnapshot, List<TEntity>>() {
					@Override
					public List<TEntity> then(@NonNull Task<QuerySnapshot> task) throws Exception {

						Log.d(TAG,  " inside getListLimited.get().continueWith: main " + Looper.getMainLooper().isCurrentThread() + Thread.currentThread());
						if (task.isSuccessful()) {
							for (QueryDocumentSnapshot document : task.getResult()) {
								Log.d(TAG, document.getId() + " => " + document.getData());
								result.add(document.toObject(entityClass));
							}
							cb.onSuccess(result);
						} else {
							Log.e(TAG, "Error getting documents: ", task.getException());
							cb.onError(task.getException());
						}
						return result;
					}
				});
	}

	/**
	 * Applying to {@link TEntity} collection filtering data by equality of:
	 * @param field
	 * @param value
	 * @return List limited by {@link org.nebobrod.schulteplus.common.Const#QUERY_COMMON_LIMIT}
	 */
	public Task<List<TEntity>> getListByField(@NonNull String field, @NonNull @WhereCond.Condition WhereCond condition, @Nullable Object value) {
		List<TEntity> result = new ArrayList<>();
		Log.i(TAG, "Applying to  '" + collectionReference.getPath()
				+ " for list filtered by " + field + " == " + value);

		Query _query = collectionReference.limit(QUERY_COMMON_LIMIT);

		if (hasFieldName(entityClass, field)) {
			_query = _query.whereEqualTo(field, value);
			switch (condition) {
				case EQ:
					_query = _query.whereEqualTo(field, value);
					break;
				case GE:
					_query = _query.whereGreaterThanOrEqualTo(field, value);
					break;
				case LE:
					_query = _query.whereLessThanOrEqualTo(field, value);
					break;
				default:
					throw new IllegalArgumentException("Unsupported condition: " + condition);
			}
		}

		return _query
				.get().continueWith(bgRunner, new Continuation<QuerySnapshot, List<TEntity>>() {
					@Override
					public List<TEntity> then(@NonNull Task<QuerySnapshot> task) {

						Log.d(TAG,  " inside getListByField.get().continueWith: main " + Looper.getMainLooper().isCurrentThread() + Thread.currentThread());
						QuerySnapshot querySnapshot = task.getResult();
						if (task.isSuccessful()) {
							for (QueryDocumentSnapshot document : querySnapshot) {
								try {
									Log.d(TAG, document.getId() + " => " + document.getData());
									result.add(document.toObject(entityClass));
								} catch (Exception e) {
									android.util.Log.e(TAG, "then ERROR getting doc: " + document.getId(), e);
									throw new RuntimeException(e);
								}
							}
							if (result.size() > 0) {
								result.sort(Comparator.comparingLong(TEntity::getTimeStamp).reversed());
							} else {
								Log.w(TAG, "No documents  with field: " + field + " as: " + value);
								return null;
							}
						} else {
							Log.e(TAG, "Error getting documents: ", task.getException());
							return null;
						}
						return result;
					}
				});
	}

	/**
	 * Clean user personal data before the account removal in {@link TEntity} related collection (name, email fields).
	 *
	 * @param uid                user id from Authentication service
	 * @param newName new dummy name for keep ExResults' history
	 */
	public Task<Void> unpersonilise(String uid, String newName) {
		TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
		String dummyEmail = newName + getRes().getString(R.string.txt_common_mailbox);

		collectionReference.whereEqualTo("uid", uid).get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						WriteBatch batch = FirebaseFirestore.getInstance().batch();
						for (DocumentSnapshot document : task.getResult().getDocuments()) {
							if (hasFieldName(entityClass, "name")) {
								batch.update(document.getReference(), "name", newName);
							}
							if (hasFieldName(entityClass, "email")) {
								batch.update(document.getReference(), "email", dummyEmail);
							}

						}
						batch.commit()
								.addOnCompleteListener(batchTask -> {
									if (batchTask.isSuccessful()) {
										taskCompletionSource.setResult(null);
									} else {
										taskCompletionSource.setException(batchTask.getException());
									}
								});
					} else {
						taskCompletionSource.setException(task.getException());
					}
				});

		return taskCompletionSource.getTask();
	}
}