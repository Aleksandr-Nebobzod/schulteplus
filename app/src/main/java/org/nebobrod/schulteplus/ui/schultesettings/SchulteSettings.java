package org.nebobrod.schulteplus.ui.schultesettings;

import static org.nebobrod.schulteplus.Utils.getRes;

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

public class SchulteSettings extends PreferenceFragmentCompat {
	// TODO: 28.09.2023 this and BasicSettings class are mostly same (think how to unify them) 
	ArrayList<Preference> exerciseTypes = new ArrayList<>();
	androidx.preference.CheckBoxPreference chosen;
	private ExerciseRunner runner = ExerciseRunner.getInstance(getContext());
	private String[] exTypes;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
		setPreferencesFromResource(R.xml.preferences_schulte, rootKey);
		PreferenceScreen screen = this.getPreferenceScreen();
		exTypes = getRes().getStringArray(R.array.ex_type);

		initiateExerciseTypes();
	}

	private void updatePrefScreen(){
		runner.loadPreference(getContext());
		EditTextPreference exType = findPreference("prf_ex_type");

		// Find which checkbox of "group" is selected on the screen:
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
		runner.setExType(exType.getText().toString()); // set exType to runner from invisible pref et
		switch (chosen.getKey()){	// disable options PreferenceCategory for hard levels
			case "gcb_schulte_1_sequence":
				findPreference("prf_cat_options").setEnabled(true);
				break;
			default:
				findPreference("prf_cat_options").setEnabled(false);
		}
		// set X & Y to runner
		switch (chosen.getKey()){
			case "gcb_schulte_1_sequence":
				runner.setX((byte) ((androidx.preference.SeekBarPreference) findPreference("prf_x_size")).getValue());
				runner.setY((byte) ((androidx.preference.SeekBarPreference) findPreference("prf_y_size")).getValue());
				break;
			case "gcb_schulte_2_sequences":
				runner.setX((byte) 7);
				runner.setY((byte) 7);
				break;
			case "gcb_schulte_3_sequences":
				runner.setX((byte) 10);
				runner.setY((byte) 10);
				break;
			default:
				runner.setX((byte) 5);
				runner.setY((byte) 5);
		}
		// set hinted to runner
		runner.setHinted(((androidx.preference.SwitchPreference) findPreference("prf_sw_hints")).isChecked());

	}
	@Override
	public void onResume() {
		super.onResume();
		updatePrefScreen();
	}

	@Override
	public void onPause() {
		super.onPause();
		updatePrefScreen();
		ExerciseRunner.savePreferences(null);
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

			updatePrefScreen();
		}
		return super.onPreferenceTreeClick(preference);
	}


	/**
	 * method provides fnc Group of Checkboxes directly in _preferences.xml -- layout
	 */
	private void initiateExerciseTypes(){
		ArrayList<Preference> list = getPreferenceList(getPreferenceScreen(), new ArrayList<Preference>());
		exerciseTypes.clear();
		for (Preference p : list) {
			// prefix "gcb_" of Preference means Group Check Box
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
