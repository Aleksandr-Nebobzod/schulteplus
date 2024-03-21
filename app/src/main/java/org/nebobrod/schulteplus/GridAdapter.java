/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import org.nebobrod.schulteplus.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;


public class GridAdapter extends BaseAdapter {
	 private static final String TAG = "GridAdapter";
	 private Context mContext;
	 private STable mExercise;
	 private int textScale;

	 public GridAdapter(Context context, STable exercise) {
		 mExercise = exercise;
		 mContext = context;
		 textScale = ExerciseRunner.getPrefTextScale();

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


		 { // Squared cells
			 int itemWidth = ((GridView) parent).getColumnWidth();
			 int itemHeight = ((GridView) parent).getColumnWidth();
			 int rows = ((GridView) parent).getCount() / ((GridView) parent).getNumColumns();
			 /*if (itemHeight * rows > ((GridView) parent).getHeight())*/ {
				 itemHeight = ((GridView) parent).getHeight() / rows;
			 }
//			 Log.d(TAG, "itemHeight: " + itemHeight);
//			 view.setLayoutParams(new GridView.LayoutParams(new ViewGroup.LayoutParams(itemHeight, itemHeight)));
			 view.setLayoutParams(new GridView.LayoutParams(new ViewGroup.LayoutParams(itemWidth, itemHeight)));
			 view.setTextSize((Math.min(itemWidth, itemHeight)/(-1.3F * textScale + 5)));
		 }

//		 TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 22, 36, 1, TypedValue.COMPLEX_UNIT_DIP);
//		 TextViewCompat.setAutoSizeTextTypeWithDefaults(view, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
		 view.setGravity(Gravity.CENTER_VERTICAL);
		 view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//		 view.setPadding(0, 25, 0, 25);
//		 Log.d(TAG, "itemHeight: " + view.getHeight() + " and TextSize: " + view.getTextSize());

		 return view;
	 }
 }