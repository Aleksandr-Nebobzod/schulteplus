/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import org.nebobrod.schulteplus.Const;

import java.util.List;

/** Makes one entry point for different places to maintain data
 */
public class DataRepositories implements DataRepository {

	private static final OrmRepo ormLiteDataHandler = new OrmRepo();
//	private static final FirestoreUtils firestoreDataHandler = new FirestoreUtils();


	/**
	 * Puts into a DataRepository
	 *
	 * @param result
	 */
	@Override
	public void putResult(Object result) {

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
