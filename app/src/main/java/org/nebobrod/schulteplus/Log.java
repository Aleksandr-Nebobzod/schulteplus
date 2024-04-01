/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.Utils.exceptionFbCrash;
import static org.nebobrod.schulteplus.Utils.logFbCrash;

import org.nebobrod.schulteplus.BuildConfig;

public class Log {
	static final boolean LOG = BuildConfig.DEBUGGING;
	// static final boolean LOG2 = (boolean) getBuildConfigValue("DEBUGGING"); // this attempt was until correct import
	static final String PREFIX = ("MyLog ");
	static final boolean DB = BuildConfig.ENABLE_CRASHLYTICS;

	public static void i(String tag, String string) {
		if (LOG) android.util.Log.i(PREFIX + tag, string);
	}

	public static void d(String tag, String string) {
		if (LOG) android.util.Log.d(PREFIX + tag, string);
	}
	public static void v(String tag, String string) {
		if (LOG) {
			android.util.Log.v(PREFIX + tag, string);
		} else {
			logFbCrash("V." + PREFIX + tag +": " + string);
		}
	}
	public static void w(String tag, String string) {
		if (LOG) {
			android.util.Log.w(PREFIX + tag, string);
		} else {
			logFbCrash("W." + PREFIX + tag +": " + string);
		}
	}
	public static void e(String tag, String string, Exception e) {
		if (LOG) {
			android.util.Log.e(PREFIX + tag, string, e);
		} else {
			logFbCrash(PREFIX + tag +": " + string);
			exceptionFbCrash(e);
		}
	}
}
