/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import static org.nebobrod.schulteplus.Utils.exceptionFbCrash;
import static org.nebobrod.schulteplus.Utils.logFbCrash;

import android.os.Looper;

import org.nebobrod.schulteplus.BuildConfig;

public class Log {
	static final boolean LOG = BuildConfig.DEBUGGING;
	// static final boolean LOG2 = (boolean) getBuildConfigValue("DEBUGGING"); // this attempt was until correct import
	static final String PREFIX = ("MyLog ");
	static final boolean DB = BuildConfig.ENABLE_CRASHLYTICS;

	public static void i(String tag, String string) {
		//					Log.d(TAG, "fetchResultsLimited, is MainLooper1?: " + (Looper.myLooper() == Looper.getMainLooper()));
//					Log.d(TAG, "fetchResultsLimited, is MainLooper2?: " + (Looper.getMainLooper().getThread() == Thread.currentThread()));
		if (LOG) android.util.Log.i(PREFIX + tag, string + " is Thread main?=" + Looper.getMainLooper().isCurrentThread()
				+ "\nThread is: " + Thread.currentThread());
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
