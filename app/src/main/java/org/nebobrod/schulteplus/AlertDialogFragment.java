package org.nebobrod.schulteplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author Andrius Baruckis http://www.baruckis.com
 *
 */
public final class AlertDialogFragment extends DialogFragment {

	public static final String ALERT_DIALOG_ICON_KEY = "alert_dialog_icon_key";
	public static final String ALERT_DIALOG_TITLE_KEY = "alert_dialog_title_key";
	public static final String ALERT_DIALOG_MESSAGE_KEY = "alert_dialog_message_key";
	public static final String ALERT_DIALOG_BUTTON_KEY = "alert_dialog_button_key";

	public static final String DIALOG_FRAGMENT_TAG = "dialog_fragment_tag";

	public static AlertDialogFragment newInstance(int iconResourceId,
												  CharSequence titleText, CharSequence messageText,
												  CharSequence buttonText) {
		AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(ALERT_DIALOG_ICON_KEY, iconResourceId);
		arguments.putCharSequence(ALERT_DIALOG_TITLE_KEY, titleText);
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
