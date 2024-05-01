/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

//package com.example.networkconnectivityapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.Log;

import androidx.annotation.Nullable;

import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;

/**
 * provides checks for splash-screen
 */
@Singleton
public class NetworkConnectivity {
	private String TAG = getClass().getSimpleName(); // TODONE: 23.11.2023 check for others

	private final AppExecutors appExecutors;
	private final Context context;

	@Inject
	public NetworkConnectivity(AppExecutors appExecutors, Context context) {
		this.appExecutors = appExecutors;
		this.context = context;
	}

	public synchronized void checkInternetConnection(ConnectivityCallback callback, @Nullable String specUrl) {
		appExecutors.getNetworkIO().execute(() -> {
			Log.d(TAG, "checkInternetConnection: " + Thread.currentThread());
			if (isNetworkAvailable()) {
				HttpsURLConnection connection = null;
				try {
//					String spec = UserFbData.DB_URL;
//					String spec =  (specUrl == null ? "https://www.google.com/humans.txt" : specUrl);
//					connection = (HttpsURLConnection) new URL("https://clients3.google.com/generate_204").openConnection();
//					connection = (HttpsURLConnection) new URL(spec).openConnection();
					connection = (HttpsURLConnection) new URL("https://www.google.com/humans.txt").openConnection();
					connection.setRequestProperty("User-Agent", "Android");
					connection.setRequestProperty("Connection", "close");
					connection.setConnectTimeout(1000);
//					connection.setReadTimeout (10 * 100);
//					connection.connect();

					boolean isConnected = connection.getResponseCode() == 200; // && connection.getContentLength() == 286 for humans.txt;
					postCallback(isConnected, callback);
					connection.disconnect();
				} catch (Exception e) {
					postCallback( false, callback);
					if(connection != null) connection.disconnect();
				}
			} else {
				postCallback(false, callback);
			}
		});
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) return false;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
			if (cap == null) return false;
			return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Network[] networks = cm.getAllNetworks();
			for (Network n : networks) {
				NetworkInfo nInfo = cm.getNetworkInfo(n);
				if (nInfo != null && nInfo.isConnected()) return true;
			}
		} else {
			NetworkInfo[] networks = cm.getAllNetworkInfo();
			for (NetworkInfo nInfo : networks) {
				if (nInfo != null && nInfo.isConnected()) return true;
			}
		}

		return false;
	}

	public interface ConnectivityCallback {
		void onDetected(boolean isConnected);
	}

	private void postCallback(boolean isConnected, ConnectivityCallback callBack) {
		appExecutors.mainThread().execute(() -> callBack.onDetected(isConnected));
	}

}
