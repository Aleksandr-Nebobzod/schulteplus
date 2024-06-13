/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

//package com.example.networkconnectivityapplication;
// https://github.com/amrsalah3/CheckInternetConnectivity/blob/main/AppExecutors.java

import android.os.Handler;
import android.os.Looper;
import org.nebobrod.schulteplus.common.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;

@Singleton
public class AppExecutors {
	private final String TAG = getClass().getName();

	private final Executor networkIO;
	private final Executor diskIO;
	private final Executor mainThread;

	@Inject
	public AppExecutors() {
		this.networkIO = Executors.newSingleThreadExecutor(r -> new Thread(r, "NetworkIOThread"));
		this.diskIO = Executors.newFixedThreadPool(3, r -> new Thread(r, "DiskIOThread"));
		this.mainThread = new MainThreadExecutor();
	}

	public Executor getNetworkIO() {
		Log.d(TAG, "getNetworkIO: " + networkIO.toString());
		return networkIO;
	}

	public Executor getDiskIO() {
		Log.d(TAG, "getDiskIO: " + diskIO.toString());
		return diskIO;
	}

	public Executor mainThread() {
		Log.d(TAG, "getMainThread: " + diskIO.toString());
		return mainThread;
	}

	private static class MainThreadExecutor implements Executor {
		private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

		@Override
		public void execute(@NonNull Runnable command) {
			mainThreadHandler.post(command);
		}
	}
}
