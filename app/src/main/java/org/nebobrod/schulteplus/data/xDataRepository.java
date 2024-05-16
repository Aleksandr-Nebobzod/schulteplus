/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import org.nebobrod.schulteplus.common.Const;

import java.util.List;

/** Provides necessary methods for stored data
 */
public interface xDataRepository {

	/** Puts into a DataRepository */
	public<T> void put(T result);

	/** Gets from a DataRepository <p>
	 * number of rows as defined in: {@link Const#QUERY_COMMON_LIMIT} <p>
	 * way to call: <code>List< ExResultBasics > results = getResultsLimited(ExResultBasics.class);</code>*/
	public<T> List<T> getListLimited(Class<T> clazz, String exType);

	/**
	 * Clean user personal data (after account removal)
	 * @param uid user id
	 * @param unpersonalisedName new dummy name for keep ExResults' history
	 */
	void unpersonalise(String uid, String unpersonalisedName);
}

