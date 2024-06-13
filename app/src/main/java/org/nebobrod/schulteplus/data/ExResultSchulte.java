/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@DatabaseTable(tableName = "exresult")
public class ExResultSchulte  extends ExResult {
	private static final String TAG = "ExResultSchulte";

	/* numValue is used as number of milliseconds, spent */
//	private int turns;
//	int turnsMissed;
//	float average;
//	float rmsd; // Root-mean-square deviation as a sign of stability & rhythm in exercises

	public ExResultSchulte(){}
	public ExResultSchulte(long seed, long time, int turns, int turnsMissed, float average, float rmsd, int levelOfEmotion, int levelOfEnergy, String note) {
		super(seed, time, levelOfEmotion, levelOfEnergy, note);
		this.setTurns(turns);
		this.setTurnsMissed(turnsMissed);
		this.setAverage(average);
		this.setRmsd(rmsd);
	}

	/** minimum update */
	public void update(long timeStamp, long numValue, int turns, int turnsMissed, float average, float rmsd, int levelOfEmotion, int levelOfEnergy, String note) {
		super.update(timeStamp, numValue, levelOfEmotion, levelOfEnergy, note);
		this.setTurns(turns);
		this.setTurnsMissed(turnsMissed);
		this.setAverage(average);
		this.setRmsd(rmsd);
	}

	@Override
	public Map<String, String> toMap() {
		Map<String, String> stringMap = new LinkedHashMap<>();
		stringMap = super.toMap();

		// Add pairs in accordance with Class
		stringMap.put(Utils.getRes().getString(R.string.lbl_turns), getTurns() + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_turns_missed), getTurnsMissed() + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_average), String.format(Locale.ENGLISH, "%.2f", (getAverage() /1000F)));
		stringMap.put(Utils.getRes().getString(R.string.lbl_sd), String.format(Locale.ENGLISH,"%.2f", (getRmsd() /1000F)));

		return stringMap;
	}
}
