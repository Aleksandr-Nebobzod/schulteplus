package org.nebobrod.schulteplus;





import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
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

		 view.setText("" + mExercise.getArea().get(position).getValue());
		 //Log.d(TAG, "getView:  " + view.getText());


		 { // Squared cells
			 int itemHeight = ((GridView) parent).getColumnWidth();
			 int rows = ((GridView) parent).getCount() / ((GridView) parent).getNumColumns();
			 if (itemHeight * rows > ((GridView) parent).getHeight()) {
				 itemHeight = ((GridView) parent).getHeight() / rows;
			 }
			 Log.d(TAG, "itemHeight: " + itemHeight);
			 view.setLayoutParams(new GridView.LayoutParams(new ViewGroup.LayoutParams(itemHeight, itemHeight)));
			 view.setTextSize((itemHeight/(.5F * textScale + 3)));
		 }


//		 https://stackoverflow.com/questions/51719485/adding-border-to-textview-programmatically
		 Drawable img = mContext.getDrawable(R.drawable.ic_border);
		 view.setBackground(img);
//		 TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 22, 36, 1, TypedValue.COMPLEX_UNIT_DIP);
//		 TextViewCompat.setAutoSizeTextTypeWithDefaults(view, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
		 view.setGravity(Gravity.CENTER_VERTICAL);
		 view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//		 view.setPadding(0, 25, 0, 25);
		 Log.d(TAG, "itemHeight: " + view.getHeight() + " and TextSize: " + view.getTextSize());

		 return view;
	 }


 }