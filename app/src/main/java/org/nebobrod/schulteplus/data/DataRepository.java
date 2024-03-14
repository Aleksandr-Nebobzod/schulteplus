/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import java.util.List;

/** Provides necessary methods for stored data
 */
public interface DataRepository {

	/** Puts into a DataRepository */
	public<T> void putResult(T result);

	/** Gets from a DataRepository <p>
	 * number of rows as defined in: {@link org.nebobrod.schulteplus.Const#QUERY_COMMON_LIMIT} <p>
	 * way to call: <code>List< ExResultBasics > results = getResultsLimited(ExResultBasics.class);</code>*/
	public<T> List<T> getResultsLimited(Class<T> clazz, String exType);
}

