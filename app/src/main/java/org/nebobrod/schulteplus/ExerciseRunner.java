package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.Utils.*;

import static org.nebobrod.schulteplus.Const.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import org.nebobrod.schulteplus.data.OrmRepo;
import org.nebobrod.schulteplus.fbservices.AchievementsFbData;
import org.nebobrod.schulteplus.fbservices.UserDbPref;
import org.nebobrod.schulteplus.fbservices.UserHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExerciseRunner implements UserDbPref.UserDbPrefCallback {
	private static final String TAG = "ExerciseRunner";
	public static final String KEY_RUNNER = "runner"; //?
	public static final String KEY_DEFAULT_USER_PREF = "TFKBiTdd7OVYUaplfzDHrXSCixr1";
	public static SharedPreferences sharedPreferences;
	public static String uid = "";
	public static String userName = "";
	public static String userEmail = "";
	public static boolean sharedData = false; // KEY_SEND_DATA
	public static int points = 0; //  achieved point (seconds)
	public static int hours = 0; //  achieved hours
	public static int level = 1; // maximum achieved level
	public static int currentLevel = 1; // level limited by user
	public static  long tsUpdated;	// Timestamp of data updated
//	private Context context;
	private static ExerciseRunner instance = null;
	private static String exType = "no_exercise";
	private static byte xSize = 5, ySize = 5;
	private static boolean ratings = false; // Limitation of Settings
	private static boolean hinted = true;
	private static boolean shuffled = true;
	private static String symbolType = "number"; // See string-array name="symbol_type_values"
	private static int fontScale = 0; // {-1, 0, 1}
	private static boolean probEnabled = false; // Probabilities control
	private static boolean probZero = false; // Probabilities can be 0
	private static double probDx = .0, probDy = .0, probW = .0;
	private static boolean online = true; // False if no connection to the Internet
	private static UserHelper userHelper;


	private ExerciseRunner(UserHelper userHelper) {
		Context context = getAppContext();
		setUserHelper(userHelper);
		try {
//			sharedPreferences = context.getSharedPreferences(KEY_APP_STATE, Context.MODE_PRIVATE);
			uid = userHelper.getUid();
			userName = userHelper.getName();
			userEmail = userHelper.getEmail();
			if (uid.isEmpty()) uid = KEY_DEFAULT_USER_PREF;
			userFbCrash(uid);
		}
		catch (Exception e){
			Log.d(TAG, "ExerciseRunner: not enough data");
			online = false;
			uid = KEY_DEFAULT_USER_PREF;
		}

		sharedPreferences = context.getSharedPreferences(uid, Context.MODE_PRIVATE);
		// Getting default preferences if there aren't still there
		PreferenceManager.setDefaultValues(context, R.xml.menu_preferences, false);

		this.loadPreference();
		this.savePreferences(null);
	}

	public static ExerciseRunner getInstance() 	{

		if (userHelper == null) userHelper = new UserHelper("nebobzod@gmail.com", "all", "password", "TFKBiTdd7OVYUaplfzDHrXSCixr1", "65ed474536cced3a", false);
		return  getInstance(userHelper);
	}
	public static ExerciseRunner getInstance(@NonNull UserHelper userHelper) {
/*		Context context = getAppContext();
		if (null == context) {
			Log.i(TAG, "getInstance: skipped");
		}*/
		if (null == instance) {
			instance = new ExerciseRunner(userHelper);
			UserDbPref.getInstance(instance);
			Log.i(TAG, "getInstance: applied");
		}
		if (!uid.equals(userHelper.getUid())) {
			instance = new ExerciseRunner(userHelper);
			UserDbPref.getInstance(instance);
		}
		return instance;
	}

	public static void loadPreference(){
		try {
			// apply previous parameters on load
			// And some additional prefs:
			userName = sharedPreferences.getString(KEY_USER_NAME, userName);
			userEmail = sharedPreferences.getString(KEY_USER_EMAIL, userEmail);
			sharedData = sharedPreferences.getBoolean(KEY_PRF_SHARED_DATA, false);
			points = sharedPreferences.getInt(KEY_POINTS, 0);
			level = sharedPreferences.getInt(KEY_PRF_LEVEL, 1);
			currentLevel = sharedPreferences.getInt(KEY_PRF_CURRENT_LEVEL, 1);
			ratings = sharedPreferences.getBoolean(KEY_PRF_RATINGS, false);
			probEnabled = sharedPreferences.getBoolean(KEY_PRF_PROB_ENABLED, false);
			exType = sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, KEY_PRF_EX_S1);
			if (exType.equals(KEY_PRF_EX_S2)
				| exType.equals(KEY_PRF_EX_S3)
				| exType.equals(KEY_PRF_EX_S4)) ratings = true; // an extra assurance for advanced level of STable
			if (exType.startsWith("gcb_bas_")) ratings = false; // clearance for basics
			// So, only TS 5x5 allows ratings On or Off

			if (ratings) {
				hinted = false;
				shuffled = true;
				switch (exType){
//					case KEY_PRF_EX_S1: xSize = ySize = 5; break; // see default
					case KEY_PRF_EX_S2: xSize = ySize = 7; break;
					case KEY_PRF_EX_S3: xSize = ySize = 10; break;
					default: xSize = ySize = 5;
				}
				symbolType = "number";
				probEnabled = false; probDx = probDy = probW = 0D;
			} else {
				hinted = sharedPreferences.getBoolean(KEY_PRF_HINTED, true);
				shuffled = sharedPreferences.getBoolean(KEY_PRF_SHUFFLE, true);
				xSize = (byte) sharedPreferences.getInt(KEY_X_SIZE, 5);
				ySize = (byte) sharedPreferences.getInt(KEY_Y_SIZE, 5);
				symbolType = sharedPreferences.getString(KEY_PRF_SYMBOLS, "number");
				if (probEnabled) {
					probZero = sharedPreferences.getBoolean(KEY_PRF_PROB_ZERO, false);
					probDx = sharedPreferences.getInt(KEY_PRF_PROB_X, 0) / 10D;
					probDy = sharedPreferences.getInt(KEY_PRF_PROB_Y, 0) / 10D;
					probW = sharedPreferences.getInt(KEY_PRF_PROB_SURFACE, 0) / 10D;
				} else {
					probDx = probDy = probW = 0D;
				}
			}

			fontScale = sharedPreferences.getInt(KEY_PRF_FONT_SCALE, 0);
			tsUpdated = sharedPreferences.getLong(KEY_TS_UPDATED, timeStamp());
		}
			catch (Exception e){
			Log.d(TAG, "ExerciseRunner: noContext");
		}
	}

	public static boolean savePreferences(@Nullable STable exercise){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		boolean result = true;
		List<AchievementFlags> achieved = new ArrayList<>();
		editor.putString(	KEY_USER_NAME, userName);
		editor.putString(	KEY_USER_EMAIL, userEmail);

		editor.putBoolean(	KEY_PRF_ONLINE, online);


		tsUpdated = timeStamp();
		editor.putLong(		KEY_TS_UPDATED, tsUpdated);

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
				achieved.add(AchievementFlags.SECONDS);
				if (points > 3600){
					hours += (points / 3600);
					points = (points % 3600);
					achieved.add(AchievementFlags.HOURS);
				}
				editor.putInt(		KEY_POINTS, (int) points);
				hours += sharedPreferences.getInt(KEY_HOURS, 0);
				editor.putInt(		KEY_HOURS, (int) hours  );
				int _level = (int) Math.sqrt(hours * 3600 + points);
				if (_level > level) {
					editor.putInt(		KEY_PRF_LEVEL, level = _level );
				}
			}
		}

		if (result) {
			editor.commit();
			achievedToBothDb(achieved, uid, userName, timeStamp(), timeStampFormattedLocal(timeStamp()), Utils.getRes().getString(R.string.lbl_mu_second), "" + points, " ");
		} else {
			editor.clear();

		}
		if (null != instance) UserDbPref.getInstance(instance).save();

		Log.d(TAG, "save " + result + " in Preferences: " + instance);
		return result;
	}

	/**Checks @achieved and put records to Local & Server db */
	private static void achievedToBothDb
	(List<AchievementFlags> achieved, String uid, String userName, long timeStamp, String timeStampFormattedLocal, String string, String s, String s1) {

		for (AchievementFlags flag: achieved) {
			switch (flag) {
				case SECONDS:
					OrmRepo.achievePut( uid, userName, timeStamp(), timeStampFormattedLocal(timeStamp()), Utils.getRes().getString(R.string.lbl_mu_second), "" + points, "");
					if (sharedData) AchievementsFbData.achievePut( uid, userName, timeStamp(), timeStampFormattedLocal(timeStamp()), Utils.getRes().getString(R.string.lbl_mu_second), "" + points, "");
				break;
				case HOURS:
					OrmRepo.achievePut(  uid, userName, timeStamp(), timeStampFormattedLocal(timeStamp()), Utils.getRes().getString(R.string.prf_hours_title), "" + hours, "➚");
					if (sharedData) AchievementsFbData.achievePut( uid, userName, timeStamp(), timeStampFormattedLocal(timeStamp()), Utils.getRes().getString(R.string.prf_hours_title), "" + hours, "➚");
				break;
			}
		}
	}

	public static String getExType() {
//		exType = sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, "");
		return exType; // may be the var here is redundant
	}

	public void setExType(String s) {
		s = (s.length()<7?"no_exercise":s);
		exType = s;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY_TYPE_OF_EXERCISE, exType);
		editor.commit();
	}

	public static boolean isHinted() {
		return hinted;
	}

	public void setHinted(boolean hinted) {
		ExerciseRunner.hinted = hinted;
	}

	public static double probDx() {
		return probDx;
	}

	public static double probDy() {
		return probDy;
	}

	public static double probW() {
		return probW;
	}

	public static boolean getPrefHaptic(){ return sharedPreferences.getBoolean(KEY_PRF_HAPTIC, true);}

	public static boolean getPrefSound(){ return sharedPreferences.getBoolean(KEY_PRF_HAPTIC, true);}

	public static int getPrefTextScale(){ return sharedPreferences.getInt(KEY_PRF_FONT_SCALE, 0);}

	public static boolean isOnline() {		return online;	}

	public static void setOnline(boolean online) {		ExerciseRunner.online = online;	}

	public static UserHelper getUserHelper() {	return userHelper;	}

	public static void setUserHelper(UserHelper userHelper) { ExerciseRunner.userHelper = userHelper;}

	public static boolean isRatings() {return ratings;}

	public static void setRatings(boolean ratings) {ExerciseRunner.ratings = ratings;}

	public static boolean isProbEnabled() {return probEnabled;}

	public static void setProbEnabled(boolean probEnabled) {ExerciseRunner.probEnabled = probEnabled;}

	public static boolean isShuffled() {return shuffled;}

	public static void setShuffled(boolean shuffled) {ExerciseRunner.shuffled = shuffled;}

	public static String GetUid() {return uid;}

	public SharedPreferences getSharedPreferences() { return sharedPreferences; }

	public int getX (){
		return xSize;
	}

	public void setX(byte xSize) {
		this.xSize = xSize;
	}

	public int getY (){
		return ySize;
	}

	public void setY(byte ySize) {
		this.ySize = ySize;
	}

	public  String getDateTimeUpdated() {
		return timeStampFormattedLocal(tsUpdated);
	}

	@Override
	public String toString() {
		return "ExerciseRunner of " + userName + ", " + userEmail + ", " + uid + " {" +
				"\npoints=" + points +
				"\nexType=" + exType +
				"\nxSize=" + xSize +
				"\nySize=" + ySize +
				"\nHints=" + (hinted ?"On":"Off") +
				"\ntsUpdated=" + tsUpdated + "\ntsUpdatedDateTime=" + timeStampFormattedLocal(tsUpdated) +
				"\nsharedPreferences=" +
					"\n\texType = " + sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, "gcb_schulte_1_sequence<") +
					"\n\txSize = " + String.valueOf( sharedPreferences.getInt(KEY_X_SIZE, 5)) +
					"\n\tySize = " + String.valueOf( sharedPreferences.getInt(KEY_Y_SIZE, 5)) +
					"\n\tySize = " + String.valueOf( sharedPreferences.getBoolean(KEY_PRF_HINTED, true)) +
				"\n}";
	}

	public String getName() {return userName;}
	public String getEmail() {return userEmail;}
	public int getPoints() {return points;}
	public int getHours() {return hours;}
	public int getLevel() {return level;}
	public long getTsUpdated() {return tsUpdated;}
	public String getUid() {return uid;	}

	@Override
	public void onCallback(Map<String, Object> objectMap) {
		if (objectMap == null & uid != null) {
			// Seems a new user
			UserDbPref.getInstance(instance).save();
			return;
		}
		// check which source is more fresh (local or srv)
		long sumPointsDb = ((Number)  objectMap.get("hours")).longValue() * 3600 + ((Number) objectMap.get("hours")).longValue();
		long sumPointsLocal = getHours() * 3600 + this.getPoints();
		if (sumPointsLocal == sumPointsDb) {
			// do nothing
		} else if (sumPointsLocal > sumPointsDb) {
			UserDbPref.getInstance(instance).save();
		} else {
			loadFromDbPref();
		}
	}

	private void loadFromDbPref() {

		Map<String, Object> objectMap = UserDbPref.getInstance(instance).getObjectMap();

		uid = objectMap.get("uid").toString();
		userName = objectMap.get("name").toString();
		userEmail = objectMap.get("email").toString();
		points = ((Number) objectMap.get("psyCoins")).intValue();
		hours = ((Number) objectMap.get("hours")).intValue();
		level = ((Number) objectMap.get("level")).intValue();
		tsUpdated = ((Number) objectMap.get("tsUpdated")).longValue();
		savePreferences(null);
	}
}


