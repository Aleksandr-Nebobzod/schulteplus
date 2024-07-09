/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.animThrob;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.common.NetworkConnectivity;
import org.nebobrod.schulteplus.common.SnackBarManager;
import org.nebobrod.schulteplus.data.UserHelper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.Arrays;

import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity {

	private static final String TAG = "SplashActivity";

	private SplashViewModel viewModel;
	private boolean[] testsCompleted = new boolean[5];
	private SplashViewModel.InitialCheck[] initialChecks = new SplashViewModel.InitialCheck[5];
	UserHelper userHelper;

	private View ivBackground, clTextHolder;
	private ImageView iv01App, iv02User, iv03Verified, iv04Network, iv05Data;
	private TextView tvVendor, tvStatus;
	private Animation zoomHyper;
	private ColorStateList cstGreen, cstYellow, cstRed;
	private SnackBarManager snackBarManager;

	@Inject
	NetworkConnectivity networkConnectivity;
	@Inject
	AppExecutors appExecutors = new AppExecutors();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		snackBarManager = new SnackBarManager(this).setPostponed(true);
		initializeUI();

		viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

		viewModel.getUserHelperLD().observe(this, userHelper -> {
			if (userHelper != null) SplashActivity.this.userHelper = userHelper;
		});

		// Watch for Checks results
		viewModel.getCheckResult().observe(this, result -> {
			Log.d(TAG, "getCheckResult().observe: " + result);
			testsCompleted[result.getType().ordinal()] = true;
			initialChecks[result.getType().ordinal()] = result;

			switch (result.getType()) {
				case APP:
					handleAppCheckResult(result);
					break;
				case USER:
					handleUserCheckResult(result);
					break;
				case NETWORK:
					handleNetworkCheckResult(result);
					break;
				case DATA:
					handleDataCheckResult(result);
					break;
				case TIME:
					handleTimeCheckResult(result);

					break;
			}

			evaluateResults();

		});

		// Set up a listener to start the process once the view is fully laid out
		final View rootView = findViewById(android.R.id.content);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// Start the splash process
				viewModel.startSplashProcess();

				// Remove the listener to prevent it from being called multiple times
				rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

		animStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (viewModel != null) {
			viewModel.onCleared();
			this.getViewModelStore().clear();
		}
		Log.d("SplashActivity", "Activity destroyed");
	}

	private void initializeUI() {
		clTextHolder = findViewById(R.id.cl_text_holder);
		zoomHyper = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);
		iv01App = findViewById(R.id.iv_01_app);
		iv02User = findViewById(R.id.iv_02_user);
		iv03Verified = findViewById(R.id.iv_03_verified);
		iv04Network = findViewById(R.id.iv_04_network);
		iv05Data = findViewById(R.id.iv_05_data);
		tvStatus = findViewById(R.id.tv_status);
		tvVendor = findViewById(R.id.tv_vendor);
		tvVendor.setOnClickListener(view -> {
			// Little cheat-code
			Toast.makeText(this, "Run Demo User...", Toast.LENGTH_LONG).show();
			new Handler(Looper.getMainLooper()).postDelayed(() -> {
				runMainActivity(null);
				this.finish();
			}, 1000);
		});

		cstRed = ColorStateList.valueOf(getColor(R.color.light_grey_A_red));
		cstYellow = ColorStateList.valueOf(getColor(R.color.light_grey_A_yellow));
		cstGreen = ColorStateList.valueOf(getColor(R.color.light_grey_A_green));
	}

	private void handleAppCheckResult(SplashViewModel.InitialCheck check) {
		Log.d(TAG, "handleAppCheckResult: " + check);
		switch (check.getResult()) {
			case OK:
				// no-op
				iv01App.setImageTintList(true ? cstGreen : cstRed);
				break;
			case WARN:
				iv01App.setImageTintList(cstYellow);
				snackBarManager.queueMessage(check.getMessage(), null);
				break;
			case ERROR:
				iv01App.setImageTintList(cstRed);
				snackBarManager.queueMessage(check.getMessage(), view -> {
					finishAffinity();
					System.exit(0);
				});
				break;
		}
		animThrob(iv01App,null);
	}

	private void handleUserCheckResult(SplashViewModel.InitialCheck check) {

		Log.d(TAG, "handleUserCheckResult: " + check);

		switch (check.getResult()) {
			case OK:
				// no-op
				iv02User.setImageTintList(cstGreen);
				iv03Verified.setImageTintList(cstGreen);
				break;
			case WARN:
				iv02User.setImageTintList(cstGreen);
				iv03Verified.setImageTintList(cstRed);

				if (userHelper == null) {
					iv02User.setImageTintList(cstRed);
					Log.w(TAG, "handleUserCheckResult: NO USERHELPER");
					viewModel.postCheckResult(new SplashViewModel.InitialCheck(SplashViewModel.CheckType.USER, SplashViewModel.CheckResult.ERROR, ""));
					break;
				}

				Log.d(TAG, "run NO verified, user: " + userHelper); // How can we get userHelper from Viewmodel here?
				String strMessage = userHelper.getName() + ", "
						+ getString(R.string.msg_user_unverified);
				// User has to confirm he isn't verified
				snackBarManager.queueMessage(strMessage, new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// And here we can even set  InitialCheck.result = OK
						// just to skip click processing as it same with VERIFIED
						// it's wrong: testsCompleted[SplashViewModel.SplashState.USER_CHECK.ordinal()] = true;
						viewModel.postCheckResult(new SplashViewModel.InitialCheck(
								SplashViewModel.CheckType.USER,
								SplashViewModel.CheckResult.OK, "Verification informed"));
					}
				});
				break;
			case ERROR:
				iv02User.setImageTintList(cstRed);
				iv03Verified.setImageTintList(cstRed);

				// we can run this but only after all checks passed without hard ERROR
				// runActivity(SignupActivity.class);

				break;
		}
	}

	private void handleNetworkCheckResult(SplashViewModel.InitialCheck check) {

		Log.d(TAG, "handleNetworkCheckResult: " + check);
		if (check.getResult() == SplashViewModel.CheckResult.OK) {
			iv04Network.setImageTintList(cstGreen);
		} else {
			iv04Network.setImageTintList(cstRed);
		}

		animThrob(iv04Network, null);
	}

	private void handleDataCheckResult(SplashViewModel.InitialCheck check) {

		Log.d(TAG, "handleDataCheckResult: " + check);
		iv05Data.setImageTintList(true ? cstGreen : cstRed);
		animThrob(iv05Data,null);
	}

	private void handleTimeCheckResult(SplashViewModel.InitialCheck check) {
		if (check.getResult() == SplashViewModel.CheckResult.WARN) {
			Toast.makeText(this, getString(R.string.msg_tests_failed), Toast.LENGTH_LONG).show();
			new Handler(Looper.getMainLooper()).postDelayed(() -> {
				snackBarManager.showAllQueue(this::finishActivityBasedOnUserHelper);
			}, 1000);
		}
	}

	private void finishActivityBasedOnUserHelper() {
		if (userHelper == null) {
			runActivity(LoginActivity.class);
		} else {
			runMainActivity(userHelper);
		}
	}


	private void evaluateResults() {
		Log.d(TAG, "evaluateResults: " + Arrays.toString(testsCompleted));
		boolean allTestsCompleted = true;
		for (boolean completed : testsCompleted) {
			if (!completed) {
				allTestsCompleted = false;
				break;
			}
		}

		if (allTestsCompleted) {
			snackBarManager.setPostponed(false).showAllQueue(() -> {
				new Handler(Looper.getMainLooper()).post(() -> {
					if (initialChecks[SplashViewModel.CheckType.USER.ordinal()].getResult() == SplashViewModel.CheckResult.ERROR
							|| userHelper == null) {
						runActivity(LoginActivity.class);
					} else {
						runMainActivity(userHelper);
					}
				});
			});
		}
	}

	private void animStart() {
		clTextHolder.setVisibility(View.VISIBLE);
		clTextHolder.setAnimation(zoomHyper);
	}

	private void runActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		this.finish();
		startActivity(intent);
	}
	private void runMainActivity(UserHelper user) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("user", user);
		this.finish();
		startActivity(intent);
	}
}

