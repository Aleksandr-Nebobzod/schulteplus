/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import org.nebobrod.schulteplus.common.TurnWriter;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

/**
 * Прикладная реализация TurnWriter (этап 1.4): двойная запись хода —
 * в локальную БД (ORM) и в Firestore (мультиаккаунт/мультидевайс).
 */
public class DefaultTurnWriter implements TurnWriter {

	@Override
	public void write(Turn turn) {
		DataOrmRepo localRepo = new DataOrmRepo<>(turn.getClass());
		localRepo.put(turn);

		DataFirestoreRepo centralRepo = new DataFirestoreRepo<Turn>(Turn.class);
		centralRepo.create(turn);
	}
}
