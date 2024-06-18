/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;

import static org.nebobrod.schulteplus.Utils.getRes;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.nebobrod.schulteplus.common.Const;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.data.z_DataRepository;

import java.util.List;

/** Provides common CRUD methods working on external SchultePlus DB by FirebaseFirestore
 **/
@Deprecated
public class FirestoreRepo implements z_DataRepository {
	private static final String TAG = FirestoreRepo.class.getSimpleName();
	private static final String DB_ROOT = getRes().getString(R.string.firestore_root);

	static FirebaseFirestore db;
//	static DatabaseReference fbReference;


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
	public<T> void put(T result) {
		final String PATH = DB_ROOT + "exresults";

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
		// TODO: 06.04.2024 implement 
		;
	}
}
