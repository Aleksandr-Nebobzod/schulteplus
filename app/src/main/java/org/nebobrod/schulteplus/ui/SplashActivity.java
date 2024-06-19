/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.animThrob;
import static org.nebobrod.schulteplus.Utils.showSnackBarConfirmation;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.common.NetworkConnectivity;
import org.nebobrod.schulteplus.data.UserHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity {

	private static final String TAG = "SplashActivity";

	private static final long TEST_TIME_ALLOWED = 8000L; // 8 sec is maximum splash time

	private SplashViewModel viewModel;
	private boolean[] testsCompleted = new boolean[4];

	private View ivBackground, clTextHolder;
	private ImageView iv01App, iv02User, iv03Verified, iv04Network, iv05Data;
	private TextView tvVendor, tvStatus;
	private Animation zoomHyper;
	private ColorStateList cstGreen, cstYellow, cstRed;

	@Inject
	NetworkConnectivity networkConnectivity;
	@Inject
	AppExecutors appExecutors = new AppExecutors();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		initializeUI();

		viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

		// Watch for Checks results
		viewModel.getCheckResult().observe(this, result -> {
			android.util.Log.d(TAG, "getCheckResult().observe: ");
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
			}
			testsCompleted[result.getType().ordinal()] = true;

			evaluateResults();

		});

		// Watch for Checks passed
		viewModel.getSplashState().observe(this, splashState -> {
			android.util.Log.d(TAG, "getSplashState().observe: " + splashState);
			switch (splashState) {
				case FINISH:

					break;
				default:
					break;
			}
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
		cstRed = ColorStateList.valueOf(getColor(R.color.light_grey_A_red));
		cstYellow = ColorStateList.valueOf(getColor(R.color.light_grey_A_yellow));
		cstGreen = ColorStateList.valueOf(getColor(R.color.light_grey_A_green));
	}

	private void handleAppCheckResult(SplashViewModel.InitialCheck check) {
		switch (check.getResult()) {
			case OK:
				// no-op
				iv01App.setImageTintList(true ? cstGreen : cstRed);
				break;
			case WARN:
				iv01App.setImageTintList(cstYellow);
				showSnackBarConfirmation(this, check.getMessage(), view -> {
					// Confirmed deprecating but OK
				});
				break;
			case ERROR:
				iv01App.setImageTintList(cstRed);
				showSnackBarConfirmation(this, check.getMessage(), view -> {
					finishAffinity();
					System.exit(0);
				});
				break;
		}
		animThrob(iv01App,null);
	}

	private void handleUserCheckResult(SplashViewModel.InitialCheck check) {
		UserHelper userHelper = viewModel.getUserHelper();
		switch (check.getResult()) {
			case OK:
				// no-op
				iv02User.setImageTintList(cstGreen);
				iv03Verified.setImageTintList(cstGreen);
				break;
			case WARN:
				iv02User.setImageTintList(cstGreen);
				iv03Verified.setImageTintList(cstRed);

				Log.d(TAG, "run NO verified, user: " + userHelper); // How can we get userHelper from Viewmodel here?
				String strMessage = userHelper.getName() + ", "
						+ getString(R.string.msg_user_unverified);
				// User has to confirm he isn't verified
				showSnackBarConfirmation(this, strMessage, new View.OnClickListener() {
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

		if (check.getResult() == SplashViewModel.CheckResult.OK) {
			iv04Network.setImageTintList(cstGreen);
		} else {
			iv04Network.setImageTintList(cstRed);
		}

		animThrob(iv04Network, null);
	}

	private void handleDataCheckResult(SplashViewModel.InitialCheck result) {

		animThrob(iv05Data,null);
		iv05Data.setImageTintList(true ? cstGreen : cstRed);
	}

	private void evaluateResults() {
		boolean allTestsCompleted = true;
		for (boolean completed : testsCompleted) {
			if (!completed) {
				allTestsCompleted = false;
				break;
			}
		}

		if (allTestsCompleted) {
			runMainActivity(viewModel.getUserHelper());
		}
	}

	private void animStart() {
		clTextHolder.setVisibility(View.VISIBLE);
		clTextHolder.setAnimation(zoomHyper);
	}

	private void runActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
		finish();
	}
	private void runMainActivity(UserHelper user) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
		finish();
	}
}

