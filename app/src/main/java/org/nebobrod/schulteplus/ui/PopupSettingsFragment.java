package org.nebobrod.schulteplus.ui;

import static android.preference.PreferenceManager.getDefaultSharedPreferencesName;
import static androidx.core.app.ActivityCompat.finishAffinity;
import static org.nebobrod.schulteplus.Const.*;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.MainActivity;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

public class PopupSettingsFragment extends AppCompatDialogFragment
{

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

	public static class PreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
	{
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
//					finishAndRemoveTask();
					MainActivity.getInstance().finishAndRemoveTask();
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

