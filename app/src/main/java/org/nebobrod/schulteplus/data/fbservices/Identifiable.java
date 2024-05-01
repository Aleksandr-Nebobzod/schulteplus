/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;

import com.google.firebase.firestore.Exclude;

/**
 * Represents an object that can be uniquely identified among other objects of the same type
 * by using an UID.
 *
 * @param <TKey> type of the unique key (UID) this object is uniquely identified by. The type needs
 *              a correct implementation of its equals() method or the behaviour of code using this
 *              interface will be undefined.
 */
public interface Identifiable<TKey> {

	@Exclude
	TKey getEntityKey();
}
