/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.durationCut;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.timeStampToDateLocal;
import static org.nebobrod.schulteplus.Utils.timeStampToTimeLocal;
import static org.nebobrod.schulteplus.common.Const.LAYOUT_GROUP_FLAG;
import static org.nebobrod.schulteplus.common.Const.LAYOUT_HEADER_FLAG;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.ExerciseRunner;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides a List adapter in accordance with declared layout {@link ExResultArrayAdapter#textViewResourceId}
 * @return
 */
public class ExResultArrayAdapter extends ArrayAdapter<ExResult> {
	static int textViewResourceId  = R.layout.fragment_dashboard_elv_ex_result;
	String myUid;
	List<ExResult> items = new ArrayList<>();
//		static ArrayAdapter<List<ExResult>> exResultArrayAdapter;

	public ExResultArrayAdapter(Context context, List<ExResult> items, String uid) {
		super(context, textViewResourceId, items);
		myUid = uid;
//			this.items = items;
	}

	/*
			@Override
			public int getCount() {
				return (int) items.size();
			}

			@Nullable
			@Override
			public ExResult getItem(int position) {
				return items.get(position);
			}

			@Override
			public long getItemId(int position) {
				return items.get(position).id();
			}*/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater li = (LayoutInflater) getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.fragment_dashboard_elv_ex_result, null);
		}
		ExResult exResult = getItem(position);

		// Reset visibility for header and group header
		v.findViewById(R.id.ll_header).setVisibility(View.GONE);
		v.findViewById(R.id.tv_group_header).setVisibility(View.GONE);

		// Manage header and grouping
		switch (exResult.getLayoutFlag()) {
			case LAYOUT_HEADER_FLAG:
				v.findViewById(R.id.ll_header).setVisibility(View.VISIBLE);
			case LAYOUT_GROUP_FLAG:
				v.findViewById(R.id.tv_group_header).setVisibility(View.VISIBLE);
				fillText(v, R.id.tv_group_header, timeStampToDateLocal(exResult.getTimeStamp()));
				break;
			default:
				break;
		}

//			fillText(v, R.id.tv_flag, exResult.layoutFlag()); // for debug
		fillText(v, R.id.tv_num, "" + (position + 1));
		fillText(v, R.id.tv_time, timeStampToTimeLocal(exResult.getTimeStamp()));
		fillText(v, R.id.tv_duration, durationCut(exResult.getNumValue()));
		fillText(v, R.id.tv_events, exResult.getTurns() + "");

		if (!exResult.getUid().equals(myUid)) {
			exResult.setNote(exResult.getName());                                //  -- hide the Note from www list
//			exResult.setLevelOfEmotion(0);
//			exResult.setLevelOfEnergy(0);
		}
		fillText(v, R.id.tv_note, exResult.getNote());
		fillText(v, R.id.tv_note_full, exResult.getNote());
		fillText(v, R.id.tv_emotion, exResult.getLevelOfEmotion() + "");
		fillText(v, R.id.tv_energy, exResult.getLevelOfEnergy() + "");

		fillText(v, R.id.tv_special_mark, "*");

		// Make Note an expandable
		TextView clickableTextView = (TextView) v.findViewById(R.id.tv_note);
		TextView expandableTextView = (TextView) v.findViewById(R.id.tv_note_full);
		clickableTextView.setOnClickListener(view -> {
			if (expandableTextView.getVisibility() == View.GONE) {
				expandableTextView.setVisibility(View.VISIBLE);
			} else {
				expandableTextView.setVisibility(View.GONE);
			}

		});
		expandableTextView.setOnClickListener(view -> {
			expandableTextView.setVisibility(View.GONE);
		});

		return v;
	}

	private <T> void fillText(View v, int id, T text) {
		TextView textView = (TextView) v.findViewById(id);
		textView.setText((CharSequence) text);
	}


	/**
	 * <b>"OK"</b> means Continue or restart <p> when <b>"No"</b> means stop the parent Activity and exit.
	 * @param context1 really necessary as SchulteActivity02.this
	 * @param resultLiveData ExResult (for set of key-value Pairs to fill internal result table)
	 * @param strMessage Output below results and feedback elements
	 */
	public static void feedbackDialog(Context context1,
									  MutableLiveData<ExResult> resultLiveData,
									  String strMessage,
									  @Nullable DialogInterface.OnClickListener okListener,
									  @Nullable DialogInterface.OnClickListener cancelListener) {
//		AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context1);
		AlertDialog.Builder builder = new AlertDialog.Builder(context1, R.style.alertDialogTheme);
		ExResult resultClone;
		Map<String, String> resultsMap = null;

		if (resultLiveData != null) {
			resultClone = resultLiveData.getValue();
			resultsMap = resultClone.toMap();
		} else {
			resultClone =  new ExResult();
		}

		final FrameLayout frameView = new FrameLayout(context1);
		builder.setView(frameView);
		builder.setPositiveButton(Utils.getRes().getText(R.string.lbl_ok), null);

		final AlertDialog alertDialog = builder.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		alertDialog.getWindow().setDimAmount(0.2F);

		// Put the dialog layout to center of the screen
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.CENTER_VERTICAL;
//		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		// Variables
		LayoutInflater inflater = alertDialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_result_feedback, frameView);
		TextView txtTitle, txtMessage;
		TableLayout tb;
		TextView txtKey, txtValue, txtKeyNew, txtValueNew;
		TableRow tbRow, tbRowNew;
		TableLayout tbPsychometry;
		EditText etNote;
		SwitchCompat switchDataProvided;
		SeekBar sbEmotionalLevel, sbEnergyLevel;
		SeekBar.OnSeekBarChangeListener seekBarChangeListener;
		Button btnOk, btnCancel;	// These template buttons are invisible on  inflated layout
//		final Button[] btnRedesign = new Button[1];
		final MaterialButton[] btnRedesign = new MaterialButton[1];

		// Initiating controls
		txtTitle = layout.findViewById(R.id.txtTitle);
		tb = layout.findViewById(R.id.table_layout);
		tbRow = layout.findViewById(R.id.table_row);
		txtKey = layout.findViewById(R.id.tv_key1);
		txtValue = layout.findViewById(R.id.tv_value1);
		txtMessage = layout.findViewById(R.id.txtMessage);
		tbPsychometry = layout.findViewById(R.id.table_psychometry);
		etNote = layout.findViewById(R.id.et_note);
		switchDataProvided = layout.findViewById(R.id.sw_data_provided);
		sbEmotionalLevel = layout.findViewById(R.id.sb_emotion);
		sbEnergyLevel = layout.findViewById(R.id.sb_energy);
		btnCancel = layout.findViewById(R.id.btnCancel);
		btnOk = layout.findViewById(R.id.btnOK);

		txtTitle.setText(R.string.title_result);
		txtMessage.setText(Html.fromHtml(strMessage));

		if (resultsMap != null) {
			// Each result pair key-value put into textviews of new row
			for (Map.Entry<String, String> entry : resultsMap.entrySet()) {
				tbRowNew = new TableRow(context1);
				tbRowNew.setLayoutParams(tbRow.getLayoutParams());
				// Add textview Key to new table row
				txtKeyNew = new TextView(context1);
				txtKeyNew.setLayoutParams(txtKey.getLayoutParams());
				txtKeyNew.setText(entry.getKey());
				tbRowNew.addView(txtKeyNew);
				// Add textview Value to new table row
				txtValueNew = new TextView(context1);
				txtValueNew.setLayoutParams(txtValue.getLayoutParams());
				txtValueNew.setText(entry.getValue());
				tbRowNew.addView(txtValueNew);
				// Add new row
				tb.addView(tbRowNew);
			}
		} else {
			tbPsychometry.setVisibility(View.GONE);
		}
		// hide template row of table
		tbRow.setVisibility(View.GONE);

		// Prepare listeners
		DialogInterface.OnClickListener voidListener = (DialogInterface dialogInterface, int i) -> {
			// Means continue ex i.e. do nothing
			alertDialog.dismiss();
		};

		if (cancelListener == null) cancelListener = voidListener;
		if (okListener == null) okListener = voidListener;

		// Set the buttons
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, Utils.getRes().getText(R.string.lbl_ok), (DialogInterface.OnClickListener) okListener);
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, Utils.getRes().getText(R.string.lbl_no), (DialogInterface.OnClickListener)  cancelListener);

		// Copy design from templates
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				// redesign OK by template
				btnRedesign[0] = (MaterialButton) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				btnRedesign[0].setLayoutParams(btnOk.getLayoutParams());
//				btnRedesign[0].setBackground(AppCompatResources.getDrawable(context1, R.drawable.bg_button));
//				btnRedesign[0].setBackgroundColor(getRes().getColor(R.color.colorPrimaryVariant, null));
				btnRedesign[0].setBackgroundColor(getRes().getColor(R.color.light_grey_8, null));
				btnRedesign[0].setTextAppearance(R.style.button3d);
				btnRedesign[0].setAllCaps(false);
				btnRedesign[0].setWidth(btnOk.getWidth()-10);

				// redesign Cancel by template
				btnRedesign[0] = (MaterialButton)  alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				btnRedesign[0].setLayoutParams(btnCancel.getLayoutParams());
//				btnRedesign[0].setBackground(getRes().getDrawable(R.drawable.ic_border_thick, context1.getTheme()));
				btnRedesign[0].setBackgroundColor(getRes().getColor(R.color.light_grey_8, null));
				btnRedesign[0].setTextAppearance(R.style.button3d);
				btnRedesign[0].setAllCaps(false);
				btnRedesign[0].setWidth(btnCancel.getWidth()-10);
			}
		});

		// check if notes entered manually
		etNote.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable editable) {
				if (!editable.toString().equals("")) switchDataProvided.setChecked(true);
			}

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
		});

		// Switcher is the Flag for gathering entered data
		switchDataProvided.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (tbPsychometry.getVisibility() == View.GONE) return;

				// Update Views and LiveData
				sbEmotionalLevel.setThumbTintList(context1.getColorStateList(R.color.light_grey_A_green));
				sbEnergyLevel.setThumbTintList(context1.getColorStateList(R.color.light_grey_A_green));
				alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.lbl_no_ok);
				resultClone.setNote(etNote.getText().toString());
				resultClone.setLevelOfEmotion(sbEmotionalLevel.getProgress()-2);
				resultClone.setLevelOfEnergy(sbEnergyLevel.getProgress()-1);
				// Plus Description:
				resultClone.setExDescription(ExerciseRunner.exDescription());
				resultLiveData.setValue(resultClone);
				switchDataProvided.setChecked(false);
			}
		});

		// Listener for any seekBar changes
		seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				switchDataProvided.setChecked(true);
			}
			// Not used:
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		};
		sbEmotionalLevel.setOnSeekBarChangeListener(seekBarChangeListener); // Same listener
		sbEnergyLevel.setOnSeekBarChangeListener(seekBarChangeListener); // Same listener

		// Show keyboard
		if (resultLiveData != null) {
			etNote.requestFocus();
			alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			// Show the keyboard
			InputMethodManager imm = (InputMethodManager) context1.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(etNote, InputMethodManager.SHOW_IMPLICIT);
		}

		alertDialog.show();
	}

}
