package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.Utils.*;

import static org.nebobrod.schulteplus.Const.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import org.nebobrod.schulteplus.fbservices.AchievementsFbData;

public class ExerciseRunner {
	private static final String TAG = "ExerciseRunner";

	public static final String KEY_RUNNER = "runner"; //?
	public static final String KEY_DEFAULT_USER_PREF = "TFKBiTdd7OVYUaplfzDHrXSCixr1";


//	private Context context;
	private static ExerciseRunner instance = null;
	public static SharedPreferences sharedPreferences;

	// TODO: 13.09.2023   make get this fields from SchulteSettingsFragment & BaseSettingsFragment
	public static String uid = "";
	public static String userName = "";
	public static String userEmail = "";
	public static int points = 0; //  achieved point (seconds)
	public static int hours = 0; //  achieved hours
	public static int level = 1; // maximum achieved level
	public static int currentLevel = 1; // level limited by user
	private static String exType = "no_exercise";
	private static byte xSize = 5, ySize = 5;
	private static boolean hinted = true;
	public static  long tsRun;	// Timestamp of started

	private ExerciseRunner(Context context) {
		try {
//			sharedPreferences = context.getSharedPreferences(KEY_APP_STATE, Context.MODE_PRIVATE);
			uid = ((MainActivity) context).userHelper.getUid();
			userName = ((MainActivity) context).userHelper.getName();
			userEmail = ((MainActivity) context).userHelper.getEmail();
			if (uid.isEmpty()) uid = KEY_DEFAULT_USER_PREF;
			this.loadPreference(context);
			this.savePreferences(null);
		}
		catch (Exception e){
			Log.d(TAG, "ExerciseRunner: noContext");
		}
	}

	public static ExerciseRunner getInstance(Context context) {
		if (null == context) {
			Log.i(TAG, "getInstance: skipped");
		}
		if (null == instance) {
			instance = new ExerciseRunner(context);
			Log.i(TAG, "getInstance: applied");
		}
		return instance;
	}
	public SharedPreferences getSharedPreferences() { return sharedPreferences; }

	public void loadPreference(Context context){
		try {
			// apply previous parameters on load

			sharedPreferences = context.getSharedPreferences(uid, Context.MODE_PRIVATE);

			// Getting default preferences if there aren't still there
			PreferenceManager.setDefaultValues(context, R.xml.menu_preferences, false);

			// And some additional prefs:
			//userName = sharedPreferences.getString(KEY_USER_NAME, "null");
			//userEmail = sharedPreferences.getString(KEY_USER_EMAIL, "null@email.com");
			points = sharedPreferences.getInt(KEY_POINTS, 0);
			level = sharedPreferences.getInt(KEY_PRF_LEVEL, 1);
			currentLevel = sharedPreferences.getInt(KEY_PRF_CURRENT_LEVEL, 1);
			hinted = sharedPreferences.getBoolean(KEY_HINTED, true);

			exType = sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, "gcb_schulte_1_sequence");
			xSize = (byte) sharedPreferences.getInt(KEY_X_SIZE, 5);
			ySize = (byte) sharedPreferences.getInt(KEY_Y_SIZE, 5);
			tsRun = sharedPreferences.getLong(KEY_TS_UPDATED, timeStamp());
		}
			catch (Exception e){
			Log.d(TAG, "ExerciseRunner: noContext");
		}
	}

	public static boolean savePreferences(@Nullable STable exercise){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		boolean result = true;
		boolean newHour = false;
		editor.putString(	KEY_USER_NAME, userName);
		editor.putString(	KEY_USER_EMAIL, userEmail);

		editor.putBoolean(	KEY_HINTED, hinted).apply();
		editor.putString(	KEY_TYPE_OF_EXERCISE, exType);
		editor.putInt(		KEY_X_SIZE, (int) xSize).apply();
		editor.putInt(		KEY_Y_SIZE, (int) xSize).apply();
		tsRun = timeStamp();
		editor.putLong(		KEY_TS_UPDATED, tsRun).apply();

		if(null != exercise){
			CALC:
			if (exercise.isFinished()) {
				// Spent ms During the exercise
				int events = exercise.journal.size()-1;
				long spent = ((exercise.journal.get(events).timeStamp - exercise.journal.get(0).timeStamp)/1000000000) ;

				// if an average turn duration exceeds 5 minutes
				if ((spent / events) > 300) {
					result = false;
					break CALC;
				}

				// On the whole spent seconds
				spent += sharedPreferences.getInt(KEY_POINTS, 0);

				points = (int) spent;
				AchievementsFbData.achievePut( uid, userName, timeStamp(), timeStampFormatted(timeStamp()), Utils.getRes().getString(R.string.lbl_mu_second), "" + points, " ");
//				AchievementsFbData.getData();
				if (points > 3600){
					hours += (points / 3600);
					points = (points % 3600);
					newHour = true;
				}
				editor.putInt(		KEY_POINTS, (int) points).apply();
				hours += sharedPreferences.getInt(KEY_HOURS, 0);
				if (newHour) AchievementsFbData.achievePut( uid, userName, timeStamp(), timeStampFormatted(timeStamp()), Utils.getRes().getString(R.string.prf_hours_title), "" + hours, "➚");
				editor.putInt(		KEY_HOURS, (int) hours  ).apply();
				editor.putInt(		KEY_PRF_LEVEL, (int) Math.sqrt(hours * 3600 + points)  ).apply();
			}
		}

		if (result) editor.commit();

		Log.d(TAG, "save " + result + " in Preferences: " + instance);
		return result;
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

	public static boolean isHinted() {
		return hinted;
	}

	public  String getDateTimeUpdated() {
		return timeStampFormatted(tsRun);
	}

	@Override
	public String toString() {
		return "ExerciseRunner of " + userName + ", " + userEmail + ", " + uid + " {" +
				"\npoints=" + points +
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

	public static boolean getPrefHaptic(){ return sharedPreferences.getBoolean(KEY_HAPTIC, true);}
	public static boolean getPrefSound(){ return sharedPreferences.getBoolean(KEY_HAPTIC, true);}
	public static int getPrefTextScale(){ return sharedPreferences.getInt(KEY_PRF_FONT_SCALE, 0);}
}


