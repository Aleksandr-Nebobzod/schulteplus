/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.timeStampToDateLocal;
import static org.nebobrod.schulteplus.Utils.timeStampToTimeLocal;
import static org.nebobrod.schulteplus.common.Const.LAYOUT_GROUP_FLAG;
import static org.nebobrod.schulteplus.common.Const.LAYOUT_HEADER_FLAG;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.nebobrod.schulteplus.R;


/**
 *  Сlass providing data processing for a visual adapter of Achievement-POJO
 */
public class AchievementArrayAdapter extends ArrayAdapter<Achievement> {
	static int textViewResourceId  = R.layout.fragment_dashboard_elv_achievement;
	boolean hide;

	public AchievementArrayAdapter(Context context, List<Achievement> items, boolean hideExtraData) {
		super(context, textViewResourceId, items);
		hide = hideExtraData;
	}

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
		switch (achievement.getLayoutFlag()) {
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
		fillText(v, R.id.tv_num, "" + (position + 1));
		fillText(v, R.id.tv_time, timeStampToTimeLocal(achievement.getTimeStamp()));
		if (hide) {
			achievement.setName("");
			fillText(v, R.id.tv_name, achievement.getName()); 		//  -- not needed in personal list
		} else {
			fillText(v, R.id.tv_name, achievement.getName()); 		//  -- show in www list
		}
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

