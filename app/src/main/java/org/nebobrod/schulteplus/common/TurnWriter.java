/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import org.nebobrod.schulteplus.data.Turn;

/**
 * Запись хода журнала (этап 1.4 развязки ядра): персистенция — вне домена,
 * реализация предоставляется приложением (см. DefaultTurnWriter).
 */
public interface TurnWriter {

	void write(Turn turn);
}
