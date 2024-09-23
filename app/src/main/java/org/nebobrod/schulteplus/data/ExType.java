/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.overlayBadgedIcon;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.data.fbservices.ConditionEntry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.Transient;

/**
 * Definition of JSON structure for exercise-type master-data
 */
public class ExType {
	private final String TAG = this.getClass().getSimpleName();

	public static final String ACHIEVE_CERTIFIED = "certified";
	public static final String ACHIEVE_PURCHASED = "purchased";
	public static final String ACHIEVE_PASSED = "passed";
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

	/** Achieved by user and not kept in json */
	@Transient   // exclude from gson serialisation
	private Map<String, Boolean> achieved;

	public ExType(String id, String parentId, String nameEn, int status, int price, boolean certRequired, Map<String, ConditionEntry> achieveConditions) {
		this.id = id;
		this.parentId = parentId;
		this.nameEn = nameEn;
		this.status = status;
		this.price = price;
		this.certRequired = certRequired;
		this.achieveConditions = achieveConditions;
		this.achieved = new HashMap<>(); // init

		// Fill with false for every key
		for (String key : achieveConditions.keySet()) {
			achieved.put(key, false);
		}
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

	public Map<String, Boolean> getAchieved() {
		return achieved;
	}

	public void setAchieved(Map<String, Boolean> achieved) {
		this.achieved = achieved;
	}

	public static Map<String, ExType> load() {
		// Get JSON file
		InputStream inputStream = getRes().openRawResource(R.raw.ex_types);
		InputStreamReader reader = new InputStreamReader(inputStream);

		// Using Gson to parse JSON
		Gson gson = new Gson();
		Type mapType = new TypeToken<Map<String, ExType>>() {}.getType();
		Map<String, ExType> exTypeMap = gson.fromJson(reader, mapType);

		// Map field not mentioned in JSON
		for (ExType exType : exTypeMap.values()) {
			if (null == exType.getAchieveConditions()) {
				continue;	// safety
			}
			// Fill with false for every key
			Map<String, Boolean> achieved = new HashMap<>();
			for (String key : exType.getAchieveConditions().keySet()) {
				achieved.put(key, false);
			}
			exType.setAchieved(achieved);
		}

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

	/** Apply to local DB and refreshes achieved map */
	public Task<Void> refreshAchieved() {
		DataOrmRepo<Achievement> repo = new DataOrmRepo<>(Achievement.class);
		List<Task<Void>> tasks = new ArrayList<>();

		// No requirements
		if (achieved == null || achieved.size() == 0) {
			return Tasks.forResult(null);
		}

		// Check all the Options required
		achieved.forEach((key, value) -> {
			System.out.println("Key: " + key + ", Value: " + value);
			if (!value) {
				// Check if the user has this achievement recorded
				Task<Void> task = repo.queryForGroup(ExerciseRunner.GetUid(), id, key)
						.continueWithTask(taskResult -> {
							Integer result = taskResult.getResult();
							if (result < 1) {
								achieved.put(key, false);  // Set Not achieved
							} else {
								achieved.put(key, true);   // Set achieved
							}
							return Tasks.forResult(null);
						});
				tasks.add(task);
			}
		});

		// Return a Task that completes when all tasks in the list are completed
		return Tasks.whenAll(tasks);
	}

	/** defines what badges to show by achieved records */
	public Drawable getBadge() {

		// No requirements
		if (achieved == null || achieved.size() == 0) {
			return new ColorDrawable(Color.TRANSPARENT);
		}

		// Quiz not passed
		if (certRequired && achieved.get(ACHIEVE_CERTIFIED) != true) {
			return getRes().getDrawable(R.drawable.ic_badge_question, null);
		}

		// Psycoins not invested
		if (price != 0 && achieved.get(ACHIEVE_PURCHASED) != true) {
			return getRes().getDrawable(R.drawable.ic_badge_psycoin, null);
		}

		// Default no badge
		return new ColorDrawable(Color.TRANSPARENT);
	}

	/** defines what investments luck by achieved records */
	public String getRequiredAchievement() {

		// No requirements
		if (achieved == null || achieved.size() == 0) {
			return ACHIEVE_PASSED;
		}

		// Quiz not passed
		if (certRequired && achieved.get(ACHIEVE_CERTIFIED) != true) {
			return ACHIEVE_CERTIFIED;
		}

		// Psycoins not invested
		if (price != 0 && achieved.get(ACHIEVE_PURCHASED) != true) {
			return ACHIEVE_PURCHASED;
		}

		// Default no need
		return ACHIEVE_PASSED;
	}
}
