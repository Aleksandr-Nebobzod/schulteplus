/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.iHtml;
import static org.nebobrod.schulteplus.Utils.pHtml;
import static org.nebobrod.schulteplus.Utils.timeStampDateLocal;
import static org.nebobrod.schulteplus.Utils.timeStampTimeLocal;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Achievement information object saved to the local database through ormlite.
 *
 * @author nebobzod
 */
@DatabaseTable
public class Achievement implements Serializable {
	private static final String TAG = "Achievement";

	private static final long serialVersionUID = -7874823823497497001L;
	public static final String DATE_FIELD_NAME = "dateTime";

	static Achievement achievement;
	static AchievementArrayAdapter achievementArrayAdapter;

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String uid;

	@DatabaseField
	private String name;

	@DatabaseField
	private long timeStamp;

	@DatabaseField
	private String dateTime;

	@DatabaseField
	private String recordText;

	@DatabaseField
	private String recordValue;

	/**
	 * for example: 1st achievement of day, one done 3 at once, selfrecords
	 * (if I got today most point than the best day before, or for week), duels wins, etc...
	 */
	@DatabaseField
	private String specialMark;

	public Integer getId() {
		return id;
	}

	public String getUid() {return uid;}

	public void setUid(String uid) {this.uid = uid;}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimeStamp() {return timeStamp;}

	public void setTimeStamp(long timeStamp) {this.timeStamp = timeStamp;}

	public String getDateTime() {return dateTime;}

	public void setDateTime(String dateTime) {this.dateTime = dateTime;}

	public String getRecordText() {return recordText;}

	public void setRecordText(String recordText) {this.recordText = recordText;}

	public String getRecordValue() {return recordValue;}

	public void setRecordValue(String recordValue) {this.recordValue = recordValue;}

	public String getSpecialMark() {return specialMark;}

	public void setSpecialMark(String specialMark) {this.specialMark = specialMark;}

	/**
	 * Provides a List adapter in accordance with declared layout {@link AchievementArrayAdapter#textViewResourceId}
	 * @return
	 */
	public static AchievementArrayAdapter getArrayAdapter(Context context, List<Achievement> items) {
		achievementArrayAdapter = new AchievementArrayAdapter(context, items);
		return achievementArrayAdapter;
	}

	@Override
	public String toString() {
		return name == null ? "<None>" : name + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|";
	}
	///////////////////////////////////////////////////////////////
	public Spanned toSpanned() {
		return Html.fromHtml("| \t" + this.getSpecialMark() + "\t| " + Utils.timeStampFormattedLocal(this.getTimeStamp()) + " | " + iHtml(this.getName()) + pHtml()
				+ "|\t." + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|");
	}

	public  Achievement setAchievement(String uid, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark){
		this.uid = uid;
		this.name = name;
		this.timeStamp = timeStamp;
		this.dateTime = dateTime;
		this.recordText = recordText;
		this.recordValue = recordValue;
		this.specialMark = specialMark;

		return this;
	}

	private static class AchievementArrayAdapter extends ArrayAdapter<Achievement> {
		static int textViewResourceId  = R.layout.fragment_dashboard_elv_achievement;

		public AchievementArrayAdapter(Context context, List<Achievement> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public int getCount() {
			return super.getCount();
		}

		@Nullable
		@Override
		public Achievement getItem(int position) {
			return super.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return super.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater li = (LayoutInflater) getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.fragment_dashboard_elv_achievement, null);
			}
			Achievement achievement = getItem(position);

			fillText(v, R.id.tv_name, "" + (position + 1)); // achievement.getName() -- not needed in personal list
			fillText(v, R.id.tv_date, timeStampDateLocal(achievement.getTimeStamp()));
			fillText(v, R.id.tv_time, timeStampTimeLocal(achievement.getTimeStamp()));
			fillText(v, R.id.tv_record_text, achievement.getRecordText());
			fillText(v, R.id.tv_record_value, achievement.getRecordValue());
			fillText(v, R.id.tv_special_mark, achievement.getSpecialMark());

			return v;
		}

		private <T> void fillText(View v, int id, T text) {
			TextView textView = (TextView) v.findViewById(id);
			textView.setText((CharSequence) text);
		}
	}
}
