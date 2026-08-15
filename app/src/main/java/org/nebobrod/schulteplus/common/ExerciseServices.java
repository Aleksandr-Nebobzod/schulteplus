/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

/**
 * Прикладные сервисы упражнения (этап 1.5 развязки ядра):
 * персистенция и шаблон символов; собираются на границе приложения.
 */
public class ExerciseServices {

	private final TurnWriter turnWriter;
	private final SymbolTemplate symbolTemplate;
	private final ResultSaver resultSaver;

	public ExerciseServices(TurnWriter turnWriter, SymbolTemplate symbolTemplate, ResultSaver resultSaver) {
		this.turnWriter = turnWriter;
		this.symbolTemplate = symbolTemplate;
		this.resultSaver = resultSaver;
	}

	public TurnWriter getTurnWriter() {
		return turnWriter;
	}

	public SymbolTemplate getSymbolTemplate() {
		return symbolTemplate;
	}

	public ResultSaver getResultSaver() {
		return resultSaver;
	}
}
