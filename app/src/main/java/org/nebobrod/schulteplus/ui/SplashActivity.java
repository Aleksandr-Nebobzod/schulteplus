/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import org.nebobrod.schulteplus.common.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.NetworkConnectivity;
import org.nebobrod.schulteplus.data.fbservices.UserDbPreferences;
import org.nebobrod.schulteplus.data.fbservices.UserFbData;
import org.nebobrod.schulteplus.data.UserHelper;

import java.util.Random;

import javax.inject.Inject;

/**
 * This class is a bit obfuscated.
 * Checks the environment, shows animation and routes user to
 * Signup or Login or Main
 */
public class SplashActivity extends AppCompatActivity  implements UserFbData.UserHelperCallback, NetworkConnectivity.ConnectivityCallback {
	public static final String TAG = "SplashActivity";
	private static final long SPLASH_STEP_TIME = 500;
	private static final long TEST_TIME_ALLOWED = 8000L; // 8 sec is maximum splash time
	private static final int TEST_TIME_IS_UP = 	0b1<<0;
	private static final int TEST_APP_PASSED = 	0b1<<1;
	private static final int TEST_USER_PASSED = 0b1<<2;
	private static final int TEST_WEB_PASSED = 	0b1<<3;
	private static final int TEST_DATA_PASSED = 0b1<<4;

	private int testPassedFlags = 0;

	private static final int TEST_RES_APP = 		0b1<<0;
	private static final int TEST_RES_USER_LOGGED_IN = 0b1<<1;
	private static final int TEST_RES_USER_EXISTS = 0b1<<2;
	private static final int TEST_RES_USER_VERIFIED = 	0b1<<3;
	private static final int TEST_RES_USER_VERIF_MESSAGE_CONFIRMED = 	0b1<<4;
	private static final int TEST_RES_WEB_EXISTS = 	0b1<<5;
	private static final int TEST_RES_DATA_STORAGE = 0b1<<6;
	private static final int TEST_RES_DATA_TG = 	0b1<<7;
	private static final int TEST_RES_DATA_VK = 	0b1<<8;
	private static final int TEST_RES_USER_VERIF_MESSAGE_SHOWN = 	0b1<<9;


	private int testResFlags = 0;

	//Hooks
	View ivBackground, clTextHolder;
	ImageView iv01App, iv02User, iv03Verified, iv04Network, iv05Data; // Icons of passed checks
	TextView tvVendor, tvStatus;
	//Animations
	Animation zoomIn, zoomOut, zoomHyper, swap;
	ColorStateList cstGreen, cstRed;

	@Inject
	NetworkConnectivity networkConnectivity;
	@Inject
	AppExecutors appExecutors;
	boolean isConnected = false;


	FirebaseAuth fbAuth;
	FirebaseUser user = null;
	UserHelper userHelper = null;

	final Handler splashMainHandler = new Handler();
	Thread thread;
	Runnable rMain = new Runnable() {
	public int count = 5;
	// (testPassedFlags != (TEST_APP_PASSED | TEST_USER_PASSED | TEST_WEB_PASSED | TEST_DATA_PASSED))
		public void run() {
			switch (count--) {
				case 5: 	// Animation
					splashMainHandler.postDelayed(this, SPLASH_STEP_TIME);
					animStart();
					// no flag is needed
					break;
				case 4:		// Self-test
					tvStatus.setText(Utils.getRes().getText(R.string.msg_app_consistency) + "...");
					Random rnd = new Random(System.currentTimeMillis());

					// couple seconds later (not a real check yet)
					splashMainHandler.postDelayed(this, rnd.nextInt(5 * (int) SPLASH_STEP_TIME));
					iv01App.setImageTintList(true ? cstGreen : cstRed);
					animThrob(iv01App,null);
					testPassedFlags |= TEST_APP_PASSED;
					testResFlags |= TEST_RES_APP; // at ver 008 it's absent so it's always true
					tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					break;
				case 3:	// User
					tvStatus.setText(Utils.getRes().getText(R.string.msg_user)+ "...");
					animThrob(iv02User,null);
					fbAuth = FirebaseAuth.getInstance();
					user = fbAuth.getCurrentUser();
					if (user == null) {
						testPassedFlags |= TEST_USER_PASSED; // No user detected so don't call UserFbData
						iv02User.setImageTintList(cstRed);
						iv03Verified.setImageTintList(cstRed);
						tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					} else {
						String strMessage;
						String email = user.getEmail();

						UserFbData.getByUid(splashUfbCallback, user.getUid());
//						UserFbData.isExist(splashUfbCallback, email.replace(".", "_"));
						iv02User.setImageTintList(cstGreen);
//						testPassedFlags |= TEST_USER_PASSED;
//						testResFlags |= TEST_RES_USER_EXISTS;
						testResFlags |= TEST_RES_USER_LOGGED_IN;

						tvStatus.setText(Utils.getRes().getText(R.string.msg_veryfied) + "...");
						animThrob(iv03Verified,null);

						if (user.isEmailVerified()){
							strMessage = email + " " + getString(R.string.msg_user_logged_in);
							iv03Verified.setImageTintList(cstGreen);
							testResFlags |= TEST_RES_USER_VERIFIED;
							Log.d(TAG, strMessage);
						} else {
							iv03Verified.setImageTintList(cstRed);
						}
						tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					}
					splashMainHandler.postDelayed(this, SPLASH_STEP_TIME);
					break;
				case 2:	// Network
//					tvStatus.setText(getResources().getText(R.string.msg_network_connection));
					tvStatus.setText(Utils.getRes().getText(R.string.msg_internet) + "...");
//					animThrob(iv04Network,null);
					appExecutors = new AppExecutors();
					networkConnectivity = new NetworkConnectivity(appExecutors, getApplicationContext());
					internetCheck(ivBackground);
					tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					splashMainHandler.postDelayed(this, SPLASH_STEP_TIME);
					break;
				case 1:	// Data sources
					tvStatus.setText(Utils.getRes().getText(R.string.msg_datasources) + "...");
					animThrob(iv05Data,null);
					iv05Data.setImageTintList(true ? cstGreen : cstRed);
					splashMainHandler.postDelayed(this, SPLASH_STEP_TIME);
					tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					break;
				default:	// Final step
					// Calling Signup Activity in case of time is over:
					if (0 != (testPassedFlags & TEST_TIME_IS_UP)) {
						// run anyhow
						Log.d(TAG, "testPassedFlags: " + Integer.toBinaryString(testPassedFlags));
						Toast.makeText(SplashActivity.this, getString(R.string.msg_tests_failed), Toast.LENGTH_LONG).show();
						runActivity(SignupActivity.class);

					} else { //Did all the tests pass?
						if (0 != (testPassedFlags & (TEST_APP_PASSED | TEST_USER_PASSED | TEST_WEB_PASSED | TEST_DATA_PASSED))) {
							// Can we run to MainActivity?
							if (0 != (testResFlags & TEST_RES_USER_EXISTS)) {
								ExerciseRunner.getInstance(userHelper); // user found
								ExerciseRunner.setOnline(0 != (testResFlags & TEST_RES_WEB_EXISTS));
								if (0 != (testResFlags & TEST_RES_USER_VERIFIED)) {
									// Yes all done
									runMainActivity(userHelper);
								} else { // User has to confirm he isn't verified
//									if (thread.isAlive()) thread.interrupt(); // No need run by time anymore...
									// <- this changed: threaded runnable looks for flag USER_VERIF_MESSAGE_SHOWN ->
									Log.d(TAG, "run NO verified, user: " + userHelper);
									Log.d(TAG, "run NO verified, dbPref: " + UserDbPreferences.getInstance(ExerciseRunner.getInstance()).getObjectMap().toString() );
									String strMessage = userHelper.getName() + ", "
											+ getString(R.string.msg_user_unverified);
									if (0 == (testResFlags & TEST_RES_USER_VERIF_MESSAGE_SHOWN)) {
										showSnackBarConfirmation(SplashActivity.this, strMessage, new View.OnClickListener() {
											@Override
											public void onClick(View view) {
												testResFlags |= TEST_RES_USER_VERIF_MESSAGE_CONFIRMED;
												testResFlags |= TEST_RES_USER_VERIFIED; // just to skip click processing as it same with VERIF
											}
										});
										testResFlags |= TEST_RES_USER_VERIF_MESSAGE_SHOWN;
									}
									splashMainHandler.postDelayed(this, SPLASH_STEP_TIME);
								}
							}
							else { // Or wait if there is no User logged before
								splashMainHandler.postDelayed(this, SPLASH_STEP_TIME);
							}
						}
						else { // Tests not passed
							splashMainHandler.postDelayed(this, SPLASH_STEP_TIME);
						}
					}

				// end switch
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Layout
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		View mContentView = findViewById(R.id.iv_background);
		// Hide UI first
/*		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}*/
		// And Navigation Bar
		if (Build.VERSION.SDK_INT >= 30) {
			mContentView.getWindowInsetsController().hide(
					WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
		} else {
			// Note that some of these constants are new as of API 16 (Jelly Bean)
			// and API 19 (KitKat). It is safe to use them, as they are inlined
			// at compile-time and do nothing on earlier devices.
			mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}

//		this.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
		// Hook links
		ivBackground = findViewById(R.id.iv_background);
		clTextHolder = findViewById(R.id.cl_text_holder);
		//Animation Calls
//		zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
//		zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
		zoomHyper = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);

		// Icons of passed checks
		iv01App = findViewById(R.id.iv_01_app);
			iv01App.setOnClickListener(view -> animThrob(iv01App,null));
		iv02User = findViewById(R.id.iv_02_user);
		iv03Verified = findViewById(R.id.iv_03_verified);
		iv04Network = findViewById(R.id.iv_04_network);
		iv05Data = findViewById(R.id.iv_05_data);
		// Text of clarification
		tvStatus = findViewById(R.id.tv_status);
		tvVendor = findViewById(R.id.tv_vendor);
		tvVendor.setOnClickListener(view -> {
			runMainActivity(null);
		});
		// Colors for Icons of passed checks
		cstGreen = ColorStateList.valueOf(getColor(R.color.light_grey_A_green));
		cstRed = ColorStateList.valueOf(getColor(R.color.light_grey_A_red));


		//  Runnable, which counts deadline time
		Runnable limitUiIdleTime = new Runnable() {
			public void run() {
				long endTime = System.currentTimeMillis() + TEST_TIME_ALLOWED;

				while (System.currentTimeMillis() < endTime) {
					synchronized (this) {
						try {
							wait(endTime -
									System.currentTimeMillis());
						} catch (Exception e) {
						}
					}
				}
				if (0 != (testResFlags & TEST_RES_USER_VERIF_MESSAGE_SHOWN)) {
					// waiting for user response
					Log.d(TAG, "No checking time in: " + Thread.currentThread());
				} else {
					testPassedFlags |= TEST_TIME_IS_UP; // bitwise conjunction says that allowed time is gone
					Log.d(TAG, "BTP time's up in: " + Thread.currentThread());
				}
			}
		};

		// Start it in a new thread
		thread = new Thread(null, limitUiIdleTime,
				"Background");
		thread.start();

		//Splash Screen Code to call new Activity after some time
		rMain.run();

	}


///////////////////////////////////////

	@Override
	protected void onStart() {
		super.onStart();
//		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
//			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		}
	}

	// UserFb methods callback
	@Override
	public void onCallback(UserHelper value) { }
	// This strange construction allow avoid error: Non-static method cannot be referenced...
	private final UserFbData.UserHelperCallback splashUfbCallback = new UserFbData.UserHelperCallback() {
		@Override
		public void onCallback(UserHelper value)
		{
			Log.d(TAG, "onCallback: " + value);
			testPassedFlags |= TEST_USER_PASSED;
			if (value == null) {
//				no flag to rise
			} else {
				userHelper = value;
				testResFlags |= TEST_RES_USER_EXISTS;
				ExerciseRunner.getInstance(userHelper);
			}
		}
	};

	// Connectivity check callback
	@Override
	public void onDetected(boolean isConnected) {
		Log.d(TAG, "onDetected,  isConnected: " + isConnected);
	}

/*	View.OnClickListener rapid (View view) -> {
		@Override
		public void onClick(View view) {
			SignupActivity.signInAnonymously();
		}
	};*/
	////////////////////////////////////////////////////

	public void internetCheck(View view) { //view is for test
		networkConnectivity.checkInternetConnection((isConnected) -> {
			if (isConnected) {
				testPassedFlags |= TEST_WEB_PASSED;
				testResFlags |= TEST_RES_WEB_EXISTS;
			}
			iv04Network.setImageTintList(isConnected ? cstGreen : cstRed);
			animThrob(iv04Network, null);
		}, null);
	}

	private void animStart() {
		clTextHolder.setVisibility(View.VISIBLE);
		clTextHolder.setAnimation(zoomHyper);
		testPassedFlags |= TEST_APP_PASSED; // bitwise conjunction to shared flags that test passed
	}

	private void runActivity(Class<?> cls)
	{
		Intent intent = new Intent(this, cls);
		startActivity(intent);
		finish();
	}
	private void runMainActivity(UserHelper user)
	{
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
		finish();
	}



}