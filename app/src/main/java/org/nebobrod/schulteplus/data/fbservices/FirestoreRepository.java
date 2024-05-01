/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;


import static org.nebobrod.schulteplus.Utils.getRes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.nebobrod.schulteplus.R;


/**
 * Manages data access for Firebase
 */
public class FirestoreRepository<TEntity extends Identifiable<String>> implements Repository<TEntity, String> {

	private static final String TAG = "FirestoreRepository";
	private static final String DB_ROOT = getRes().getString(R.string.firestore_root); // example: "spdbs/dev/"


	private final Class<TEntity> entityClass;
	private final CollectionReference collectionReference;
	private final String collectionName;

	/**
	 * Initializes the repository storing the data in the given collection. Should be from {@link }.
	 */
	public FirestoreRepository(Class<TEntity> entityClass) {
		this.entityClass = entityClass;
		this.collectionName = getTableName(entityClass);

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		this.collectionReference = db.collection(DB_ROOT + this.collectionName);
	}

	public CollectionReference getCollectionReference() {
		return collectionReference;
	}

	@Override
	public Task<Boolean> exists(final String documentName) {
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Checking existence of '" + documentName + "' in '" + collectionName + "'.");

		return documentReference.get().continueWith(new Continuation<DocumentSnapshot, Boolean>() {
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
			result = documentReference.get().continueWith(new Continuation<DocumentSnapshot, Boolean>() {
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
	public Task<TEntity> get(String id) {
		final String documentName = id;
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Getting '" + documentName + "' in '" + collectionName + "'.");

		return documentReference.get().continueWith(new Continuation<DocumentSnapshot, TEntity>() {
			@Override
			public TEntity then(@NonNull Task<DocumentSnapshot> task) throws Exception {
				DocumentSnapshot documentSnapshot = task.getResult();
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
	public Task<Void> create(TEntity entity) {
		final String documentName = entity.getEntityKey();
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Creating '" + documentName + "' in '" + collectionName + "'.");
		return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "There was an error creating '" + documentName + "' in '" + collectionName + "'!", e);
			}
		});
	}

	@Override
	public Task<Void> update(TEntity entity) {
		final String documentName = entity.getEntityKey();
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Updating '" + documentName + "' in '" + collectionName + "'.");

		return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "There was an error updating '" + documentName + "' in '" + collectionName + "'.", e);
			}
		});
	}

	@Override
	public Task<Void> delete(final String documentName) {
		DocumentReference documentReference = collectionReference.document(documentName);
		Log.i(TAG, "Deleting '" + documentName + "' in '" + collectionName + "'.");

		return documentReference.delete().addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "There was an error deleting '" + documentName + "' in '" + collectionName + "'.", e);
			}
		});
	}
}