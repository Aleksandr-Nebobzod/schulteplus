/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.sssr;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SssrSettings extends PreferenceFragmentCompat {
	/**
	 * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
	 * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
	 * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
	 *
	 * @param savedInstanceState If the fragment is being re-created from a previous saved state,
	 *                           this is the state.
	 * @param rootKey            If non-null, this preference fragment should be rooted at the
	 *                           {@link PreferenceScreen} with this key.
	 */
	@Override
	public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

	}
}
