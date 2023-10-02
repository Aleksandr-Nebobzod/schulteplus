package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.Utils.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;

public class ExerciseRunner {
	private static final String TAG = "ExerciseRunner";
	public static final String KEY_RUNNER = "runner";
//	public static final String KEY_APP_STATE = "org.nebobrod.schulteplus_APP_STATE";
	public static final String KEY_APP_STATE = "org.nebobrod.schulteplus_preferences";
	public static final String KEY_TYPE_OF_EXERCISE = "prf_ex_type";
	public static final String KEY_X_SIZE = "prf_x_size";
	public static final String KEY_Y_SIZE = "prf_y_size";
	public static final String KEY_TS_UPDATED = "prf_ts_updated";
	public static final String KEY_HINTED = "prf_sw_hints";

	private Context context;
	private static ExerciseRunner instance = null;
	public SharedPreferences sharedPreferences;

	// TODO: 13.09.2023   make get this fields from SchulteSettingsFragment & BaseSettingsFragment
	private static String exType = "no_exercise";
	private static byte xSize = 5, ySize = 5;
	private static boolean hinted = true;
	public static  long tsRun;	// Timestamp of started

	private ExerciseRunner(Context context) {
		try {
//			sharedPreferences = context.getSharedPreferences(KEY_APP_STATE, Context.MODE_PRIVATE);
			this.getPreference(context);
		}
		catch (Exception e){
			Log.d(TAG, "ExerciseRunner: noContext");
		}
	}

	public void getPreference(Context context){
		try {
			this.context = context;
			sharedPreferences = context.getSharedPreferences(KEY_APP_STATE, Context.MODE_PRIVATE);
			exType = sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, "gcb_schulte_1_sequence");
			xSize = (byte) sharedPreferences.getInt(KEY_X_SIZE, 5);
			ySize = (byte) sharedPreferences.getInt(KEY_Y_SIZE, 5);
			tsRun = sharedPreferences.getLong(KEY_Y_SIZE, timeStamp());
			hinted = sharedPreferences.getBoolean(KEY_HINTED, true);
		}
			catch (Exception e){
			Log.d(TAG, "ExerciseRunner: noContext");
		}
	}

	public void savePreferences(){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(	KEY_TYPE_OF_EXERCISE, exType);
		editor.putInt(		KEY_X_SIZE, (int) xSize).apply();
		editor.putInt(		KEY_Y_SIZE, (int) xSize).apply();
		tsRun = timeStamp();
		editor.putLong(		KEY_TS_UPDATED, tsRun).apply();
		editor.putBoolean(	KEY_HINTED, hinted).apply();

		editor.commit();
		Log.d(TAG, "savePreferences: " + this.toString());
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


	public void setX(byte xSize) {
		this.xSize = xSize;
	}

	public void setY(byte ySize) {
		this.ySize = ySize;
	}

	public void setExType(String s) {
		s = (s.length()<7?"no_exercise":s);
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

	public  String getExType() {
//		exType = sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, "");
		return exType; // may be the var here is redundant
	}

	public void setHinted(boolean hinted) {
		ExerciseRunner.hinted = hinted;
	}

	public  boolean isHinted() {
		return hinted;
	}

	public  String getDateTimeUpdated() {
		return timeStampFormatted(tsRun);
	}

	@Override
	public String toString() {
		return "ExerciseRunner{" +
				"\nexType=" + exType +
				"\nxSize=" + xSize +
				"\nySize=" + ySize +
				"\nHints=" + (hinted ?"On":"Off") +
				"\ntsUpdated=" + tsRun + "\ntsUpdatedDateTime=" + timeStampFormatted(tsRun) +
				"\nsharedPreferences=" +
					"\n\texType = " + sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, "gcb_schulte_1_sequence<") +
					"\n\txSize = " + String.valueOf( sharedPreferences.getInt(KEY_X_SIZE, 5)) +
					"\n\tySize = " + String.valueOf( sharedPreferences.getInt(KEY_Y_SIZE, 5)) +
					"\n\tySize = " + String.valueOf( sharedPreferences.getBoolean(KEY_HINTED, true)) +
				"\n}";
	}
}


