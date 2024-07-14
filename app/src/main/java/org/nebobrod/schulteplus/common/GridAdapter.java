/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import static org.nebobrod.schulteplus.Utils.getRes;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.nebobrod.schulteplus.R;


public class GridAdapter extends BaseAdapter {
	private static final String TAG = "GridAdapter";
	private final int textScale;
	private Context mContext;
	private STable mExercise;
	private boolean isSquared;

	public GridAdapter(Context context, STable exercise, boolean isSquared, int textScale) {
		this.mContext = context;
		this.mExercise = exercise;
		this.isSquared = isSquared;
		this.textScale = textScale;

	}

	@Override
	public int getCount() {
		return mExercise.getX() * mExercise.getY();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView view; // Text of a cell

		if (convertView == null)
			view = new TextView(mContext);
		else
			view = (TextView) convertView;

		// TODONE: 28.11.2023--14.12 extend this to Stable.setViewContent by exType & position
//		 view.setText("" + mExercise.getArea().get(position).getValue());
		view = mExercise.setViewContent(view, position);
		//Log.d(TAG, "getView:  " + view.getText());


		// Maximized cells
		int itemWidth = ((GridView) parent).getColumnWidth();
		int itemHeight = ((GridView) parent).getColumnWidth();
		int rows = ((GridView) parent).getCount() / ((GridView) parent).getNumColumns();
		/*if (itemHeight * rows > ((GridView) parent).getHeight())*/

		itemHeight = ((GridView) parent).getHeight() / rows;

		if (isSquared) {
			itemHeight = itemWidth = Math.min(itemHeight, itemWidth);
		}

//			 Log.d(TAG, "itemHeight: " + itemHeight);
//			 view.setLayoutParams(new GridView.LayoutParams(new ViewGroup.LayoutParams(itemHeight, itemHeight)));
		view.setLayoutParams(new GridView.LayoutParams(new ViewGroup.LayoutParams(itemWidth, itemHeight)));
		view.setTextColor(getRes().getColor(R.color.light_grey_2, getRes().newTheme()));
		view.setTextSize((Math.min(itemWidth, itemHeight) / (-1.3F * textScale + 5)));


//		 TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 22, 36, 1, TypedValue.COMPLEX_UNIT_DIP);
//		 TextViewCompat.setAutoSizeTextTypeWithDefaults(view, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
		view.setGravity(Gravity.CENTER_VERTICAL);
		view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//		 view.setPadding(0, 25, 0, 25);
//		 Log.d(TAG, "itemHeight: " + view.getHeight() + " and TextSize: " + view.getTextSize());

		return view;
	}
}