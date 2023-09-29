package org.nebobrod.schulteplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ExerciseRunner {
	private static final String TAG = "ExerciseRunner";
	public static final String KEY_RUNNER = "runner";
	public static final String KEY_TYPE_OF_EXERCISE = "tpr_ex_type";
	public static final String KEY_APP_STATE = "org.nebobrod.schulteplus_APP_STATE";

	private Context context;
	private static ExerciseRunner instance = null;
	private SharedPreferences sharedPreferences;

	// TODO: 13.09.2023   make get this fields from SchulteSettingsFragment & BaseSettingsFragment
	private static byte xSize = 2, ySize = 2;
	private static String exType = "";

	private ExerciseRunner(Context context) {
		try {
			sharedPreferences = context.getSharedPreferences(KEY_APP_STATE, Context.MODE_PRIVATE);
		}
		catch (Exception e){
			Log.d(TAG, "ExerciseRunner: noContext");
		}
	}

	public void setPreference(Context context){
		try {
			sharedPreferences = context.getSharedPreferences(KEY_APP_STATE, Context.MODE_PRIVATE);
		}
			catch (Exception e){
			Log.d(TAG, "ExerciseRunner: noContext");
		}
	}
	public static ExerciseRunner getInstance(Context context) {
		if (null == context) {
			Log.i(TAG, "getInstance: skipped");
			return instance;
		}
		if (null == instance) {
			instance = new ExerciseRunner(context);
			Log.i(TAG, "getInstance: applied");
		}
		return instance;
	}

	public static int getTypeOfExercise(){
		// TODO: 08.09.2023 learn STATIC 
		// TODO: 08.09.2023 it will linked to db Type of Exercise
		int type = 100;
		// TODO: 08.09.2023 make it savable later
		//type = intFromString (sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, null));

		switch (type){
			case 100:
				return 100; // means simple Schulte 5x5
			case 200:
				return 200; // means The Dot exercise
			case 210:
				return 210; // means The Necker cube
			default:
				return 0;
		}
	}

	public void setX(byte xSize) {
		this.xSize = xSize;
	}

	public void setY(byte ySize) {
		this.ySize = ySize;
	}

	public void setExType(String s) {
		exType = s;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY_TYPE_OF_EXERCISE, exType);
		editor.commit();
	}

	public int getX (){
		return xSize;
	}

	public int getY (){
		return ySize;
	}

	public static String getExType() {
//		exType = sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, "");
		return exType; // may be the var here is redundant
	}

}


