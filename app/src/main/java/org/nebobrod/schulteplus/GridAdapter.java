package org.nebobrod.schulteplus;





import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GridAdapter extends BaseAdapter {
	 private static final String TAG = "GridAdapter";
	 private Context mContext;
	 private STable mExercise;

	 public GridAdapter(Context context, STable exercise) {
		 mExercise = exercise;
		 mContext = context;

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
//		 Log.d(TAG, "getView: width " + view.getWidth());
//		 https://stackoverflow.com/questions/51719485/adding-border-to-textview-programmatically
		 Drawable img = mContext.getDrawable(R.drawable.ic_border);
		 view.setBackground(img);
		 view.setTextSize(22);
		 view.setTextAlignment(view.TEXT_ALIGNMENT_CENTER);
		 view.setPadding(0, 25, 0, 25);

		 return view;
	 }

 }