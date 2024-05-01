/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.SeekBarPreference;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import static org.nebobrod.schulteplus.Utils.showSnackBarConfirmation;
import static org.nebobrod.schulteplus.common.Const.*;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.R;

public class PrefsPopupSettingsFragment extends AppCompatDialogFragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_popup_settings, container, false);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new BottomSheetDialog(getActivity(), getTheme());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			Fragment fragment = Fragment.instantiate(getActivity(),
					PreferenceFragment.class.getName(), getArguments());
			getChildFragmentManager()
					.beginTransaction()
					.add(R.id.content, fragment)
					.commit();
		}
	}

	public static class PreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
		@Override
		public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
			SeekBarPreference sbPrfCurrentLevel;
			int currentLevel;

			addPreferencesFromResource(R.xml.menu_preferences);

			getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
			getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

			currentLevel = (getPreferenceScreen().getSharedPreferences().getInt(KEY_PRF_LEVEL, 0));
			sbPrfCurrentLevel = getPreferenceScreen().findPreference(KEY_PRF_CURRENT_LEVEL);

			if (currentLevel < sbPrfCurrentLevel.getValue()){
				sbPrfCurrentLevel.setValue(currentLevel);
			}
			sbPrfCurrentLevel.setMax(currentLevel);
			sbPrfCurrentLevel.setTitle(R.string.prf_current_level_title );
		}

		@Override
		public boolean onPreferenceTreeClick(@NonNull Preference preference) {
			switch (preference.getKey()) {
				case "prf_user_logoff":
					FirebaseAuth.getInstance().signOut();
					getActivity().finishAndRemoveTask();
					return true; // makes not necessary of break;
				case "prf_user_delete":
					showSnackBarConfirmation(getActivity(), String.valueOf(R.string.msg_proceed_to_password_reentry), new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							// fill with extras to avoid retyping on Login
							Intent intent = new Intent(getActivity(), LoginActivity.class);
							intent.putExtra("email", ExerciseRunner.getUserHelper().getEmail());
							FirebaseAuth.getInstance().signOut();
							startActivity(intent);
						}
					});
					getActivity().finishAndRemoveTask();
					return true; // makes not necessary of break;
				default:
					return super.onPreferenceTreeClick(preference);
			}
		}

		@Override
		public void onResume() {
			super.onResume();
/*			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);*/

			// This maybe redundant... nope -- registered listeners don't work
			for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
				Preference preference = getPreferenceScreen().getPreference(i);
				if (preference instanceof PreferenceGroup) {
					PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
					for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
						Preference singlePref = preferenceGroup.getPreference(j);
						updatePreference(singlePref, singlePref.getKey());
					}
				} else {
					updatePreference(preference, preference.getKey());
				}
			}
		}

/*		@Override
		public void onPause() {
			super.onPause();
			// Unregister the listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}*/

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			updatePreference(findPreference(key), key);
		}

		private void updatePreference(Preference preference, String key) {
			if (preference == null) return;
			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				listPreference.setSummary(listPreference.getEntry());
				return;
			}
			SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

			switch (key) {
				// integer values assignment
				case "prf_points":
				case "prf_hours":
				case "prf_level":
					preference.setSummary("" + sharedPrefs.getInt(key, 0));
					break;

				// String values assignment
				case "prf_user_name":
				case "prf_user_email":
					preference.setSummary(sharedPrefs.getString(key, "-"));
					break;
				default:
					break;
			}
		}
	}
}

