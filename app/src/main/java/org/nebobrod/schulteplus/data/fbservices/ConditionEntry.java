/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nebobrod.schulteplus.data.DataRepository;

/**
 * Condition keeper for firebase query
 */
public class ConditionEntry {
	@NonNull
	private final String field;
	@NonNull
	private final DataRepository.WhereCond condition;
	@Nullable
	private final Object value;

	public ConditionEntry(@NonNull String field, @DataRepository.WhereCond.Condition DataRepository.WhereCond condition, @Nullable Object value) {
		this.field = field;
		this.condition = condition;
		this.value = value;
	}

	// Getters
	public String getField() {
		return field;
	}

	public DataRepository.WhereCond getCondition() {
		return condition;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ConditionEntry{" +
				"field='" + field + '\'' +
				", condition=" + condition +
				", value=" + value +
				'}';
	}
}

