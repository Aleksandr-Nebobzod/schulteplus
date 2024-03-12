/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Const.LAYOUT_GROUP_FLAG;
import static org.nebobrod.schulteplus.Const.LAYOUT_HEADER_FLAG;
import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.iHtml;
import static org.nebobrod.schulteplus.Utils.pHtml;
import static org.nebobrod.schulteplus.Utils.timeStampToDateLocal;
import static org.nebobrod.schulteplus.Utils.timeStampToTimeLocal;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Achievement data object saved to the local database through ormlite.
 *
 * @author nebobzod
 */
@DatabaseTable
public class Achievement implements Serializable {
	private static final String TAG = "Achievement";

	private static final long serialVersionUID = -7874823823497497001L;
	public static final String DATE_FIELD_NAME = "dateTime";
	public static final String TIMESTAMP_FIELD_NAME = "timeStamp";

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


	private String layoutFlag = "";


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

	public String layoutFlag() {
		return layoutFlag;
	}

	public Achievement setLayoutFlag(String layoutFlag) {
		this.layoutFlag = layoutFlag;
		return this;
	}

	@Override
	public String toString() {
		return name == null ? "<None>" : name + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|";
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Tab Separated Values
	 */
	public String toTabSeparatedString() {
		return TAG +
				"\t" + id +
				"\t" + dateTime +
				"\t" + name +
				"\t" + recordText +
				"\t" + recordValue +
				"\t" + specialMark;
	}
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

	/**
	 * Provides a List adapter in accordance with declared layout {@link AchievementArrayAdapter#textViewResourceId}
	 * @return
	 */
	public static AchievementArrayAdapter getArrayAdapter(Context context, List<Achievement> items) {
		achievementArrayAdapter = new AchievementArrayAdapter(context, items);
		return achievementArrayAdapter;
	}

	private static class AchievementArrayAdapter extends ArrayAdapter<Achievement> {
		static int textViewResourceId  = R.layout.fragment_dashboard_elv_achievement;

		public AchievementArrayAdapter(Context context, List<Achievement> items) {
			super(context, textViewResourceId, items);
		}
/*

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
*/

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater li = (LayoutInflater) getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.fragment_dashboard_elv_achievement, null);
			}
			Achievement achievement = getItem(position);

			// Reset visibility for header and group header
			v.findViewById(R.id.ll_header).setVisibility(View.GONE);
			v.findViewById(R.id.tv_group_header).setVisibility(View.GONE);

			// Manage header and grouping
			switch (achievement.layoutFlag()) {
				case LAYOUT_HEADER_FLAG:
					v.findViewById(R.id.ll_header).setVisibility(View.VISIBLE);
				case LAYOUT_GROUP_FLAG:
					v.findViewById(R.id.tv_group_header).setVisibility(View.VISIBLE);
					fillText(v, R.id.tv_group_header, timeStampToDateLocal(achievement.getTimeStamp()));
					break;
				default:
					break;
			}

			//the other fields
			fillText(v, R.id.tv_num, "" + (position + 1)); // achievement.getName() -- not needed in personal list
			fillText(v, R.id.tv_time, timeStampToTimeLocal(achievement.getTimeStamp()));
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
