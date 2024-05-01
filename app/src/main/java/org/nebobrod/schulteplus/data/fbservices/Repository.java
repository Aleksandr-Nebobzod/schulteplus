/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;

import com.google.android.gms.tasks.Task;
import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.common.Log;

/**
 * Manages data access for POJOs that are uniquely identifiable by a key, such as POJOs implementing {@link Identifiable}.
 */
public interface Repository<TEntity extends Identifiable<TKey>, TKey> {


	/**
	 * Allows
	 * @param <R>
	 */
	public interface RepoCallback<R> {
		void onSuccess(R result);
		void onError(Exception e);
	}

	/**
	 * Checks the repository for a given id and returns a boolean representing its existence.
	 * @param id the unique id of an entity.
	 * @return A {@link Task} for a boolean which is 'true' if the entity for the given id exists, 'false' otherwise.
	 */
	Task<Boolean> exists(TKey id);

	/**
	 * Checks the repository for a given id and returns a boolean representing its existence.
	 * @param id the unique id of an entity.
	 * @param cb {@link RepoCallback} for a boolean which is 'true' if the entity for the given id exists, 'false' otherwise.
	 */
	void exists(TKey id, RepoCallback<Boolean> cb);

	/**
	 * Queries the repository for an uniquely identified entity and returns it. If the entity does
	 * not exist in the repository, a new instance is returned.
	 * @param id the unique id of an entity.
	 * @return A {@link Task} for an entity implementing {@link Identifiable}.
	 */
	Task<TEntity> get(TKey id);

	/**
	 * Stores an entity in the repository so it is accessible via its unique id.
	 * @param entity the entity implementing {@link Identifiable} to be stored.
	 * @return An {@link Task} to be notified of failures.
	 */
	Task<Void> create(TEntity entity);

	/**
	 * Updates an entity in the repository
	 * @param entity the new entity to be stored.
	 * @return A {@link Task} to be notified of failures.
	 */
	Task<Void> update(TEntity entity);

	/**
	 * Deletes an entity from the repository.
	 * @param id uniquely identifying the entity.
	 * @return A {@link Task} to be notified of failures.
	 */
	Task<Void> delete(TKey id);

	/**
	 * Checks
	 * @param clazz which defines POJO
	 * @return database table name if clazz annotated properly
	 */
	default String getTableName(Class<?> clazz) {
		String tableName = "undefined";

		// Get table name from annotation
		if (clazz.isAnnotationPresent(DatabaseTable.class)) {
			DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);

			tableName = databaseTable.tableName();
		} else {
			Log.w("RepositoryInterface", clazz.getName() + " class has no annotation @DatabaseTable");
		}
		return tableName;
	}
}
