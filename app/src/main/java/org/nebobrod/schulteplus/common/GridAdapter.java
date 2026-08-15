/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S1;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S2;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S3;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_COLOR_BLUE;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_COLOR_RED;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_LETTER_CYRILLIC;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_LETTER_DEVANAGARI;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_LETTER_LATIN;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_NUMBER_ROME;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

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
		view = setCellView(view, position);
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

	/**
	 * Set Cell graphics by ExType (moved from STable, step 1.4)
	 * mono, two-colored sequences, four-colored sequences
	 */
	private TextView setCellView (TextView view, int position) {
		SCell cell = mExercise.getArea().get(position);
		int value = cell.getValue();
		String strValue = "";
		@ColorInt int color;
		//		 https://stackoverflow.com/questions/51719485/adding-border-to-textview-programmatically
		Drawable img = AppCompatResources.getDrawable(mContext, R.drawable.ic_border);
		color = ContextCompat.getColor(mContext, R.color.light_grey_D);

		switch (mExercise.getAppContext().getExTypeId()){
			case KEY_PRF_EX_S1:
				switch (mExercise.getAppContext().getSymbolType()) {
					case KEY_SYMBOL_TYPE_NUMBER_ROME:
					case KEY_SYMBOL_TYPE_LETTER_LATIN:
					case KEY_SYMBOL_TYPE_LETTER_CYRILLIC:
					case KEY_SYMBOL_TYPE_LETTER_DEVANAGARI:
						strValue = cell.getText();
						break;
					case KEY_SYMBOL_TYPE_COLOR_RED:
					case KEY_SYMBOL_TYPE_COLOR_BLUE:
						color = cell.getColor();
						break;
					default: 	// KEY_SYMBOL_TYPE_NUMBER
		//				view.setText(value); // value keeps its sequence
		//				color = ContextCompat.getColor(mContext, R.color.transparent);
						strValue = value + "";
				}
				break;
			case KEY_PRF_EX_S2:
				if (value % 2 != 0) { 		// odd
					value = 1 + value / 2; 	// 1:25 red
					color = ContextCompat.getColor(mContext, R.color.light_grey_A_blue);
				} else { 					// even
					value = 25 - value / 2; // 24:1 blue
					color = ContextCompat.getColor(mContext, R.color.light_grey_A_red);
				}
//				img.setColorFilter(Color.valueOf(getColor(R.color.light_grey_A_red)).toArgb(), PorterDuff.Mode.SRC_IN);
				strValue = value + "";
				break;
			case KEY_PRF_EX_S3:
				switch (value % 4) {
					case 1: // Growing
						value = 1 + value / 4; // 1:25 blue
						color = ContextCompat.getColor(mContext, R.color.light_grey_A_blue);
						break;
					case 2: // Downward
						value = (102 - value) / 4; // 25:1 red
						color = ContextCompat.getColor(mContext, R.color.light_grey_A_red);
						break;
					case 3: // Convergent
						value +=1; // 1,25:12,13 green
						value = (0 == (value % 8) ? 26 - (value / 8) : (value + 4) / 8);
						color = ContextCompat.getColor(mContext, R.color.light_grey_A_green);
						break;
					case 0: // Divergent
						value = (0 == (value % 8) ? 13 + (value / 8) : 13 - value / 8); // 12,13:1,25 yellow
						color = ContextCompat.getColor(mContext, R.color.light_grey_A_yellow);
						break;
				}
				strValue = value + "";
				break;
			default:
		}
		view.setText(strValue);
		img.setColorFilter(color, PorterDuff.Mode.DST_ATOP);
		view.setBackground(img);

		return view;
	}
}