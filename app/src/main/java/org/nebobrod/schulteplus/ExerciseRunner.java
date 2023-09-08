package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.Utils.intFromString;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ExerciseRunner {
	private static final String TAG = "ExerciseRunner";
	public static final String KEY_TYPE_OF_EXERCISE = "type_of_ex";
	public static final String KEY_APP_STATE = "schulte_app_state";


	private static ExerciseRunner instance;
	private SharedPreferences sharedPreferences;


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

	public int getX (){
		return 5;
	}

	public int getY (){
		return 5;
	}
}


