/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import org.nebobrod.schulteplus.common.Const;
import org.nebobrod.schulteplus.data.fbservices.FirestoreRepository;
import org.nebobrod.schulteplus.data.fbservices.UserDbPreferences;

import java.util.List;

/** Makes one entry point for different places to maintain data
 */
public class DataRepositories implements DataRepository {

	private static final OrmRepo ormLiteDataHandler = new OrmRepo();
//	private static final FirestoreUtils firestoreDataHandler = new FirestoreUtils();
	private static final FirestoreRepository<ExResult> exResultFsRepo = new FirestoreRepository<>(ExResult.class);


	/**
	 * Puts into a DataRepositories
	 * @param result
	 */
	@Override
	public void putResult(Object result) {
		ormLiteDataHandler.putResult(result);
	}

	/**
	 * Gets from a DataRepository <p>
	 * number of rows as defined in: {@link Const#QUERY_COMMON_LIMIT}
	 */
	@Override
	public<T> List<T> getResultsLimited(Class<T> clazz, String exType) {
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
		UserDbPreferences.getInstance(uid);
	}

}
