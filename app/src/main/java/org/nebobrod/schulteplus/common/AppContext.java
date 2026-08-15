/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

/**
 * Явный контекст выполнения упражнения (этап 1 перехода к модульной архитектуре).
 * Создаётся на границе приложения ({@link ExerciseRunner#createAppContext()})
 * и передаётся домену вместо статических чтений ExerciseRunner.
 * Расширяется инкрементально: prob-параметры, exTypes.
 */
public class AppContext {

	private final String exTypeId;
	private final String symbolType;
	private final boolean ratings;
	private final String uak;
	private final String uid;
	private final String name;

	public AppContext(String exTypeId, String symbolType, boolean ratings,
			String uak, String uid, String name) {
		this.exTypeId = exTypeId;
		this.symbolType = symbolType;
		this.ratings = ratings;
		this.uak = uak;
		this.uid = uid;
		this.name = name;
	}

	public String getExTypeId() {
		return exTypeId;
	}

	public String getSymbolType() {
		return symbolType;
	}

	public boolean isRatings() {
		return ratings;
	}

	public String getUak() {
		return uak;
	}

	public String getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}
}
