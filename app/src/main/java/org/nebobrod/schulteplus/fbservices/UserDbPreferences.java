/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

import static org.nebobrod.schulteplus.Utils.getRes;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import org.nebobrod.schulteplus.Log;
import org.nebobrod.schulteplus.R;

import javax.inject.Singleton;


/** Provides  user Preference copy in FirebaseFirestore
 * (ExerciseRunner is used as helper) */
@Singleton
public class UserDbPreferences {
	private static final String TAG = "UserDbPreferences";
//	public static final String DB_URL = "https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app";
	private static final String DB_PROJECT_ID = "schulte-plus";
	private static final String DB_ROOT = getRes().getString(R.string.firestore_root);
	private static final String PATH = DB_ROOT + "userDbPreferences"; // TODOne 21.03.2024: 18.12.2023 about "prod"...

	private static UserDbPreferences instance = null;
	private FirebaseFirestore db;
	private CollectionReference dbRef;
	private DocumentReference docRef;
	private ListenerRegistration currentDoc;

	private ExerciseRunner runner;
	private Map<String, Object> objectMap;
	private String uid, name, email;
	private long tsUpdated;
	private int psyCoins, hours, level;


	private UserDbPreferences(ExerciseRunner exerciseRunner) {
		this.runner = exerciseRunner;

		this.uid = ExerciseRunner.GetUid();
		this.name = runner.getName();
		this.email = runner.getEmail();
		this.psyCoins = runner.getPoints();
		this.hours = runner.getHours();
		this.level = runner.getLevel();
		this.tsUpdated = runner.getTsUpdated();
		this.objectMap = new HashMap<>();

		init();

		currentDoc = getSubCollectionDocumentRef(null, uid);
	}

	private void init () {
		db = FirebaseFirestore.getInstance();
		// Set the value of doc
		dbRef = db.collection(PATH);
		docRef = db.document(PATH + "/" + uid);
	}

	public static UserDbPreferences getInstance(String newUid) {
		if (instance == null) {
			if (ExerciseRunner == null)
		} else if (runner.getUid().equals(newUid)) {
			
		}
		if (ExerciseRunner.getUserHelper().getUid().equals(newUid))
		return instance;
	}
	public static UserDbPreferences getInstance(ExerciseRunner exerciseRunner) {
		return (instance == null ? instance = new UserDbPreferences(exerciseRunner) : instance);
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
				UserDbPreferences.db = (Firestore) firestoreOptions.getService();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "dbRead Error: " + e.getLocalizedMessage().toString(), null);
			}
			// [END firestore_setup_client_create_with_project_id]
			// [END firestore_setup_client_create]
	}
	*/


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
		});


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
	// TODO: 26.03.2024 redundant? check & optimize
	public ListenerRegistration getSubCollectionDocumentRef(@Nullable UserDbPrefCallback cb, String docKey) {
		// [START firestore_data_reference_subcollection]
		// Reference to a document in subcollection "userDbPreferences" /spdbs/dev/userDbPreferences
//		DocumentReference document1 = db.collection("spdbs").document("dev").collection("userDbPreferences").document(docKey);
		DocumentReference document =
				db.collection(PATH).document(docKey);

		return document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

	// removes user name and email, put dummy neName into DB
	public void depersonalise(String uid, String newName) {
		DocumentReference docRef = db.document(PATH + "/" + uid);
		docRef.update(
				"name", newName,
				"email", newName+"@mail.com"
		).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				android.util.Log.v(TAG, "onComplete: depersonalised");
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				android.util.Log.v(TAG, "onFailure: depersonalisation failed");
			}
		});
	}
}
