package org.nebobrod.schulteplus.ui.basics;

import static org.nebobrod.schulteplus.Utils.getRes;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.R;

import java.util.ArrayList;


public class BasicSettings extends PreferenceFragmentCompat {
	ArrayList<Preference> exerciseTypes = new ArrayList<>();
	androidx.preference.CheckBoxPreference chosen;
	private ExerciseRunner runner = ExerciseRunner.getInstance(getContext());
	private String[] exTypes;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
		setPreferencesFromResource(R.xml.preferences_basics, rootKey);
//		PreferenceScreen screen = this.getPreferenceScreen();
		exTypes = getRes().getStringArray(R.array.ex_type);

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
			chosen = (androidx.preference.CheckBoxPreference) findPreference("gcb_adv_schulte_1_sequence");
			chosen.setChecked(true);
		}
		exType.setText(chosen.getKey());
		runner.setExType(exType.getText().toString()); // set to runner from invisible pref et
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		ExerciseRunner.savePreferences(null);
	}

	@Override
	public boolean onPreferenceTreeClick(@NonNull Preference preference)
	{
		// only for Group Check Boxes:
		if (exerciseTypes.contains(preference)) {
			chosen = (androidx.preference.CheckBoxPreference) preference;
			for (Preference p : exerciseTypes) {
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
}