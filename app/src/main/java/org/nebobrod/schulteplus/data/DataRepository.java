/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import androidx.annotation.IntDef;

import org.checkerframework.common.value.qual.EnumVal;
import org.nebobrod.schulteplus.common.Log;

import com.j256.ormlite.table.DatabaseTable;

import com.google.android.gms.tasks.Task;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Manages data access for POJOs that are uniquely identifiable by a key, such as POJOs implementing {@link Identifiable}.
 */
public interface DataRepository<TEntity extends Identifiable<TKey>, TKey> {


	/**
	 * Allows to use common callback (for old approach)
	 * @param <R> data-object returnable to call-point
	 */
	public interface RepoCallback<R> {
		void onSuccess(R result);

		void onError(Exception e);
	}

	enum WhereCond {
		/** Means whereEqualTo */
		EQ,
		/** Means whereGreaterThanOrEqualTo */
		GE,
		/** Means whereLessThanOrEqualTo */
		LE;

		@EnumVal({"EQ", "GE", "LE"})
		@Retention(RetentionPolicy.SOURCE)
		public @interface Condition {}
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
	 * @param cb {@link RepoCallback} for a boolean which is <code>true</code> if the entity for the given id exists, <code>false</code> otherwise.
	 */
	void exists(TKey id, RepoCallback<Boolean> cb);

	/**
	 * Stores an entity in the repository so it is accessible via its unique id.
	 * @param entity the entity implementing {@link Identifiable} to be stored.
	 * @return An {@link Task} to be notified of failures.
	 */
	Task<Void> create(TEntity entity);

	/**
	 * Queries the repository for an uniquely identified entity and returns it. If the entity does
	 * not exist in the repository, a new instance is returned.
	 * @param id the unique id of an entity.
	 * @return A {@link Task} for an entity implementing {@link Identifiable}.
	 */
	Task<TEntity> read(TKey id);

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
	 * Checks if clazz annotated properly
	 * @param clazz which defines POJO
	 * @return database table name (collection name)
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
