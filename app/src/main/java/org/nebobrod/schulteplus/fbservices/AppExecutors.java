/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

//package com.example.networkconnectivityapplication;
// https://github.com/amrsalah3/CheckInternetConnectivity/blob/main/AppExecutors.java

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
		this.networkIO = Executors.newSingleThreadExecutor();
		this.diskIO = Executors.newFixedThreadPool(3);
		this.mainThread = new MainThreadExecutor();

		Log.d(TAG, "AppExecutors: " + networkIO.toString());
	}


	public Executor getNetworkIO() {
		return networkIO;
	}

	public Executor getDiskIO() {
		return diskIO;
	}

	public Executor mainThread() {
		return mainThread;
	}

	private static class MainThreadExecutor implements Executor {
		private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

		@Override
		public void execute(@NonNull Runnable command) {
			mainThreadHandler.post(command);
		}
	}

}
