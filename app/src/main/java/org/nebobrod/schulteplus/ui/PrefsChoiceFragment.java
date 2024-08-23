/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.common.Const.KEY_PFR_EXERCISE_SPACE;
import static org.nebobrod.schulteplus.common.Const.KEY_SPACE_01_SCHULTE;
import static org.nebobrod.schulteplus.common.Const.KEY_SPACE_02_BASICS;
import static org.nebobrod.schulteplus.common.Const.KEY_SPACE_03_SSSR;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.ui.basics.BasicSettings;
import org.nebobrod.schulteplus.ui.home.HomeFragment;
import org.nebobrod.schulteplus.ui.schulte.SchulteSettings;
import org.nebobrod.schulteplus.ui.sssr.SssrSettings;

import java.util.ArrayList;


public class PrefsChoiceFragment extends PreferenceFragmentCompat {
	ArrayList<Preference> exerciseTypes = new ArrayList<>();
	androidx.preference.CheckBoxPreference chosen;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
		setPreferencesFromResource(R.xml.preferences_choice, rootKey);

		initiateExerciseTypes();
	}
	@Override
	public void onResume() {
		ExerciseRunner.loadPreference();
		EditTextPreference exSpace = findPreference(KEY_PFR_EXERCISE_SPACE);
		// to find which checkbox selected on the screen:
		for (Preference p: exerciseTypes) {
			if (((androidx.preference.CheckBoxPreference) p).isChecked()) {
				chosen = (androidx.preference.CheckBoxPreference) p;
				break;
			}
		}
		if (null == chosen) {
			chosen = (androidx.preference.CheckBoxPreference) findPreference(KEY_SPACE_01_SCHULTE);
			chosen.setChecked(true);
		}
		exSpace.setText(chosen.getKey());
		ExerciseRunner.setExSpace(exSpace.getText()); // set to runner from invisible pref et

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
			EditTextPreference exType = findPreference(KEY_PFR_EXERCISE_SPACE);
			exType.setText(chosen.getKey());
			ExerciseRunner.setExType(chosen.getKey());

			// Reload new fragment on UI
			updateFragmentForSpace(chosen.getKey());
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
			// prefix "@string/gcb_" of Preference means Group Check Box
			if (p instanceof androidx.preference.CheckBoxPreference && p.getKey().startsWith("gcb_")) {
				p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						chosen = (androidx.preference.CheckBoxPreference) preference;
						return false;
					}
				});
				exerciseTypes.add(p);
			}
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

	private void updateFragmentForSpace(String chosenSpace) {

//		((PrefsPopupFragment) getParentFragment()).closeFromChild();// Close current dialog fragment

		NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);

		// Run fragment selected by check box
		switch (chosenSpace) {
			case KEY_SPACE_01_SCHULTE:
				navController.navigate(R.id.navigation_schulte_parents);
				break;
			case KEY_SPACE_02_BASICS:
				navController.navigate(R.id.navigation_basics);
				break;
			case KEY_SPACE_03_SSSR:
				navController.navigate(R.id.navigation_sssr);
				break;
			default:
				//no op
				break;
		}

		// Change current fragment by the new one
/*		Fragment selectedFragment;

		switch (chosenSpace) {
			case KEY_SPACE_01_SCHULTE:
				selectedFragment = new SchulteSettings();
				break;
			case KEY_SPACE_02_BASICS:
				selectedFragment = new BasicSettings();
				break;
			case KEY_SPACE_03_SSSR:
				selectedFragment = new PrefsPopupFragment();
				break;
			default:
				selectedFragment = this; // fro safety
				break;
		}

		getActivity().getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.nav_host_fragment_activity_main, selectedFragment)
				.commit();*/
	}

}