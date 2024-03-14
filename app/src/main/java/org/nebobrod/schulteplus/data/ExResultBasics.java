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

import java.util.LinkedHashMap;
import java.util.Map;

@DatabaseTable(tableName = "exresult")
public class ExResultBasics extends ExResult{
	private static final String TAG = "ExResultBasics";
	/** No fields yet only numValue & comment are used */

	public ExResultBasics(){};

	public ExResultBasics(long numValue, int events, int levelOfEmotion, int levelOfEnergy, String note) {
		super(numValue, levelOfEmotion, levelOfEnergy, note);
		this.setTurns(events);
	}

	@Override
	public Map<String, String> toMap() {
		Map<String, String> stringMap = new LinkedHashMap<>();
		stringMap = super.toMap();
		stringMap.put(Utils.getRes().getString(R.string.lbl_events), turns() + "");

		return stringMap;
	}
}
