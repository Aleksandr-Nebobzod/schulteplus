package org.nebobrod.schulteplus.ui.basics;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import org.nebobrod.schulteplus.R;


public class BasicSettings extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.root_preferences, rootKey);
	}
}