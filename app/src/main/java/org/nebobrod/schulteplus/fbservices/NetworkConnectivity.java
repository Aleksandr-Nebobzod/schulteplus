/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

//package com.example.networkconnectivityapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;

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

	public synchronized void checkInternetConnection(ConnectivityCallback callback) {
		appExecutors.getNetworkIO().execute(() -> {
			Log.d(TAG, "checkInternetConnection: " + Thread.currentThread());
			if (isNetworkAvailable()) {
				HttpsURLConnection connection = null;
				try {
					connection = (HttpsURLConnection)
							new URL("https://clients3.google.com/generate_204").openConnection();
					connection.setRequestProperty("User-Agent", "Android");
					connection.setRequestProperty("Connection", "close");
					connection.setConnectTimeout(1000);
					connection.connect();

					boolean isConnected = connection.getResponseCode() == 204 && connection.getContentLength() == 0;
					postCallback(callback, isConnected);
					connection.disconnect();
				} catch (Exception e) {
					postCallback(callback, false);
					if(connection != null) connection.disconnect();
				}
			} else {
				postCallback(callback, false);
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

	private void postCallback(ConnectivityCallback callBack, boolean isConnected) {
		appExecutors.mainThread().execute(() -> callBack.onDetected(isConnected));
	}

}
