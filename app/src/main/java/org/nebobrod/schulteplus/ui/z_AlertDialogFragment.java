/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrius Baruckis http://www.baruckis.com
 *
 */
public final class z_AlertDialogFragment extends DialogFragment {

	public static final String TAG = "dialog_fragment_tag";
	public static final String ALERT_DIALOG_ICON_KEY = "alert_dialog_icon_key";
	public static final String ALERT_DIALOG_TITLE_KEY = "alert_dialog_title_key";
	public static final String ALERT_DIALOG_MAP_KEY = "alert_dialog_map_key";
	public static final String ALERT_DIALOG_MESSAGE_KEY = "alert_dialog_message_key";
	public static final String ALERT_DIALOG_BUTTON_KEY = "alert_dialog_button_key";

	static DialogInterface.OnClickListener okListener;
	static DialogInterface.OnClickListener cancelListener;


	public static z_AlertDialogFragment newInstance(int iconResourceId,
													CharSequence titleText,
													@Nullable Map<String, String> stringMap,
													CharSequence messageText,
													CharSequence buttonText,
													@Nullable DialogInterface.OnClickListener okListener,
													@Nullable DialogInterface.OnClickListener cancelListener) {
		z_AlertDialogFragment alertDialogFragment = new z_AlertDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(ALERT_DIALOG_ICON_KEY, iconResourceId);
		arguments.putCharSequence(ALERT_DIALOG_TITLE_KEY, titleText);
		arguments.putString(ALERT_DIALOG_MAP_KEY, new Gson().toJson(stringMap));
		arguments.putCharSequence(ALERT_DIALOG_MESSAGE_KEY,
				messageText);
		arguments
				.putCharSequence(ALERT_DIALOG_BUTTON_KEY, buttonText);
		alertDialogFragment.setArguments(arguments);
		return alertDialogFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// We always create dialog from stored arguments.
		// This allows us to avoid problems on device screen rotation.
		int iconResourceId = getArguments().getInt(
				ALERT_DIALOG_ICON_KEY);
		CharSequence titleText = getArguments().getCharSequence(
				ALERT_DIALOG_TITLE_KEY);
		Map<String, String> stringMap = new Gson().fromJson(getArguments().getString(
				ALERT_DIALOG_MAP_KEY), new TypeToken<HashMap<String, String>>(){}.getType());
		CharSequence messageText = getArguments().getCharSequence(
				ALERT_DIALOG_MESSAGE_KEY);
		CharSequence buttonText = getArguments().getCharSequence(
				ALERT_DIALOG_BUTTON_KEY);

		return new AlertDialog.Builder(getActivity())
				.setIcon(iconResourceId)
				.setTitle(titleText)
				.setMessage(messageText)
				.setPositiveButton(buttonText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
							}
						}).create();
	}
}
