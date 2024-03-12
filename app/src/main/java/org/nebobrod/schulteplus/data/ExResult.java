/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Const.LAYOUT_GROUP_FLAG;
import static org.nebobrod.schulteplus.Const.LAYOUT_HEADER_FLAG;
import static org.nebobrod.schulteplus.Utils.duration;
import static org.nebobrod.schulteplus.Utils.durationCut;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.timeStampToDateLocal;
import static org.nebobrod.schulteplus.Utils.timeStampFormattedLocal;
import static org.nebobrod.schulteplus.Utils.timeStampToTimeLocal;
import static org.nebobrod.schulteplus.Utils.timeStampU;

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
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.MutableLiveData;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Uniform class for Exercise Results of any exercise
 */
@DatabaseTable(tableName = "exresult")
public class ExResult implements Serializable {
	public static final String TAG = "ExResult";

	private static final long serialVersionUID = -7874823823497497002L;
	public static final String DATE_FIELD_NAME = "dateTime";
	public static final String UID_FIELD_NAME = "uid";
	public static final String EXTYPE_FIELD_NAME = "exType";
	public static final String TIMESTAMP_FIELD_NAME = "timeStamp";
	static ExResultArrayAdapter exResultArrayAdapter;


	@DatabaseField(generatedId = true)
	private Integer id; // transactID()
	@DatabaseField
	private String uid; // Used as a filter for local data
	@DatabaseField
	private String name; // UserName
	@DatabaseField
	private long timeStamp;
	@DatabaseField
	private String dateTime;
	/**
	 * same as {@link org.nebobrod.schulteplus.Const#KEY_PRF_EX_S1}
	 * @see org.nebobrod.schulteplus.R.array#ex_type
	 */
	@DatabaseField
	private String exType; // see <string-array name="ex_type"> & Const
	@DatabaseField
	private String exDescription; //description of Ex, set of settings

	// and result data itself:
	@DatabaseField
	private long numValue; /* used as number of milliseconds, spent through the exercise */
	/**
	 * @see org.nebobrod.schulteplus.R.array#level_of_emotion_values
	 */
	@DatabaseField
	private int levelOfEmotion;
	/**
	 * @see org.nebobrod.schulteplus.R.array#level_of_energy_values
	 */
	@DatabaseField
	private int levelOfEnergy;
	@DatabaseField
	protected String note;


	// section of Schulte-exercises data:
	@DatabaseField
	private int turns; // also use as number of events in Basics
	@DatabaseField
	private int turnsMissed;
	@DatabaseField
	private float average;
	@DatabaseField
	private float rmsd; // Root-mean-square deviation as a sign of stability & rhythm in exercises
	// Non-database field for layout elements' visibility control
	private String layoutFlag = "";


	public ExResult() {}
	public ExResult(long numValue, int levelOfEmotion, int levelOfEnergy, String note) {
//		this.id = ((Long) Utils.transactID()).intValue(); //
		// common exercise-defined fields
		this.uid = ExerciseRunner.getUserHelper().getUid();
		this.name = ExerciseRunner.getUserHelper().getName();
		this.timeStamp = timeStampU();
		this.dateTime = timeStampFormattedLocal(this.timeStamp);
		this.exType = ExerciseRunner.getExType();
		this.exDescription = ""; // TODO: 26.02.2024 gather settings & screen width in String

		// additional fields
		this.numValue = numValue;
		this.levelOfEmotion = levelOfEmotion;
		this.levelOfEnergy = levelOfEnergy;
		this.note = note;
	}

	public Integer id() {
		return id;
	}
/* not required
	public ExResult<T> setId(Integer id) {
		this.id = id;
		return this;
	}*/

	public String uid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long timeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String dateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String exType() {
		return exType;
	}

	public void setExType(String exType) {
		this.exType = exType;
	}

	public String exDescription() {
		return exDescription;
	}

	public void setExDescription(String exDescription) {
		this.exDescription = exDescription;
	}

	public long numValue() {
		return numValue;
	}

	public void setNumValue(long numValue) {
		this.numValue = numValue;
	}

	public int levelOfEmotion() {
		return levelOfEmotion;
	}

	public void setLevelOfEmotion(int levelOfEmotion) {
		this.levelOfEmotion = levelOfEmotion;
	}

	public int levelOfEnergy() {
		return levelOfEnergy;
	}

	public void setLevelOfEnergy(int levelOfEnergy) {
		this.levelOfEnergy = levelOfEnergy;
	}

	public String note() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int turns() {
		return turns;
	}

	public void setTurns(int turns) {
		this.turns = turns;
	}

	public int turnsMissed() {
		return turnsMissed;
	}

	public void setTurnsMissed(int turnsMissed) {
		this.turnsMissed = turnsMissed;
	}

	public float average() {
		return average;
	}

	public void setAverage(float average) {
		this.average = average;
	}

	public float rmsd() {
		return rmsd;
	}

	public void setRmsd(float rmsd) {
		this.rmsd = rmsd;
	}

	public String layoutFlag() {
		return layoutFlag;
	}

	public ExResult setLayoutFlag(String layoutFlag) {
		this.layoutFlag = layoutFlag;
		return this;
	}


	@Override
	public String toString() {
		return "ExResult{" +
				"id=" + id +
				", dateTime='" + dateTime + '\'' +
				", exType='" + exType + '\'' +
				", numValue=" + numValue +
				", comment='" + note + '\'' +
				", turns=" + turns +
				'}';
	}

	/**
	 * Tab Separated Values
	 */
	public String toTabSeparatedString() {
		return TAG +
				"\t" + id +
				"\t" + dateTime +
				"\t" + exType +
				"\t" + numValue +
				"\t" + note +
				"\t" + turns;
	}

	public Map<String, String> toMap() {
		Map<String, String> stringMap = new LinkedHashMap<>();

		stringMap.put(Utils.getRes().getString(R.string.lbl_time), String.format("%.2f", (numValue /1000F)));

//		stringMap.put(Utils.getRes().getString(R.string.lbl_turns_missed), turnsMissed + "");
//		stringMap.put(Utils.getRes().getString(R.string.lbl_average), String.format("%.2f", (average /1000F)));
//		stringMap.put(Utils.getRes().getString(R.string.lbl_sd), String.format("%.2f", (rmsd /1000F)));

//		stringMap.put(Utils.getRes().getString(R.string.lbl_level_of_emotion), levelOfEmotion + "");
//		stringMap.put(Utils.getRes().getString(R.string.lbl_level_of_energy), levelOfEnergy + "");
//		stringMap.put(Utils.getRes().getString(R.string.lbl_note), note + "");

		return stringMap;
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
		AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context1);
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

		// Put the dialog layout to bottom of the screen
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
//		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		// Variables
		LayoutInflater inflater = alertDialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.activity_schulte_result_df, frameView);
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
		final Button[] btnRedesign = new Button[1];

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
				btnRedesign[0] = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				btnRedesign[0].setLayoutParams(btnOk.getLayoutParams());
				btnRedesign[0].setBackground(Utils.getRes().getDrawable(R.drawable.bg_button));
				btnRedesign[0].setTextAppearance(R.style.button3d);
				btnRedesign[0].setAllCaps(false);
				btnRedesign[0].setWidth(btnOk.getWidth()-10);
				// redesign Cancel by template
				btnRedesign[0] = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				btnRedesign[0].setLayoutParams(btnCancel.getLayoutParams());
				btnRedesign[0].setBackground(Utils.getRes().getDrawable(R.drawable.bg_button));
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

		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		alertDialog.show();
	}

	/**
	 * Provides a List adapter in accordance with declared layout {@link ExResult.ExResultArrayAdapter#textViewResourceId}
	 * @return
	 */
	public static ExResultArrayAdapter getArrayAdapter(Context context, List<ExResult> items) {
		exResultArrayAdapter = new ExResultArrayAdapter(context, items);
		return exResultArrayAdapter;
	}
	private static class ExResultArrayAdapter extends ArrayAdapter<ExResult> {
		static int textViewResourceId  = R.layout.fragment_dashboard_elv_achievement;
		List<ExResult> items = new ArrayList<>();
//		static ArrayAdapter<List<ExResult>> exResultArrayAdapter;

		public ExResultArrayAdapter(Context context, List<ExResult> items) {
			super(context, textViewResourceId, items);
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
			switch (exResult.layoutFlag()) {
				case LAYOUT_HEADER_FLAG:
					v.findViewById(R.id.ll_header).setVisibility(View.VISIBLE);
				case LAYOUT_GROUP_FLAG:
					v.findViewById(R.id.tv_group_header).setVisibility(View.VISIBLE);
					fillText(v, R.id.tv_group_header, timeStampToDateLocal(exResult.timeStamp()));
					break;
				default:
					break;
			}

//			fillText(v, R.id.tv_flag, exResult.layoutFlag()); // for debug
			fillText(v, R.id.tv_num, "" + (position + 1)); // achievement.getName() -- not needed in personal list
			fillText(v, R.id.tv_time, timeStampToTimeLocal(exResult.timeStamp()));
			fillText(v, R.id.tv_duration, durationCut(exResult.numValue()));
			fillText(v, R.id.tv_events, exResult.turns() + "");
			fillText(v, R.id.tv_note, exResult.note());
			fillText(v, R.id.tv_note_full, exResult.note());
			fillText(v, R.id.tv_emotion, exResult.levelOfEmotion() + "");
			fillText(v, R.id.tv_energy, exResult.levelOfEnergy() + "");
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
	}
}
