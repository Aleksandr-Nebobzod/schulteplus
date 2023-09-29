package org.nebobrod.schulteplus.ui;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

public class PopupSettingsFragment extends AppCompatDialogFragment {


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

	public static class PreferenceFragment extends PreferenceFragmentCompat {
		// Preference Keys
		public static final String KEY_PRF_LEVEL = "prf_level";
		public static final String KEY_PRF_CURRENT_LEVEL = "prf_current_level";
		// Shared preference
		SharedPreferences sharedPreferences;

		@Override
		public void onCreatePreferences(Bundle bundle, String s) {
			addPreferencesFromResource(R.xml.menu_preferences);

			SeekBarPreference prfCurrentLevel;
			int currentLevel;

			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			currentLevel = Utils.intFromString(sharedPreferences.getString(KEY_PRF_LEVEL, "1"));
			prfCurrentLevel = getPreferenceScreen().findPreference(KEY_PRF_CURRENT_LEVEL);

			if (currentLevel < prfCurrentLevel.getValue()){
				prfCurrentLevel.setValue(currentLevel);
			}
			prfCurrentLevel.setMax(currentLevel);
			prfCurrentLevel.setTitle(R.string.prf_current_level_title );

		}

	}
}

