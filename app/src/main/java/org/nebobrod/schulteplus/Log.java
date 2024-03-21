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

import androidx.viewbinding.BuildConfig;

public class Log {
	static final boolean LOG = BuildConfig.DEBUG;

	public static void i(String tag, String string) {
		if (LOG) android.util.Log.i(tag, string);
	}

	public static void d(String tag, String string) {
		if (LOG) android.util.Log.d(tag, string);
	}
	public static void v(String tag, String string) {
		if (LOG) android.util.Log.v(tag, string);
	}
	public static void w(String tag, String string) {
		if (LOG) {
			android.util.Log.w(tag, string);
		} else {
			logFbCrash(tag +": " + string);
		}
	}
	public static void e(String tag, String string, Exception e) {
		if (LOG) {
			android.util.Log.e(tag, string, e);
		} else {
			logFbCrash(tag +": " + string);
			exceptionFbCrash(e);
		}
	}
}
