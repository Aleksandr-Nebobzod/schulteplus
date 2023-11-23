/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.*;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.nebobrod.schulteplus.MainActivity;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.fbservices.AppExecutors;
import org.nebobrod.schulteplus.fbservices.NetworkConnectivity;
import org.nebobrod.schulteplus.fbservices.SignupActivity;
import org.nebobrod.schulteplus.fbservices.UserFbData;
import org.nebobrod.schulteplus.fbservices.UserHelper;

import java.util.Random;

import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity  implements UserFbData.UserHelperCallback, NetworkConnectivity.ConnectivityCallback {
	public static final String TAG = "SplashActivity";
	private static int SPLASH_TIME_OUT = 700;
	private View mContentView;

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

	public static int count = 5;

	FirebaseAuth fbAuth;
	FirebaseUser user = null;
	UserHelper userHelper = null;
	UserFbData userFbData = null;

	final Handler splashMainHandler = new Handler();
	Runnable r = new Runnable() {
		public void run() {
			switch (count--) {
				case 5: 	// Animation
					clTextHolder.setVisibility(View.VISIBLE);
					splashMainHandler.postDelayed(this, SPLASH_TIME_OUT);
					clTextHolder.setAnimation(zoomHyper);
					break;
				case 4:		// Selftest
					tvStatus.setText(Utils.getRes().getText(R.string.msg_app_consistency)+ "...");
					Random rnd = new Random(System.currentTimeMillis());
					splashMainHandler.postDelayed(this, rnd.nextInt(700)*3);
					iv01App.setImageTintList(true ? cstGreen : cstRed);
					animThrob(iv01App);
					tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					break;
				case 3:
					tvStatus.setText(Utils.getRes().getText(R.string.msg_user)+ "...");
					animThrob(iv02User);
					fbAuth = FirebaseAuth.getInstance();
					user = fbAuth.getCurrentUser();
					if (user == null) {
						iv02User.setImageTintList(cstRed);
						iv03Verified.setImageTintList(cstRed);
						tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					} else {
						iv02User.setImageTintList(cstGreen);

						tvStatus.setText(Utils.getRes().getText(R.string.msg_veryfied) + "...");
						animThrob(iv03Verified);

						String strMessage, email = user.getEmail();
						if (user.isEmailVerified()){
							strMessage = email + " " + getString(R.string.msg_user_logged_in);
							iv03Verified.setImageTintList(cstGreen);
							Log.d(TAG, strMessage);
						} else {
							strMessage = email + ", " + getString(R.string.msg_user_unverified);
							iv03Verified.setImageTintList(cstRed);
							Log.d(TAG, strMessage);
							showSnackBarConfirmation(SplashActivity.this, strMessage);
						}
						tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
						UserFbData.isExist(splashUfbCallback, email.replace(".", "_"));
					}



					splashMainHandler.postDelayed(this, SPLASH_TIME_OUT);

					break;
				case 2:
//					tvStatus.setText(getResources().getText(R.string.msg_network_connection));
					tvStatus.setText(Utils.getRes().getText(R.string.msg_internet) + "...");
					animThrob(iv04Network);
					appExecutors = new AppExecutors();
					networkConnectivity = new NetworkConnectivity(appExecutors, getApplicationContext());
					internetCheck(ivBackground);
					splashMainHandler.postDelayed(this, SPLASH_TIME_OUT);
					tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					break;
				case 1:
					tvStatus.setText(Utils.getRes().getText(R.string.msg_datasources) + "...");
					animThrob(iv05Data);
					splashMainHandler.postDelayed(this, SPLASH_TIME_OUT);
					iv05Data.setImageTintList(true ? cstGreen : cstRed);
					tvStatus.setText(Utils.getRes().getText(R.string.str_empty));
					break;
				default:
					// Calling necessary Activity:
//					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//					startActivity(intent);
//					finish();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Layout
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		mContentView = findViewById(R.id.iv_background);
		// Hide UI first
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
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
		zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
		zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
		zoomHyper = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);

		// Icons of passed checks
		iv01App = findViewById(R.id.iv_01_app);
			iv01App.setOnClickListener(view -> animThrob(iv01App));
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
		// Colors
		cstGreen = ColorStateList.valueOf(getColor(R.color.light_grey_A_green));
		cstRed = ColorStateList.valueOf(getColor(R.color.light_grey_A_red));


		//Splash Screen Code to call new Activity after some time
		r.run();

		Handler mHandler;

		// Объект Runnable, который запускает метод для выполнения задач
		// в фоновом режиме.
		Runnable doBackgroundThreadProcessing = new Runnable() {
			public void run() {
				Log.d(TAG, "BTP: " + Thread.currentThread());
			}
		};

		// Здесь трудоемкие задачи переносятся в дочерний поток.
		Thread thread = new Thread(null, doBackgroundThreadProcessing,
				"Background");
		thread.start();

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
	private final UserFbData.UserHelperCallback splashUfbCallback = new UserFbData.UserHelperCallback()
	{
		@Override
		public void onCallback(UserHelper fbDbUser)
		{
			Log.d(TAG, "onCallback: " + fbDbUser);
			if (fbDbUser == null) {
				runSignupActivity();
			} else {
				runMainActivity(fbDbUser);
			}
		}
	};

	// Connectivity check callback
	@Override
	public void onDetected(boolean isConnected) {

	}

/*	View.OnClickListener rapid (View view) -> {
		@Override
		public void onClick(View view) {
			SignupActivity.signInAnonymously();
		}
	};*/
	////////////////////////////////////////////////////

	public void internetCheck(View view) { //view is for test
		networkConnectivity.checkInternetConnection((isConnected) ->
						iv04Network.setImageTintList(isConnected ? cstGreen : cstRed)
//				Toast.makeText(this, isConnected + "", Toast.LENGTH_SHORT).show()
		);
	}

	private void animThrob(View view) {
/*		view.animate().scaleX(2).setDuration(500).setStartDelay(0);
		view.animate().scaleX(1).setDuration(1000).setStartDelay(600);
		view.animate().scaleYBy(3F).scaleXBy(3F).setDuration(200).setStartDelay(0);
		view.animate()
				.scaleX(1F)
				.scaleXBy(0.75F)
				.scaleY(1F)
				.scaleYBy(0.75F)
				.setDuration(750)
				.setStartDelay(0);*/

		AnimationSet aSet = new AnimationSet(true);

		ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation1.setDuration(300);
//		ScaleAnimation scaleAnimation2 = new ScaleAnimation(0.5f, 1.5f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
//		scaleAnimation2.setDuration(100);
		aSet.addAnimation(scaleAnimation1);
//		aSet.addAnimation(scaleAnimation2);
		view.startAnimation(aSet);

		Log.d(TAG, "animThrob: " + Thread.currentThread());

	}

	private void runSignupActivity()
	{
		Intent intent = new Intent(this, SignupActivity.class);
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