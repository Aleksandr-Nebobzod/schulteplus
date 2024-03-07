/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@DatabaseTable(tableName = "exresult")
public class ExResultSchulte  extends ExResult {
	private static final String TAG = "ExResultSchulte";

	/* numValue is used as number of milliseconds, spent */
//	private int turns;
//	int turnsMissed;
//	float average;
//	float rmsd; // Root-mean-square deviation as a sign of stability & rhythm in exercises

	public ExResultSchulte(){};
	public ExResultSchulte(long time, int turns, int turnsMissed, float average, float rmsd, int levelOfEmotion, int levelOfEnergy, String note) {
		super(time, levelOfEmotion, levelOfEnergy, note);
		this.setTurns(turns);
		this.setTurnsMissed(turnsMissed);
		this.setAverage(average);
		this.setRmsd(rmsd);
	}

	@Override
	public Map<String, String> toMap() {
		Map<String, String> stringMap = new LinkedHashMap<>();
		stringMap = super.toMap();

		// Remove pair with wrong key
//		stringMap.remove(Utils.getRes().getString(R.string.lbl_events));

		// Add pairs in accordance with Class
		stringMap.put(Utils.getRes().getString(R.string.lbl_turns), turns() + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_turns_missed), turnsMissed() + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_average), String.format("%.2f", (average() /1000F)));
		stringMap.put(Utils.getRes().getString(R.string.lbl_sd), String.format("%.2f", (rmsd() /1000F)));

		return stringMap;
	}
}
