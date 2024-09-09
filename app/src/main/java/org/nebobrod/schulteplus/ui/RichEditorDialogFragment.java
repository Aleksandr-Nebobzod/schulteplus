/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Log;

import jp.wasabeef.richeditor.RichEditor;

public class RichEditorDialogFragment extends DialogFragment {
	private static final String ARG_TEXT = "arg_text";

	public interface OnNoteEditedListener {
		void onNoteEdited(String newNote);
	}

	private RichEditor mEditor;
	private Button btnCancel;
	private Button btnOk;
	private OnNoteEditedListener listener;

	public static RichEditorDialogFragment newInstance(String text) {
		RichEditorDialogFragment fragment = new RichEditorDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TEXT, text);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// Restrict occasional closing
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_rich_editor, container, false);

		mEditor = view.findViewById(R.id.rich_editor);
		btnCancel = view.findViewById(R.id.btn_cancel);
		btnOk = view.findViewById(R.id.btn_ok);

		btnCancel.setOnClickListener(view1 -> dismiss());

		btnOk.setOnClickListener(v -> {
			String newNote = mEditor.getHtml(); // Get formatted text
			if (listener != null) {
				listener.onNoteEdited(newNote); // Send the note through the interface
			}
			dismiss();
		});

		// Get income text an set it
		if (getArguments() != null) {
			String text = getArguments().getString(ARG_TEXT, "");
			mEditor.setHtml(text);
		}

		// Rich Editor Settings:
		if (getDialog() != null && getDialog().getWindow() != null) {

			// Clear FLAG_HARDWARE_ACCELERATED for this Dialog window
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		mEditor.setVerticalScrollBarEnabled(true);
		mEditor.setEditorHeight(250); // Less than layout_height="300dp"
//		mEditor.setEditorFontSize(22);
//		mEditor.setEditorFontColor(Color.RED);
//		mEditor.setEditorBackgroundColor(Color.BLUE);
//		mEditor.setBackgroundColor(Color.BLUE);
//		mEditor.setBackgroundResource(R.drawable.ic_border_thick);
		mEditor.setPadding(10, 10, 10, 50);
//		mEditor.setPlaceholder(getString(R.string.txt_note_hint));
		mEditor.setInputEnabled(true);

		// Listeners
		{
			view.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.undo();
				}
			});

			view.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.redo();
				}
			});

			view.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					mEditor.clearFocusEditor();
					mEditor.setBold();
					toggle(v);
				}
			});

			view.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setItalic();
					toggle(v);
				}
			});

			view.findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setSubscript();
					toggle(v);
				}
			});

			view.findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setSuperscript();
					toggle(v);
				}
			});

			view.findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setStrikeThrough();
					toggle(v);
				}
			});

			view.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setUnderline();
					toggle(v);
				}
			});

			view.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setBullets();
					toggle(v);
				}
			});

			view.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setNumbers();
					toggle(v);
				}
			});

			view.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
				}
			});
		} // Listeners

		return view;
	}

	/**
	 * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
	 * has returned, but before any saved state has been restored in to the view.
	 * This gives subclasses a chance to initialize themselves once
	 * they know their view hierarchy has been completely created.  The fragment's
	 * view hierarchy is not however attached to its parent at this point.
	 *
	 * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed
	 *                           from a previous saved state as given here.
	 */
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Get focus
		mEditor.setFocusable(true);
		mEditor.setFocusableInTouchMode(true);
		mEditor.requestFocus();

		// Set focus to beginning of text
		mEditor.postDelayed(() -> {
			mEditor.focusEditor();

			// Show keyboard
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(mEditor, InputMethodManager.SHOW_IMPLICIT);
		}, 200);

		// Regularly check cursor status
		Handler handler = new Handler(Looper.getMainLooper());
		Runnable checkFormatRunnable = new Runnable() {
			@Override
			public void run() {

				if (isAdded() && getActivity() != null) {
					mEditor.evaluateJavascript("document.queryCommandState('bold')", value -> {
						if (Boolean.parseBoolean(value)) {
							ImageButton ibFormat = ((ImageButton) view.findViewById(R.id.action_bold));
							ibFormat.getDrawable().setTint(getResources().getColor(R.color.light_grey_F_blue, null));
							ibFormat.setTag("ON");
						}
					});

					mEditor.evaluateJavascript("document.queryCommandState('italic')", value -> {
						boolean isItalic = Boolean.parseBoolean(value);
						if (Boolean.parseBoolean(value)) {
							ImageButton ibFormat = view.findViewById(R.id.action_italic);
							ibFormat.getDrawable().setTint(getResources().getColor(R.color.light_grey_F_blue, null));
							ibFormat.setTag("ON");
						}
					});

					mEditor.evaluateJavascript("document.queryCommandState('strikethrough')", value -> {
						boolean isItalic = Boolean.parseBoolean(value);
						if (Boolean.parseBoolean(value)) {
							ImageButton ibFormat = view.findViewById(R.id.action_strikethrough);
							ibFormat.getDrawable().setTint(getResources().getColor(R.color.light_grey_F_blue, null));
							ibFormat.setTag("ON");
						}
					});

					mEditor.evaluateJavascript("document.queryCommandState('underline')", value -> {
						boolean isItalic = Boolean.parseBoolean(value);
						if (Boolean.parseBoolean(value)) {
							ImageButton ibFormat = view.findViewById(R.id.action_underline);
							ibFormat.getDrawable().setTint(getResources().getColor(R.color.light_grey_F_blue, null));
							ibFormat.setTag("ON");
						}
					});
				}

				handler.postDelayed(this, 1000); // Check again after a second
			}
		};
		handler.post(checkFormatRunnable);

		// Set 90% of width
		Dialog dialog = getDialog();
		if (dialog != null && dialog.getWindow() != null) {
			WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
			layoutParams.copyFrom(getDialog().getWindow().getAttributes());
			layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9); // 90% ширины экрана
			layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
			getDialog().getWindow().setAttributes(layoutParams);
		}
	}

	/** After everything is ready */
	@Override
	public void onStart() {
		super.onStart();
	}

	// Set Ok listener
	public void setOnNoteEditedListener(OnNoteEditedListener listener) {
		this.listener = listener;
	}

	/** Toggles button by Tag and Tint of drawing */
	private void toggle(View view) {
		if (!(view instanceof ImageView)) {
			return; // only for ImageButton and other extenders of ImageView
		}
		if ("ON".equals(view.getTag())) {	// First is always not "ON" (null)
			view.setTag("OFF");
			((ImageView) view).getDrawable().setTint(getResources().getColor(R.color.light_grey_6, null));
		} else {
			view.setTag("ON");
			((ImageView) view).getDrawable().setTint(getResources().getColor(R.color.light_grey_F_blue, null));
		}
	}
}

