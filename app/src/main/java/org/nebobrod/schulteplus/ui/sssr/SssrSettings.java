/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.sssr;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.overlayBadgedIcon;
import static org.nebobrod.schulteplus.common.Const.*;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.data.ExType;

import java.util.ArrayList;

public class SssrSettings extends PreferenceFragmentCompat {
	private static final String TAG = "SssrSettings";
	private ArrayList<Preference> exerciseTypeCheckBoxes = new ArrayList<>();
	private androidx.preference.CheckBoxPreference chosen;
	private ExerciseRunner runner = ExerciseRunner.getInstance();


	/**
	 * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
	 * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
	 * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
	 *
	 * @param savedInstanceState If the fragment is being re-created from a previous saved state,
	 *                           this is the state.
	 * @param rootKey            If non-null, this preference fragment should be rooted at the
	 *                           {@link PreferenceScreen} with this key.
	 */
	@Override
	public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
		getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

		setPreferencesFromResource(R.xml.preferences_sssr, rootKey);

		initiateExerciseTypes();
	}

	/**
	 * Called when the fragment is visible to the user and actively running.
	 * This is generally
	 * tied to {Activity.onResume} of the containing
	 * Activity's lifecycle.
	 */
	@Override
	public void onResume() {
		runner.loadPreference();
		EditTextPreference exType = findPreference("prf_ex_type");
		// to find which checkbox selected on the screen:
		for (Preference p: exerciseTypeCheckBoxes) {
			if (((androidx.preference.CheckBoxPreference) p).isChecked()) {
				chosen = (androidx.preference.CheckBoxPreference) p;
				break;
			}
		}
		// for safety
		if (null == chosen) {
			chosen = (androidx.preference.CheckBoxPreference) findPreference(KEY_PRF_EX_R1);
			chosen.setChecked(true);
		}
		exType.setText(chosen.getKey());
		runner.setExType(exType.getText().toString()); // set to runner from invisible EditTextPreference

		super.onResume();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param preference
	 */
	@Override
	public boolean onPreferenceTreeClick(@NonNull Preference preference) {
		// only for Group Check Boxes:
		if (exerciseTypeCheckBoxes.contains(preference)) {
			chosen = (androidx.preference.CheckBoxPreference) preference;
			for (Preference p : exerciseTypeCheckBoxes) {
				((androidx.preference.CheckBoxPreference) p).setChecked(false);
			}
			chosen.setChecked(true);
			EditTextPreference exType = findPreference("prf_ex_type");
			exType.setText(chosen.getKey().toString());
			runner.setExType(chosen.getKey().toString());
		}
		return super.onPreferenceTreeClick(preference);
	}

	/**
	 * Called when the Fragment is no longer resumed.  This is generally
	 * tied to {Activity#onPause() Activity.onPause} of the containing
	 * Activity's lifecycle.
	 */
	@Override
	public void onPause() {
		super.onPause();
		ExerciseRunner.savePreferences();
	}

	/**
	 * method provides functionality "Group of Checkboxes" directly in _preferences.xml -- layout
	 */
	private void initiateExerciseTypes(){
		ArrayList<Preference> list = getPreferenceList(getPreferenceScreen(), new ArrayList<Preference>());
		exerciseTypeCheckBoxes.clear();
		for (Preference p : list) {
			String pKey = p.getKey();
			// prefix "gcb_" of Preference means Group Check Box
			if (p instanceof androidx.preference.CheckBoxPreference && pKey.startsWith("gcb_")) {
				p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						chosen = (androidx.preference.CheckBoxPreference) preference;
						return false;
					}
				});
				exerciseTypeCheckBoxes.add(p);
			}

			// Setting badge
			ExType exType = ExerciseRunner.getExTypes().get(pKey);
			if (exType != null && exType.getStatus() == ExType.FUNC_STATUS_PLANNED) {
				Drawable icon = p.getIcon();
				p.setIcon(overlayBadgedIcon(icon, getRes().getDrawable(R.drawable.ic_badge_inprogress, null)));
				p.setEnabled(false);
			}
		}
	}

	/**
	 * Recursive filling preferences,
	 * @param p starting from root (i.e. Pref.Screen)
	 * @param list
	 * @return
	 */
	private ArrayList<Preference> getPreferenceList(Preference p, ArrayList<Preference> list) {
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
