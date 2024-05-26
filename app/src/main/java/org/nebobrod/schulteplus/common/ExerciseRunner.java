/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import static org.nebobrod.schulteplus.Utils.*;
import static org.nebobrod.schulteplus.common.Const.*;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.UserHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ExerciseRunner {
	private static final String TAG = "ExerciseRunner";
	public static final String KEY_RUNNER = "runner"; //?
	public static final String KEY_DEFAULT_USER_PREF = "TFKBiTdd7OVYUaplfzDHrXSCixr1";

	// exercise related data
	private static ExerciseRunner instance = null;
	private static Exercise exercise = null;
	private static ExResult exResult = null;
	private static long id;
	private static long seed = 0;

	// User statistical data (kept in LocalB, centralDB and sharedPreferences for indication)
	private static UserHelper userHelper;
	public static String uid = "";
	public static String uak = "";
	public static String userName = "";
	public static String userEmail = "";
	public static int points = 0; 				//  achieved point (seconds)
	public static int hours = 0; 				//  achieved hours
	public static int level = 1; 				// maximum achieved level
	public static  long timeStamp;				// Timestamp of data updated

	// Device-related options (kept only in sharedPreferences)
	public static SharedPreferences sharedPreferences;
	public static boolean sharedData = false; 	// KEY_SEND_DATA
	public static int currentLevel = 1; 		// level limited by user
	private static String exType = "no_exercise";
	private static byte xSize = 5, ySize = 5;
	private static boolean ratings = false; 	// Limitation of Settings
	private static boolean hinted = true;
	private static boolean shuffled = true;
	private static String symbolType = "number"; // See string-array name="symbol_type_values"
	private static int fontScale = 0; 			// {-1, 0, 1}
	private static boolean probEnabled = false; // Probabilities control
	private static boolean squared = false; 	// active screen height==width
	private static boolean probZero = false; 	// Probabilities can be 0
	private static double probDx = .0, probDy = .0, probW = .0;
	private static boolean online = true; 		// False if no connection to the Internet


	private ExerciseRunner(UserHelper userHelper) {
		Context context = getAppContext();

		uid = (uid.isEmpty() ? KEY_DEFAULT_USER_PREF : userHelper.getUid());
		setFbCrashlyticsUser(uid);
		sharedPreferences = context.getSharedPreferences(uid, Context.MODE_PRIVATE);
		// Getting default preferences if there aren't still there
		PreferenceManager.setDefaultValues(context, R.xml.menu_preferences, false);
		loadPreference();

		setUserHelper(userHelper); 		// it refreshes user related fields

		savePreferences();
	}

	/** Check and refresh runtime values for new Exercise */
	public static ExerciseRunner getInstance() 	{

		if (userHelper == null) userHelper = new UserHelper("TFKBiTdd7OVYUaplfzDHrXSCixr1", "nebobzod@gmail.com", "all", "password", "65ed474536cced3a", "65ed474536cced3a", false);
		return  getInstance(userHelper);
	}
	public static ExerciseRunner getInstance(@Nullable UserHelper userHelper) {

		if (userHelper == null) {
			getInstance();
			Log.i(TAG, "getInstance: default applied");
		}

		if (instance == null) {
			instance = new ExerciseRunner(userHelper);
			Log.i(TAG, "getInstance: new applied");
		}

		if (!uid.equals(userHelper.getUid())) {
			instance = new ExerciseRunner(userHelper);
			Log.i(TAG, "getInstance: new user applied");
		}

		timeStamp = timeStampU();

		return instance;
	}

	public static void loadPreference(){
		try {
			// apply previous parameters on load
			uak = sharedPreferences.getString(KEY_USER_APP_KEY, uak);
			userName = sharedPreferences.getString(KEY_USER_NAME, userName);
			userEmail = sharedPreferences.getString(KEY_USER_EMAIL, userEmail);
			sharedData = sharedPreferences.getBoolean(KEY_PRF_SHARED_DATA, true);
			points = sharedPreferences.getInt(KEY_POINTS, 0);
			level = sharedPreferences.getInt(KEY_PRF_LEVEL, 1);
			currentLevel = sharedPreferences.getInt(KEY_PRF_CURRENT_LEVEL, 1);
			ratings = sharedPreferences.getBoolean(KEY_PRF_RATINGS, false);
			probEnabled = sharedPreferences.getBoolean(KEY_PRF_PROB_ENABLED, false);
			squared = sharedPreferences.getBoolean(KEY_PRF_SQUARED, false);
			exType = sharedPreferences.getString(KEY_TYPE_OF_EXERCISE, KEY_PRF_EX_S1);

			// The only TS 5x5 allows ratings On or Off (KEY_PRF_EX_S1)
			if (exType.equals(KEY_PRF_EX_S2)
				| exType.equals(KEY_PRF_EX_S3)
				| exType.equals(KEY_PRF_EX_S4)) ratings = true; // an extra assurance for advanced level of STable
			if (exType.startsWith("gcb_bas_")) ratings = false; // clearance for basics

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
			timeStamp = sharedPreferences.getLong(KEY_TS_UPDATED, timeStampU());
		}
			catch (Exception e){
			Log.d(TAG, "ExerciseRunner: noContext");
		}
	}

	public static void savePreferences(){
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(	KEY_USER_APP_KEY, uak);
		editor.putString(	KEY_USER_NAME, userName);
		editor.putString(	KEY_USER_EMAIL, userEmail);
		editor.putBoolean(	KEY_PRF_ONLINE, online);
		editor.putInt(		KEY_POINTS, (int) points);
		editor.putInt(		KEY_HOURS, (int) hours);
		editor.putInt(		KEY_PRF_LEVEL, level);

		timeStamp = getTimeStamp();
		editor.putLong(		KEY_TS_UPDATED, timeStamp);

		editor.apply();
		Log.d(TAG, "saved in SharedPreferences: " + instance);
	}

	public static void start(@NonNull Exercise newExercise) {

		if (exercise != null) {
			Log.w(TAG, "start: " + "strange having an old Ex here");
		}
		exercise = newExercise;

		// save preliminary result to get id from LocalDB
		exResult = exercise.getExResult();
		timeStamp = exResult.getTimeStamp();
		DataOrmRepo ormRepo = new DataOrmRepo<>(exResult.getClass());
		ormRepo.create(exResult).addOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(@NonNull Task task) {
				id = exResult.getId();
			}
		});
	}

	public static void cancel() {
		if (exercise != null) {
			exercise = null;
		}
	}

	/**
	 * Finalise the exercise, check it, save records (exercise, achievements, preferences), update user status
	 */
	public static void complete() {

		exercise.setFinished(true);
		boolean result = true;
		List<AchievementFlags> achieved = new ArrayList<>();


		if(null != exercise){
			CALC:
			if (!exercise.validateResult()) {
				showSnackBar(getRes().getString(R.string.err_result_validity));
				result = false;
				break CALC;
			} else {
				exResult = exercise.getExResult();
				exResult.setExDescription(exDescription());

				// On the whole spent seconds
				points = (int) (sharedPreferences.getInt(KEY_POINTS, 0) + exResult.getNumValue());
				achieved.add(AchievementFlags.SECONDS);
				if (points > 3600){
					hours += (points / 3600);
					points = (points % 3600);
					achieved.add(AchievementFlags.HOURS);
				}

				// On an hour has been reached
				hours += sharedPreferences.getInt(KEY_HOURS, 0);
				int _level = (int) Math.sqrt(hours * 3600 + points);
				if (_level > level) {
					level = _level;
					achieved.add(AchievementFlags.LEVEL);
				}
			}
		}

		if (result) {
			updateExResult();
			savePreferences();
			achievedToBothDb(achieved, uid, userName, getTimeStamp());
			updateUserHelper();
		}
	}

	private static void updateUserHelper() {
		DataRepos<UserHelper> repos = new DataRepos<>(UserHelper.class);

		userHelper.setStatus(points, hours, level, getTimeStamp());
		repos.create(userHelper);
	}
	private static void updateExResult() {
		DataRepos<ExResult> repos = new DataRepos<>(ExResult.class);

		setExResult(exercise.getExResult());

		repos.create(exResult);
	}


	public static String exDescription() {
		// template is: "R/C-L-exType-X*Y-w-screen size Factor-Squared-P/L"
		String result = "";

		result += (ratings ? Utils.getRes().getString(R.string.code_rating) : Utils.getRes().getString(R.string.code_common));
		result += "-L" + level;
		result += "-" + exType;
		result += "-" + xSize + "*" + ySize;
		result += "-P" + (probEnabled ? "1" : "0");
		result += "-F" + getScreenFactor();
		result += "-S" + (squared ? "1" : "0");
		result += "-O" + (getRes().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? "L" : "P");

		return result;
	}

	/**Checks @achieved and put records to Local & Server db */
	private static void achievedToBothDb (List<AchievementFlags> achieved, String uid, String userName, long ts) {

		for (AchievementFlags flag: achieved) {
			Achievement ach = new Achievement();
			DataRepos<Achievement> repos = new DataRepos<>(Achievement.class);
			switch (flag) {
				case SECONDS:
					ach.set(uid, uak, userName, ts, timeStampFormattedLocal(ts), Utils.getRes().getString(R.string.lbl_mu_second), "" + points, "");
					break;
				case HOURS:
					ach.set(uid, uak, userName, ts, timeStampFormattedLocal(ts), Utils.getRes().getString(R.string.prf_hours_title), "" + hours, "➚");
					break;
				default:
					Log.w(TAG, "achievedToBothDb: " + "Wrong Achievement flag: " + flag);
			}
			repos.create(ach);
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

	/**
	 * Set helper and refresh user related fields
	 * @param userHelper initiator of {@link ExerciseRunner}
	 */
	public static void setUserHelper(UserHelper userHelper) {
		ExerciseRunner.userHelper = userHelper;

		uak = userHelper.getUak();
		userName = userHelper.getName();
		userEmail = userHelper.getEmail();
		uak = userHelper.getUak();
		points = userHelper.getPsyCoins();
		hours = userHelper.getHours();
		level = userHelper.getLevel();
	}

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
		return timeStampFormattedLocal(timeStamp);
	}

	@Override
	public String toString() {
		return "ExerciseRunner of " + userName + ", " + userEmail + ", " + uid + " {" +
				"\npoints=" + points +
				"\nexType=" + exType +
				"\nxSize=" + xSize +
				"\nySize=" + ySize +
				"\nHints=" + (hinted ?"On":"Off") +
				"\ntsUpdated=" + timeStamp + "\ntsUpdatedDateTime=" + timeStampFormattedLocal(timeStamp) +
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
	public long getTsUpdated() {return timeStamp;}
	public String getUid() {return uid;	}

	public static Exercise getExercise() {
		return exercise;
	}

	public static void setExercise(Exercise exercise) {
		ExerciseRunner.exercise = exercise;
	}

	public static ExResult getExResult() {
		return exResult;
	}

	public static void setExResult(ExResult exResult) {
		ExerciseRunner.exResult = exResult;
		ExerciseRunner.timeStamp = exResult.getTimeStamp();
	}

	public static long getId() {
		return id;
	}

	public static void setId(long id) {
		ExerciseRunner.id = id;
	}

	public static long getSeed() {
		return seed;
	}

	public static void setSeed(long seed) {
		ExerciseRunner.seed = seed;
	}

	public static void setPoints(int points) {
		ExerciseRunner.points = points;
	}

	public static void setHours(int hours) {
		ExerciseRunner.hours = hours;
	}

	public static void setLevel(int level) {
		ExerciseRunner.level = level;
	}

	public static long getTimeStamp() {
		return timeStamp;
	}

	public static void setTimeStamp(long timeStamp) {
		ExerciseRunner.timeStamp = timeStamp;
	}

	public static int getCurrentLevel() {
		return currentLevel;
	}

	public static void setCurrentLevel(int currentLevel) {
		ExerciseRunner.currentLevel = currentLevel;
	}

	/*	@Override
	public void onCallback(Map<String, Object> objectMap) {
		if (objectMap == null & uid != null) {
			// Seems a new user
			UserDbPreferences.getInstance(instance).save();
			return;
		}
		// check which source is more fresh, server's
		long sumPointsDb = ((Number)  objectMap.get("hours")).longValue() * 3600 + ((Number) objectMap.get("psyCoins")).longValue();
		// ... or local
		long sumPointsLocal = getHours() * 3600 + this.getPoints();
		if (sumPointsLocal == sumPointsDb) {
			// do nothing
		} else if (sumPointsLocal > sumPointsDb) {
			UserDbPreferences.getInstance(instance).save();
		} else {
			loadFromDbPref();
		}
	}*/  // 24.05.21 Getting rid of UserDbPreferences

/*	private void loadFromDbPref() {

		Map<String, Object> objectMap = UserDbPreferences.getInstance(instance).getObjectMap();

		uid = objectMap.get("uid").toString();
		userName = objectMap.get("name").toString();
		userEmail = objectMap.get("email").toString();
		points = ((Number) objectMap.get("psyCoins")).intValue();
		hours = ((Number) objectMap.get("hours")).intValue();
		level = ((Number) objectMap.get("level")).intValue();
		timeStamp = ((Number) objectMap.get("tsUpdated")).longValue();
		savePreferences(null);
	}*/ // 24.05.21 Getting rid of UserDbPreferences
}


