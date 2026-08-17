/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.basics;

import static org.nebobrod.schulteplus.Utils.overlayBadgedIcon;
import static org.nebobrod.schulteplus.common.Const.*;
import static org.nebobrod.schulteplus.Utils.getRes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.data.ExType;

import java.util.ArrayList;


public class BasicSettings extends PreferenceFragmentCompat {
	ArrayList<Preference> exerciseTypes = new ArrayList<>();
	androidx.preference.CheckBoxPreference chosen;
	private ExerciseRunner runner = ExerciseRunner.getInstance();
	private String[] exTypes;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
		setPreferencesFromResource(R.xml.preferences_basics, rootKey);
//		PreferenceScreen screen = this.getPreferenceScreen();
//		exTypes = getRes().getStringArray(R.array.ex_type);

		initiateExerciseTypes();
	}
	@Override
	public void onResume() {
		runner.loadPreference();
		EditTextPreference exType = findPreference("prf_ex_type");
		// to find which checkbox selected on the screen:
		for (Preference p: exerciseTypes) {
			if (((androidx.preference.CheckBoxPreference) p).isChecked()) {
				chosen = (androidx.preference.CheckBoxPreference) p;
				break;
			}
		}
		if (null == chosen) {
			chosen = (androidx.preference.CheckBoxPreference) findPreference(KEY_PRF_EX_B9);
			chosen.setChecked(true);
		}
		exType.setText(chosen.getKey());
		runner.setExTypeId(exType.getText().toString()); // set to runner from invisible pref et
		// set hinted to runner
		runner.setHinted(((androidx.preference.SwitchPreference) findPreference(KEY_PRF_HINTED)).isChecked());

		super.onResume();
	}

	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		container.getContext().setTheme(R.style.preferenceScreen);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		ExerciseRunner.savePreferences();
	}

	@Override
	public boolean onPreferenceTreeClick(@NonNull Preference preference) {
		// only for Group Check Boxes:
		if (exerciseTypes.contains(preference)) {
			chosen = (androidx.preference.CheckBoxPreference) preference;
			for (Preference p : exerciseTypes) {
				((androidx.preference.CheckBoxPreference) p).setChecked(false);
			}
			chosen.setChecked(true);
			EditTextPreference exType = findPreference("prf_ex_type");
			exType.setText(chosen.getKey().toString());
			runner.setExTypeId(chosen.getKey().toString());
		}
		return super.onPreferenceTreeClick(preference);
	}


	/**
	 * method provides fnc Group of Checkboxes directly in _preferences.xml -- layout
	 */
	private void initiateExerciseTypes()
	{
		ArrayList<Preference> list = getPreferenceList(getPreferenceScreen(), new ArrayList<Preference>());
		exerciseTypes.clear();
		for (Preference p : list) {
			String pKey = p.getKey();
			// prefix "@string/gcb_" of Preference means Group Check Box
			if (p instanceof androidx.preference.CheckBoxPreference && pKey.startsWith("gcb_")) {
				p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						chosen = (androidx.preference.CheckBoxPreference) preference;
						return false;
					}
				});
				exerciseTypes.add(p);
			}
			// Setting badge
			ExType exType = ExerciseRunner.getExTypes().get(pKey);
			if (exType == null) continue;

			// Option is not available yet
			if (exType.getStatus() == ExType.FUNC_STATUS_PLANNED) {
				Drawable icon = p.getIcon();
				p.setIcon(overlayBadgedIcon(icon, getRes().getDrawable(R.drawable.ic_badge_inprogress, null)));
				p.setEnabled(false);
				continue; 				// no more requirements
			}

			// Check achieved and update badge
			exType.refreshAchieved().addOnCompleteListener(task -> {
				Drawable icon = p.getIcon();
				p.setIcon(overlayBadgedIcon(icon, exType.getBadge()));
				p.setOnPreferenceChangeListener((preference, newValue) -> {
					// Check prerequisites achieved
					ExType exType1 = runner.getExTypes().get(preference.getKey());
					if (exType1 != null && !exType1.isAllAchieved()) {
						Utils.runInvestActivity(requireContext(), preference.getKey());
						return false; // as no click
					}
					return true;
				});
			});
		}
	}
	
	private ArrayList<Preference> getPreferenceList(Preference p, ArrayList<Preference> list)
	{
		if( p instanceof PreferenceCategory || p instanceof PreferenceScreen) {
			PreferenceGroup pGroup = (PreferenceGroup) p;
			int pCount = pGroup.getPreferenceCount();
			for(int i = 0; i < pCount; i++) {
				getPreferenceList(pGroup.getPreference(i), list); // recursive call
			}
		} else {
			list.add(p);
		}
		return list;
	}
}