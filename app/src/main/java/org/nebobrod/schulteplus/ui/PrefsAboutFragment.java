package org.nebobrod.schulteplus.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.util.prefs.Preferences;

/**
 * This fragment shows the preferences for the second header.
 */
public class PrefsAboutFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Can retrieve arguments from headers XML.
//		Log.i("args", "Arguments: " + getArguments());

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.menu_about_pref);

		getPreferenceManager()
				.findPreference("prf_menu_about_url")
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						try {
							Utils.openWebPage(getResources().getString(R.string.src_menu_about_url), getContext());
							return true;
						} catch (Exception e) {
							return false;
						}
					}
				});
	}
}
