/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.getRes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.fbservices.ConditionEntry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Definition of JSON structure for exercise-type master-data
 */
public class ExType {
	private final String TAG = this.getClass().getSimpleName();
	public static final String ACHIEVE_CERTIFIED = "certified";
	public static final String ACHIEVE_PURCHASED = "purchased";
	public static final String ACHIEVE_STARTED = "started";
	public static final String ACHIEVE_MEASURED = "measured";
	public static final String ACHIEVE_BOLD = "bold";
	public static final String ACHIEVE_CONT_IMPROVEMENT = "cont_improve";
	public static final String ACHIEVE_WEEK_WINNER = "week_winner";
	public static final String ACHIEVE_MASTERED = "mastered";
	public static final int FUNC_STATUS_PLANNED = 0;
	public static final int FUNC_STATUS_EXPERIMENT = 1;
	public static final int FUNC_STATUS_PRODUCTION = 2;
	public static final int FUNC_STATUS_SUNSET = 3;
	public static final int FUNC_STATUS_ARCHIVED = 4;
	public static final int FUNC_STATUS_GONE = 5;

	private String id;
	private String parentId; 	// for hierarchy needs
	private String nameEn;
	private int status;
	private int price;
	private boolean certRequired;
	/** We can keep SQL-query in field-field */
	Map<String, ConditionEntry> achieveConditions;

	public ExType(String id, String parentId, String nameEn, int status, int price, boolean certRequired, Map<String, ConditionEntry> achieveConditions) {
		this.id = id;
		this.parentId = parentId;
		this.nameEn = nameEn;
		this.price = status;
		this.price = price;
		this.certRequired = certRequired;
		this.achieveConditions = achieveConditions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isCertRequired() {
		return certRequired;
	}

	public void setCertRequired(boolean certRequired) {
		this.certRequired = certRequired;
	}

	public Map<String, ConditionEntry> getAchieveConditions() {
		return achieveConditions;
	}

	public void setAchieveConditions(Map<String, ConditionEntry> achieveConditions) {
		this.achieveConditions = achieveConditions;
	}

	public static Map<String, ExType> load() {
		// Get JSON file
		InputStream inputStream = getRes().openRawResource(R.raw.ex_types);
		InputStreamReader reader = new InputStreamReader(inputStream);

		// Using Gson to parse JSON
		Gson gson = new Gson();
		Type mapType = new TypeToken<Map<String, ExType>>() {}.getType();
		Map<String, ExType> exTypeMap = gson.fromJson(reader, mapType);

		return exTypeMap;
		// Build Hierarchy
		/*Map<String, ExType> idToParentMap = new HashMap<>();
		Map<String, List<ExType>> parentToChildrenMap = new HashMap<>();

		for (ExType exType : exTypeMap.values()) {
			idToParentMap.put(exType.getId(), exType);

			if (exType.getParentId() != null) {
				parentToChildrenMap
						.computeIfAbsent(exType.getParentId(), k -> new ArrayList<>())
						.add(exType);
			}
		}*/ // not need it yet

		/*List<ExType> children = parentToChildrenMap.get("gcb_schulte");
		if (children != null) {
			for (ExType child : children) {
				Log.d("Child", "Name: " + child.getNameEn());
			}
		}*/ // Example: children of id = gcb_schulte
	}
}
