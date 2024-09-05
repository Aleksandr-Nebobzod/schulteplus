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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.nebobrod.schulteplus.R;

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
			getDialog().getWindow().clearFlags(
//					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
//					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			);
		}

		mEditor.setVerticalScrollBarEnabled(true);
		mEditor.setEditorHeight(250);
//		mEditor.setEditorFontSize(22);
//		mEditor.setEditorFontColor(Color.RED);
//		mEditor.setEditorBackgroundColor(Color.BLUE);
		//mEditor.setBackgroundColor(Color.BLUE);
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
					mEditor.setBold();
				}
			});

			view.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setItalic();
				}
			});

			view.findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setSubscript();
				}
			});

			view.findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setSuperscript();
				}
			});

			view.findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setStrikeThrough();
				}
			});

			view.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setUnderline();
				}
			});

			view.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setBullets();
				}
			});

			view.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mEditor.setNumbers();
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


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// Restrict occasional closing
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
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
		}, 200);

		// Show keyboard
		Dialog dialog = getDialog();
		if (dialog != null && dialog.getWindow() != null) {
			getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE); // SOFT_INPUT_STATE_VISIBLE

			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(mEditor, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	// Set Ok listener
	public void setOnNoteEditedListener(OnNoteEditedListener listener) {
		this.listener = listener;
	}
}

