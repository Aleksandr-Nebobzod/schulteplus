/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.nebobrod.schulteplus.common.Const;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

import java.util.List;

/** Makes one entry point for different places to maintain data
 */
public class DataRepos<TEntity extends Identifiable<String>>  implements xDataRepository {

	private final DataOrmRepo ormRepo;
//	private static final FirestoreUtils firestoreDataHandler = new FirestoreUtils();
	private  final DataFirestoreRepo fsRepo;

	public DataRepos(Class<TEntity> entityClass) {
		ormRepo = new DataOrmRepo(entityClass);
		fsRepo = new DataFirestoreRepo<>(entityClass);
	}

	/**
	 * Puts into a DataRepositories
	 * @param result
	 */
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

	public Task<Void> create(TEntity entity) {
		Task<Void> task1 = ormRepo.create(entity);
		Task<Void> task2 = fsRepo.create(entity);

		return Tasks.whenAll(task1, task2);
	}

	/** get two results from both repos and return the newest one
	 * and update the other if it is older to be sure they are same
 	 * @param id key of entity
	 * @return
	 */
	public Task<TEntity> read(String id) {
		Task<TEntity> task1 = ormRepo.read(id);
		Task<TEntity> task2 = fsRepo.read(id);

		return Tasks.whenAll(task1, task2).continueWith(new Continuation<Void, TEntity>() {
			@Override
			public TEntity then(@NonNull Task<Void> task) throws Exception {

				// Compare and update if necessary
				TEntity r1 = task1.getResult();
				TEntity r2 = task1.getResult();
				if (r1.getTimeStamp() == r2.getTimeStamp()) {
					return r1;
				} else if (r1.getTimeStamp() > r2.getTimeStamp()) {
					fsRepo.create(r1); 	// update from newer local data
					return r1;
				} else {
					ormRepo.create(r2); 	// update from newer central data
					return r2;
				}
			}
		});
	}
}
