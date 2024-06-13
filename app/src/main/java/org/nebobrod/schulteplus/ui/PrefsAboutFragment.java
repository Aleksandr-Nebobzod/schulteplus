/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.Log;

/**
 * This fragment shows the preferences for the second header.
 */
public class PrefsAboutFragment extends PreferenceFragmentCompat {
	private final String TAG = "PrefsAboutFragment";

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

		// Go to web site
		getPreferenceManager()
				.findPreference("prf_menu_about_url")
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						try {
							Utils.openWebPage(getResources().getString(R.string.src_menu_about_url), getContext());
							return true;
						} catch (Exception e) {
							Log.e(TAG, "web page opening", e);
							return false;
						}
					}
				});

		// Show License info
		getPreferenceManager()
				.findPreference("prf_license")
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						try {
							Utils.displayHtmlAlertDialog(requireActivity(), R.string.str_about_license_html_source);
							return true;
						} catch (Exception e) {
							Log.e(TAG, "license dialog opening", e);
							return false;
						}
					}
				});

		// Show user agreement info
		getPreferenceManager()
				.findPreference("prf_user_agreement")
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						try {
							Utils.displayHtmlAlertDialog(requireActivity(), R.string.str_about_user_agreement_html_source);
							return true;
						} catch (Exception e) {
							Log.e(TAG, "user_data_policy dialog opening", e);
							return false;
						}
					}
				});

		// Show user_data_policy info
		getPreferenceManager()
				.findPreference("prf_user_data_policy")
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						try {
							Utils.displayHtmlAlertDialog(requireActivity(), R.string.str_about_user_data_policy_html_source);
							return true;
						} catch (Exception e) {
							Log.e(TAG, "user_data_policy dialog opening", e);
							return false;
						}
					}
				});

		// Call OS list from library play-services-oss-licenses
		getPreferenceManager()
				.findPreference("prf_about_oss")
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						try {
							GoogleApiAvailability gApiAvai = new GoogleApiAvailability();
							if (ConnectionResult.SUCCESS == gApiAvai.isGooglePlayServicesAvailable(requireActivity())) {
								startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class));
							} else {
								Toast.makeText(requireActivity(), R.string.msg_no_google_play_services_installed , Toast.LENGTH_SHORT).show();
							}

							return true;
						} catch (Exception e) {
							Log.e(TAG, "OSS activity opening", e);
							return false;
						}
					}
				});
	}
}
