/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.common.Const.*;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

import java.util.Objects;


public class PrefsPopupSettingsFragment extends AppCompatDialogFragment {
	public static final String TAG = "PrefsPopupSettingsFragment";
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// return inflater.inflate(R.layout.fragment_popup_settings, container, false);
		View view = inflater.inflate(R.layout.fragment_popup_settings, container, false);

		if (view != null) {
			view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackground));
		}

		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new BottomSheetDialog(getActivity(), getTheme());
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState == null) {
			PreferenceFragment fragment = new PreferenceFragment();
			fragment.setArguments(getArguments());
			getChildFragmentManager()
					.beginTransaction()
					.add(R.id.content, fragment)
					.commit();
		}
	}

	public static class PreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
		@Override
		public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

			addPreferencesFromResource(R.xml.menu_preferences);
			getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
			getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
			Objects.requireNonNull(getPreferenceScreen().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public boolean onPreferenceTreeClick(@NonNull Preference preference) {
			Activity activity = getActivity();
			if (activity == null) {
				Log.w("PreferenceFragment", "Activity is null");
				return false;
			}
			switch (preference.getKey()) {
				case "prf_show_intro":
					if (((SwitchPreference)preference).isChecked()) {
						((SwitchPreference)preference).setChecked(true);
						ExerciseRunner.setShowIntro(true);
						ExerciseRunner.setShownIntros(0);
						ExerciseRunner.savePreferences();
					}
					return false; // makes not necessary of break;
					// -- this FALSE is really important to prevent self-circled job


				case "prf_user_logoff":
					FirebaseAuth.getInstance().signOut();
					getActivity().finishAndRemoveTask();
					return true; // makes not necessary of break;

				case "prf_user_delete":

					// Confirmation
					Snackbar.make(getView(), getRes().getString(R.string.msg_proceed_to_password_reentry), Snackbar.LENGTH_INDEFINITE)
							.setAction(getRes().getString(R.string.lbl_ok), view -> {
								// fill with extras to avoid retyping on Login
								Intent intent = new Intent(getActivity(), LoginActivity.class);
								intent.putExtra("email", ExerciseRunner.getUserHelper().getEmail());
//									FirebaseAuth.getInstance().signOut();
								startActivity(intent);
							})
							.show();
					return true; // makes not necessary of break;

				default:
					return super.onPreferenceTreeClick(preference);
			}
		}

		@Override
		public void onResume() {
			super.onResume();
			// Set up a listener whenever a key changes
			Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
					.registerOnSharedPreferenceChangeListener(this);

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

		@Override
		public void onPause() {
			super.onPause();
			// Unregister the listener whenever a key changes
			Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
					.unregisterOnSharedPreferenceChangeListener(this);
		}

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

			String oldValue;
			String newValue;
			String checkResult;

			switch (key) {
				// integer values assignment
				case KEY_POINTS:
				case KEY_HOURS:
				case KEY_PRF_LEVEL:
				case KEY_PRF_SHOWN_INTROS:
					Log.d(TAG, "updatePreference: int value: " + ((EditTextPreference) getPreferenceScreen().findPreference(KEY_PRF_SHOWN_INTROS)).getText());
					preference.setSummary("" + sharedPrefs.getInt(key, 0));

					// Update Seekbar range
					SeekBarPreference sbPrfCurrentLevel;
					int currentLevel;

					currentLevel = (getPreferenceScreen().getSharedPreferences().getInt(KEY_PRF_LEVEL, 0));
					sbPrfCurrentLevel = getPreferenceScreen().findPreference(KEY_PRF_CURRENT_LEVEL);

					if (currentLevel < sbPrfCurrentLevel.getValue()){
						sbPrfCurrentLevel.setValue(currentLevel);
					}
					sbPrfCurrentLevel.setMax(currentLevel);
					sbPrfCurrentLevel.setTitle(R.string.prf_current_level_title );
					break;

				// boolean values assignment
				case KEY_PRF_SHOW_INTRO:
					((SwitchPreference)preference).setChecked((sharedPrefs.getBoolean(key, true)));
					break;

				// String values assignment
				case KEY_USER_NAME:
					oldValue = ExerciseRunner.userName;
					newValue = ((EditTextPreference) preference).getText();
					if (newValue == null) {
						((EditTextPreference) preference).setText(oldValue);
						break;
					}
					checkResult = validateName(newValue);
					if (checkResult.equals("")) {
						ExerciseRunner.loadPreference();
						ExerciseRunner.getUserHelper().setName(ExerciseRunner.userName);
						ExerciseRunner.updateUserHelper();
						/** we leave old records but future records market with new name
						 *  (to avoid enormous data traffic) -- BE CONSCIOUS!!! */
						// updateNameInHistory(ExerciseRunner.getUserHelper().getUid(), newValue);
						Toast.makeText(requireActivity(), getRes().getString(R.string.msg_username_updated) + ": " + ExerciseRunner.userName, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(requireActivity(), checkResult, Toast.LENGTH_LONG).show();
						((EditTextPreference) preference).setText(oldValue);
						// preference.setSummary(ExerciseRunner.getUserHelper().getName());
					}
					break;
				case KEY_USER_EMAIL:
					// preference.setSummary(sharedPrefs.getString(key, "-"));
					oldValue = ExerciseRunner.userEmail;
					newValue = ((EditTextPreference) preference).getText();
					if (newValue == null) {
						((EditTextPreference) preference).setText(oldValue);
						break;
					}
					checkResult = validateEmail(newValue);
					if (checkResult.equals("")) {
						ExerciseRunner.loadPreference();
						ExerciseRunner.getUserHelper().setEmail(ExerciseRunner.userEmail);
						ExerciseRunner.updateUserHelper();

						/** leaveHereSomeFlag() which should be checked in Splash for
						 forwarding user to Login for re-authenticate */
						Toast.makeText(requireActivity(), getRes().getString(R.string.msg_user_email_will_change) + ExerciseRunner.userName, Toast.LENGTH_LONG).show();
					} else {
						((EditTextPreference) preference).setText(oldValue);
						Toast.makeText(requireActivity(), checkResult, Toast.LENGTH_LONG).show();
					}
					break;
				default:
					break;
			}
		}
	}

	private static String validateName(String val) {
		if (!val.matches(NAME_REG_EXP)) {
			return (getRes().getString(R.string.msg_username_rules));
		} else {
			return "";
		}
	}
	private static String validateEmail(String val) {
		if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
			return (getRes().getString(R.string.msg_email_pattern));
		} else {
			return "";
		}
	}

	/** Update the whole history records ov the user
	 * (this is maybe for future) */
	private static void updateNameInHistory(String uid, String newName) {

		// Replace userdata in central repository
		DataFirestoreRepo<Achievement> achRepo = new DataFirestoreRepo<>(Achievement.class);
		Task<Void> taskAch = achRepo.unpersonilise(uid, newName).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					Log.i(TAG, "onComplete: Achievements rewritten");
				} else {
					Log.w(TAG, "onComplete: Achievements rewriting error " + task.getException().getLocalizedMessage());
				}
			}
		});

		DataFirestoreRepo<ExResult> exResRepo = new DataFirestoreRepo<>(ExResult.class);
		Task<Void> taskExRes = exResRepo.unpersonilise(uid, newName).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					Log.i(TAG, "onComplete: ExResult rewritten");
				} else {
					Log.w(TAG, "onComplete: ExResult rewriting error " + task.getException().getLocalizedMessage());
				}
			}
		});
	}
}

