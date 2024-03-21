/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

import org.nebobrod.schulteplus.ExerciseRunner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import com.google.api.core.ApiFuture;
/*import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;*/
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;
import org.nebobrod.schulteplus.Log;

import javax.inject.Singleton;


/** Provides  user Preference copy in FirebaseFirestore
 * (ExerciseRunner is used as helper) */
@Singleton
public class UserDbPref {
	private static final String TAG = "UserDbPref";
//	public static final String DB_URL = "https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app";
	private static final String DB_PROJECT_ID = "schulte-plus";
	private static final String DB_PATH = "spdbs/dev/userDbPreferences"; // TODO: 18.12.2023 about "prod"...

	private static UserDbPref instance = null;
	ExerciseRunner runner;
	Map<String, Object> objectMap;
	public String uid, name, email;
	public long tsUpdated;
	public int psyCoins, hours, level;
	static FirebaseFirestore db;

	private UserDbPref(ExerciseRunner exerciseRunner) {
		this.runner = exerciseRunner;

		this.uid = ExerciseRunner.GetUid();
		this.name = runner.getName();
		this.email = runner.getEmail();
		this.psyCoins = runner.getPoints();
		this.hours = runner.getHours();
		this.level = runner.getLevel();
		this.tsUpdated = runner.getTsUpdated();

		this.objectMap = new HashMap<>();

		getSubCollectionDocumentRef(null, exerciseRunner.getUid());
	}


	public static UserDbPref getInstance(ExerciseRunner exerciseRunner) {
		return (instance == null ? instance = new UserDbPref(exerciseRunner) : instance);
	}

/*	private  static void init (String projectId) throws IOException {
			// [START firestore_setup_client_create]
			// Option 1: Initialize a Firestore client with a specific `projectId` and
			//           authorization credential.
			// [START firestore_setup_client_create_with_project_id]
		try {
				FirestoreOptions firestoreOptions =
						FirestoreOptions.getDefaultInstance().toBuilder()
								.setProjectId(projectId)
								.setCredentials(GoogleCredentials.getApplicationDefault())
								.build();
				UserDbPref.db = (Firestore) firestoreOptions.getService();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "dbRead Error: " + e.getLocalizedMessage().toString(), null);
			}
			// [END firestore_setup_client_create_with_project_id]
			// [END firestore_setup_client_create]
	}
	*/
	private static void init () {
		db = FirebaseFirestore.getInstance();
	}

	public  void save() {
		objectMap = new HashMap<>();
		objectMap.put("uid", runner.getUid());
		objectMap.put("name", runner.getName());
		objectMap.put("email", runner.getEmail());
		objectMap.put("psyCoins", runner.getPoints());
		objectMap.put("hours", runner.getHours());
		objectMap.put("level", runner.getLevel());
		objectMap.put("tsUpdated", runner.getTsUpdated());
// Get a new write batch
		WriteBatch batch = db.batch();

// Set the value of doc
		CollectionReference dbRef = db.collection(DB_PATH);
		DocumentReference docRef = db.document(DB_PATH + "/" + runner.getUid());
		batch.set(docRef, objectMap);

/* EXAMPLES of other actionsL:
// Update the population of 'SF'
		DocumentReference sfRef = db.collection("cities").document("SF");
		batch.update(sfRef, "population", 1000000L);

// Delete the city 'LA'
		DocumentReference laRef = db.collection("cities").document("LA");
		batch.delete(laRef);*/

// Commit the batch
		batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				runner.onCallback(objectMap);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "save onFailure: " + e.getMessage());
			}
		})
		;


/* // ...one more strange attempt from doc: https://cloud.google.com/firestore/docs/samples/firestore-data-set-doc-upsert
		// asynchronously update doc, create the document if missing
		ApiFuture<WriteResult> writeResult =
				db.collection("cities").document("BJ").set(objectMap, SetOptions.merge());
// ...
		System.out.println("Update time : " + writeResult.get().getUpdateTime());

		docRef.set(objectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void unused) {

			}
		})*/

/* That's generates autoId, unacceptable yet:
		dbRef.add(objectMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
			@Override
			public void onSuccess(DocumentReference documentReference) {
				Log.d(TAG, "onSuccess: " + documentReference.toString());
				runner.onCallback(objectMap);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "onFailure: " + e.getMessage());
			}
		});*/
	}

	public Map<String, Object> getObjectMap() {return objectMap;}

	public interface UserDbPrefCallback {
		void onCallback(Map<String, Object> objectMap);
	}

	/*
	 * method applies to DB an updates the objectMap
	 * @param cb callback
	 * @param uid user id
	 * @throws Exception -- was in docs <a href="https://github.com/googleapis/java-firestore/blob/9693c7b46dcb63b0348217ecb7c29b95ecd04191/samples/snippets/src/main/java/com/example/firestore/Quickstart.java#L47-L52">official</a>
	 */
	/*private void dbRead(UserDbPrefCallback cb, String uid) {
		try {
		init(DB_PROJECT_ID);
		DocumentReference docRef = db.collection("cities").document("SF");
			docRef.get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<com.google.firebase.firestore.DocumentSnapshot> task) {
					if (task.isSuccessful()) {
						DocumentSnapshot document = task.getResult();
						if (document.exists()) {
							Log.d(TAG, "DocumentSnapshot data: " + document.getData());
							objectMap = document.getData();
						} else {
							Log.d(TAG, "No such document");
							objectMap = null;
						}
					} else {
						Log.d(TAG, "get failed with ", task.getException());
						objectMap = null;
					}
					cb.onCallback(objectMap);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "dbRead Error: " + e.getLocalizedMessage().toString(), null);
		}
	}
*/
	public void getSubCollectionDocumentRef(@Nullable UserDbPrefCallback cb, String docKey) {
		// [START firestore_data_reference_subcollection]
		// Reference to a document in subcollection "userDbPreferences" /spdbs/dev/userDbPreferences
		init();
		DocumentReference document =
				db.collection("spdbs").document("dev").collection("userDbPreferences").document(docKey);

		document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
				try {
					objectMap = (value == null ? null : value.getData());
					Log.d(TAG, "onEvent: " + objectMap);
					Log.d(TAG, "is from cache?: " + value.getMetadata().isFromCache());
				} catch (Exception e) {
					Log.d(TAG, "onEvent Exception: " + e.getMessage());
					objectMap = null;
				}
//				if (null != cb) cb.onCallback(objectMap);
				runner.onCallback(objectMap);
			}
		});
		// [END firestore_data_reference_subcollection]
	}
}
