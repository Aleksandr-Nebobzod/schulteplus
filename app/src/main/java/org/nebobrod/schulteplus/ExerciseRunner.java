package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.Utils.intFromString;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.nebobrod.schulteplus.ui.schultesettings.SchulteSettingsFragment;

public class ExerciseRunner {
	private static final String TAG = "ExerciseRunner";
	public static final String KEY_RUNNER = "runner";
	public static final String KEY_TYPE_OF_EXERCISE = "type_of_ex";
	public static final String KEY_APP_STATE = "schulte_app_state";

	private Context context;
	private static ExerciseRunner instance;
	private SharedPreferences sharedPreferences;

	// TODO: 13.09.2023   make get this fields from SchulteSettingsFragment & BaseSettingsFragment
	private static byte xSize = 2, ySize = 2;
	private static String exType = "";

	private ExerciseRunner(Context context) {
		sharedPreferences = context.getSharedPreferences(KEY_APP_STATE, Context.MODE_PRIVATE);
	}

	public static ExerciseRunner getInstance(Context context) {
		Log.i(TAG, "getInstance: applied");
		if (null == instance) {
			instance = new ExerciseRunner(context);
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
	}

	public int getX (){
		return xSize;
	}

	public int getY (){
		return ySize;
	}

	public static String getExType() {
		return exType;
	}

}


