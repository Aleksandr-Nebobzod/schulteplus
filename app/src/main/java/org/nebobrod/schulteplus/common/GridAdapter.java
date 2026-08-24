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
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S4;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S2;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S3;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_COLOR_BLUE;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_COLOR_RED;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_LETTER_CYRILLIC;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_LETTER_DEVANAGARI;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_LETTER_LATIN;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_NUMBER_ROME;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
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
	/** Paved-режим «Мешанины»: null — классический вывод; иначе — маппер ячеек ↔ плиток */
	private PavingMap pavingMap = null;

	public void setPavingMap(PavingMap pavingMap) {
		this.pavingMap = pavingMap;
	}

	public GridAdapter(Context context, STable exercise, boolean isSquared, int textScale) {
		this.mContext = context;
		this.mExercise = exercise;
		this.isSquared = isSquared;
		this.textScale = textScale;

	}

	@Override
	public int getCount() {
		if (pavingMap != null) {
			return 100;	// paved: physical field 10x10
		}
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
			// з2: квадрат = min(ширина колонки, высота экрана/строк) — все плитки в поле зрения;
			// columnWidth грида задаёт SchulteActivity.post() (в getView при NO_STRETCH
			// getColumnWidth()=0 до явной установки — см. initArea)
			int side = Math.min(itemWidth, itemHeight);
			if (side > 0) {
				itemWidth = itemHeight = side;
			}
		}

//			 Log.d(TAG, "itemHeight: " + itemHeight);
//			 view.setLayoutParams(new GridView.LayoutParams(new ViewGroup.LayoutParams(itemHeight, itemHeight)));
		view.setLayoutParams(new GridView.LayoutParams(new ViewGroup.LayoutParams(itemWidth, itemHeight)));
		view.setTextColor(getRes().getColor(R.color.light_grey_2, getRes().newTheme()));
		// Кегль классики по режиму (в paved-режиме текст рисует drawable — setTextSize не влияет);
		// явно в px: setTextSize(float) трактует значение как sp и раздувает кегль на плотных экранах
		if (pavingMap == null) {
			view.setTextSize(TypedValue.COMPLEX_UNIT_PX, calcTextSize(itemWidth, itemHeight));
		}


//		 TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 22, 36, 1, TypedValue.COMPLEX_UNIT_DIP);
//		 TextViewCompat.setAutoSizeTextTypeWithDefaults(view, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
		view.setGravity(Gravity.CENTER_VERTICAL);
		view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//		 view.setPadding(0, 25, 0, 25);
//		 Log.d(TAG, "itemHeight: " + view.getHeight() + " and TextSize: " + view.getTextSize());

		return view;
	}

	/**
	 * Кегль классической ячейки по режиму (prf_font_scale):
	 * -1 — маленький: символ ½ длины и ½ ширины ячейки;
	 * 0 — максимальный: единый кегль по самому длинному символу последовательности;
	 * 1 — плитка: растяжка на ячейку (StretchTextDrawable), кегль не используется.
	 */
	private int calcTextSize(int itemWidth, int itemHeight) {
		int minSide = Math.min(itemWidth, itemHeight);
		switch (textScale) {
			case -1:
				return minSide / 2;
			case 0: {
				Paint paint = new Paint();
				paint.setTextSize(100);
				float longestW = paint.measureText(longestSymbol());
				if (longestW > 0) {
					// з3: −10% — визуально слишком тесно при точном вписывании
					return (int) (Math.min(minSide, minSide * 100 / longestW) * 0.9f);
				}
				return (int) (minSide * 0.9f);
			}
			default:
				return minSide / 2;	// 1 (плитка): текст рисует StretchTextDrawable
		}
	}

	/** Самый длинный текстовый символ последовательности (для режима «максимальный») */
	private String longestSymbol() {
		String longest = "";
		for (SCell cell : mExercise.getArea()) {
			String text = cell.getText();
			if (text != null && text.length() > longest.length()) {
				longest = text;
			}
		}
		return longest;
	}

	/**
	 * Set Cell graphics by ExType (moved from STable, step 1.4)
	 * mono, two-colored sequences, four-colored sequences
	 */
	private TextView setCellView (TextView view, int position) {
		SCell cell;
		if (pavingMap != null) {
			// paved: physical cell belongs to its tile (see PavingMap)
			int tile = pavingMap.tileAt(position / 10, position % 10);
			cell = mExercise.getArea().get(tile - 1);
		} else {
			cell = mExercise.getArea().get(position);
		}
		int value = cell.getValue();
		String strValue = "";
		@ColorInt int color;
		//		 https://stackoverflow.com/questions/51719485/adding-border-to-textview-programmatically
		Drawable img = AppCompatResources.getDrawable(mContext, R.drawable.ic_border);
		color = ContextCompat.getColor(mContext, R.color.light_grey_D);

		switch (mExercise.getAppContext().getExTypeId()){
			case KEY_PRF_EX_S1:
			case KEY_PRF_EX_S4:	// «Мешанина»: знаки по общей настройке (TP-13)
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
		if (pavingMap != null) {
			float borderWidth = 2 * view.getResources().getDisplayMetrics().density;
			PavedCellDrawable paved = new PavedCellDrawable(color, borderWidth);
			int row = position / 10;
			int col = position % 10;
			paved.setSides(
					pavingMap.isOuterSide(row, col, 2),	// West
					pavingMap.isOuterSide(row, col, 3),	// North
					pavingMap.isOuterSide(row, col, 0),	// East
					pavingMap.isOuterSide(row, col, 1));// South
			// TP-15: одно число на плитку рисует drawable (TextView пустой); режим кегля — textScale
			paved.setNumber(strValue, view.getCurrentTextColor(),
					pavingMap.tileBounds(pavingMap.tileAt(row, col)), row, col, textScale);
			view.setBackground(paved);
			view.setText("");
		} else {
			img.setColorFilter(color, PorterDuff.Mode.DST_ATOP);
			if (textScale == 1 && !strValue.isEmpty()) {
				// кегль «плитка»: растяжка символа на всю ячейку (искажение пропорций)
				view.setText("");
				view.setBackground(new LayerDrawable(new Drawable[] {img,
						new StretchTextDrawable(strValue, view.getCurrentTextColor())}));
			} else {
				view.setBackground(img);
			}
		}

		return view;
	}
}