/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

import org.nebobrod.schulteplus.Const;
import org.nebobrod.schulteplus.data.DataRepository;

import java.util.List;

/** Provides common CRUD methods working on external SchultePlus DB by FirebaseFirestore
 **/
public class FbFsRepo implements DataRepository {

	private static final String TAG = FbFsRepo.class.getSimpleName();

	private static final AppExecutors appExecutors = new AppExecutors();



	/**
	 * Puts into a DataRepository
	 *
	 * @param result ExResult's child classes
	 */
	@Override
	public<T> void putResult(T result) {

	}

	/**
	 * Gets from a DataRepository <p>
	 * number of rows as defined in: {@link Const#QUERY_COMMON_LIMIT}
	 */
	@Override
	public<T> List<T> getResultsLimited(Class<T> clazz) {
		return null;
	}
}
