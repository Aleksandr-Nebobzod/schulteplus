/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.timeStampU;

import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

@DatabaseTable(tableName = "exresult")
public class ExResultBasics extends ExResult{
	private static final String TAG = "ExResultBasics";
	/** No fields yet only numValue & comment are used */

	public ExResultBasics(){}

	public ExResultBasics(long seed, long numValue, int events, int levelOfEmotion, int levelOfEnergy, String note) {
		super(seed, numValue, levelOfEmotion, levelOfEnergy, note);
		this.setTurns(events);
	}

	/** minimum update */
	public void update(long timeStamp, long numValue, int events, int levelOfEmotion, int levelOfEnergy, String note) {
		super.update(timeStamp, numValue, levelOfEmotion, levelOfEnergy, note);
		this.setTurns(events);
	}

	@Override
	public Map<String, String> toMap() {
		Map<String, String> stringMap = new LinkedHashMap<>();
		stringMap = super.toMap();
		stringMap.put(Utils.getRes().getString(R.string.lbl_events), getTurns() + "");

		return stringMap;
	}
}
