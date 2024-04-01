/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

import static org.nebobrod.schulteplus.Utils.getRes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.nebobrod.schulteplus.Const;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.data.DataRepository;

import java.util.List;

/** Provides common CRUD methods working on external SchultePlus DB by FirebaseFirestore
 **/
public class FirestoreRepo implements DataRepository {
	private static final String TAG = FirestoreRepo.class.getSimpleName();
	private static final String DB_ROOT = getRes().getString(R.string.firestore_root);

	static FirebaseFirestore db;
	static DatabaseReference fbReference;


	private  static void init (String path) {
		db = FirebaseFirestore.getInstance();
		CollectionReference collRef = db.collection(path);
//		DocumentReference docRef = db.document(path + "/" + runner.getUid());
	}

	/**
	 * Puts into a DataRepository
	 * @param result ExResult's child classes
	 */
	@Override
	public<T> void putResult(T result) {
		final String PATH = DB_ROOT + "exresults";

	}

	/**
	 * Gets from a DataRepository <p>
	 * number of rows as defined in: {@link Const#QUERY_COMMON_LIMIT}
	 */
	@Override
	public<T> List<T> getResultsLimited(Class<T> clazz, String exType) {
		return null;
	}
}
