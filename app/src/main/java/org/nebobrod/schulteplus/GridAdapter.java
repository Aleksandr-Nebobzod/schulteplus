package org.nebobrod.schulteplus;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GridAdapter extends BaseAdapter {
	 private Context mContext;
	 private Integer mCols, mRows;

	 public GridAdapter(Context context, int cols, int rows)
	 {
		 mContext = context;
		 mCols = cols;
		 mRows = rows;
	 }

	 @Override
	 public int getCount() {
		 return mCols * mRows;
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

		 view.setText("25");

		 return view;
	 }
 }