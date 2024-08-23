/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.nebobrod.schulteplus.R;


public class PrefsPopupFragment extends AppCompatDialogFragment {
	public static final String TAG = "PrefsPopupSettingsFragment";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
//		setStyle(AppCompatDialogFragment.STYLE_NORMAL, R.style.preferenceScreen);
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// return inflater.inflate(R.layout.fragment_popup_settings, container, false);
		View view = inflater.inflate(R.layout.fragment_popup_settings, container, false);


/*		if (view != null) {
			view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackground));
		}*/

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
			// Get data from MainActivity ша PrefsChoiceFragment
			Fragment fragment;
			try {
				// Bottom PrefsChoiceFragment
				String fragmentClassName = getArguments().getString("fragment_class", PrefsSettingsFragment.class.getName());
				fragment = (Fragment) Class.forName(fragmentClassName).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				// Top settings menu
				fragment = new PrefsSettingsFragment();
			}

			fragment.setArguments(getArguments());
			getChildFragmentManager()
					.beginTransaction()
					.add(R.id.content, fragment)
					.commit();
		}
	}

	/** For child fragments */
	public void closeFromChild() {
		dismiss();
	}
}

