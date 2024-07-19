/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;


import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.getTopRightCornerRect;
import static org.nebobrod.schulteplus.Utils.showSnackBarConfirmation;
import static org.nebobrod.schulteplus.common.Const.SHOWN_00_MAIN;

import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.databinding.ActivityMainBinding;
import org.nebobrod.schulteplus.data.UserHelper;
import org.nebobrod.schulteplus.ui.basics.BasicsActivity;
import org.nebobrod.schulteplus.ui.schulte.SchulteActivity;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Main screen dispatches other activities and keeps ExerciseRunner actual
 */
public class MainActivity extends AppCompatActivity {
	public static final String TAG = "MainActivity";
	private static final String PLAY_STORE_TAG = "com.android.vending";

	private ActivityMainBinding binding;
	private UserHelper userHelper;

	private AppUpdateManager appUpdateManager;
	private ActivityResultLauncher<IntentSenderRequest> appUpdateLauncher;

	private ExerciseRunner runner;
	private FloatingActionButton fabLaunch;

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu); // this is a one button
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				((AppCompatDialogFragment) Fragment.instantiate(this, PrefsPopupSettingsFragment.class.getName()))
						.show(getSupportFragmentManager(), PrefsPopupSettingsFragment.class.getName());
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedMAInstanceState) {
		super.onCreate(savedMAInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		// get userHelper from Intent
		if(getIntent() != null & getIntent().hasExtra("user")) {
			userHelper = (UserHelper) getIntent().getExtras().getSerializable("user");
		}

		// if no user at all
		runner = ( null != userHelper ? ExerciseRunner.getInstance(userHelper) : ExerciseRunner.getInstance());

		// Check the updates
		{
			PackageManager packageManager = this.getPackageManager();
			String packageName = this.getPackageName();
			String installerName = packageManager.getInstallerPackageName(packageName);
			Log.w(TAG, "PLAY_STORE_OR NOT installerName: " + installerName);

			// Check for official version Update
			appUpdateManager = AppUpdateManagerFactory.create(this);
			appUpdateLauncher = registerForActivityResult(
					new ActivityResultContracts.StartIntentSenderForResult(),
					result -> {
						if (result.getResultCode() != RESULT_OK) {
							String errMessage = getString(R.string.msg_unable_reach_update);
							Log.w(TAG, "onCreate, StartIntentSenderForResult: " + errMessage);
							showSnackBarConfirmation(MainActivity.this, errMessage, null);
						}
					});

			// Returns an intent object that you use to check for an update.
			Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

			// Checks that the platform will allow the specified type of update.
			appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
				if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
						// This example applies an immediate update.
						&& appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
					// Request the update.
					appUpdateManager.startUpdateFlowForResult(
							// Pass the intent that is returned by 'getAppUpdateInfo()'.
							appUpdateInfo,
							appUpdateLauncher,
							AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
				}
			});

		}

		// Init Toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//ActionBar as a toolbar
		androidx.appcompat.app.ActionBar mainActionBar = this.getSupportActionBar();
		int abColor =  getWindow().getStatusBarColor();
		mainActionBar.setLogo(R.drawable.ic_logo_100);
		mainActionBar.setDisplayUseLogoEnabled(true);
		mainActionBar.setDisplayShowHomeEnabled(true);

/*
//		mainActionBar.hide();

//		View view = mainActionBar.getCustomView();

//		mainActionBar.setDisplayShowCustomEnabled(true);
//		mainActionBar.setCustomView(R.layout.action_bar);
//		mainActionBar.setTitle("T.h.e. .t.i.t.l.e");
//		mainActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//		mainActionBar.setIcon(R.drawable.ic_schulte_black_24dp);
//		mainActionBar.setDisplayHomeAsUpEnabled(true);
//		mainActionBar.setHomeAsUpIndicator(R.drawable.ic_basics_black_24dp);
//		mainActionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO);
//		mainActionBar.setHomeButtonEnabled(true);
//		mainActionBar.setSubtitle("subtitle");
		//mainActionBar.openOptionsMenu(); */ // -- ActionBar methods tested.

		BottomNavigationView navView = findViewById(R.id.nav_view);
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.navigation_home,
				R.id.navigation_dashboard,
				R.id.navigation_schulte,
				R.id.navigation_basics)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(binding.navView, navController);

		fabLaunch = findViewById(R.id.fabLaunch);

		getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				finishAffinity();
			}
		});

		// BottomSheet for Preferences
		findViewById(R.id.touch_outside).setOnClickListener(v -> finish());
		BottomSheetBehavior.from(findViewById(R.id.bottom_sheet))
				.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
					@Override
					public void onStateChanged(@NonNull View bottomSheet, int newState) {
						switch (newState) {
							case BottomSheetBehavior.STATE_HIDDEN:
								finish();
								break;
							case BottomSheetBehavior.STATE_EXPANDED:
								getWindow().setStatusBarColor(Color.TRANSPARENT);
								break;
							default:
								getWindow().setStatusBarColor(abColor);
								break;
						}
					}
					@Override
					public void onSlide(@NonNull View bottomSheet, float slideOffset) {
						// no op
					}
				});

		// Show the exercise description
		fabLaunch.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				ExerciseRunner.loadPreference();
				String exType = ExerciseRunner.getExType();

				if (exType == null | exType.equals("no_exercise")) {
					return false;
				} else {

					String exDescriptionLocalUrl = getResources().getString(R.string.src_exDescriptionUrl) + exType + ".html";
					try {
						Utils.openWebPage(exDescriptionLocalUrl, MainActivity.this);
						return true;
					} catch (Exception e) {
						Log.e(TAG, "web page opening url: " + exDescriptionLocalUrl, e);
						return false;
					}
				}
			}
		});
		// Run the exercise
		fabLaunch.setOnClickListener(view -> {
			Class activity = null;
			ExerciseRunner.loadPreference();
			String exType = runner.getExType();
			// done: 21.09.2023 here we need to choose Activity by switch: (ExerciseRunner.getTypeOfExercise())
			// gcb_bas_dbl_dot, gcb_bas_circles_rb, schulte_1_sequence
			switch (exType.substring(0,7)){
				case "gcb_bas":
					activity = BasicsActivity.class;
					break;
				case "gcb_sch":
					activity = SchulteActivity.class;
					break;
				default: Toast.makeText(MainActivity.this, TAG+ getResources().getString(R.string.err_unknown), Toast.LENGTH_SHORT).show();
			}

			//intent.putExtra(KEY_RUNNER, ExerciseRunner.getTypeOfExercise()); // actually it's not necessary since we got Singleton for ExerciseRunner
			if (null != activity) {
				Intent intent = new Intent(MainActivity.this, activity);
				startActivity(intent);
			} else {
				Toast.makeText(MainActivity.this, TAG+ getResources().getString(R.string.err_unknown), Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// continue installation in case of interrupted update is found
		appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
				appUpdateInfo -> {
					if (appUpdateInfo.updateAvailability()
							== UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
						appUpdateManager.startUpdateFlowForResult(
								appUpdateInfo,
								appUpdateLauncher,
								AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
					}
				});

		// Onboarding hints
		if (ExerciseRunner.isShowIntro() &&
				(0 == (ExerciseRunner.getShownIntros() & SHOWN_00_MAIN))) {
			new TapTargetSequence(this)
					.targets(
							new TapTargetViewWr(this, fabLaunch, getString(R.string.hint_main_fab_title), getString(R.string.hint_main_fab_desc)).getTapTarget(),
							//new TapTargetViewWr(this, findViewById(R.id.navigation_basics), getString(R.string.hint_main_basics_title), getString(R.string.hint_main_basics_desc)).getTapTarget(),
							//new TapTargetViewWr(this, findViewById(R.id.navigation_schulte), getString(R.string.hint_main_schulte_title), getString(R.string.hint_main_schulte_desc)).getTapTarget(),
							//new TapTargetViewWr(this, findViewById(R.id.navigation_dashboard), getString(R.string.hint_main_dashboard_title), getString(R.string.hint_main_dashboard_desc)).getTapTarget(),
							//new TapTargetViewWr(this, findViewById(R.id.navigation_home), getString(R.string.hint_main_home_title), getString(R.string.hint_main_home_desc)).getTapTarget(),

							TapTarget.forBounds(getTopRightCornerRect(this), getString(R.string.hint_main_settings_title), getString(R.string.hint_main_settings_desc))
									.outerCircleAlpha(0.9f)
									.outerCircleColor(R.color.purple_700)
									.textColor(R.color.light_grey_D_yellow)
									.targetRadius(150)
									.transparentTarget(true)
									.cancelable(true)
					)
					.listener(new TapTargetSequence.Listener() {
						// This listener will tell us when interesting(tm) events happen in regards
						// to the sequence
						@Override
						public void onSequenceFinish() {
							//Toast.makeText(MainActivity.this, "onSequenceFinish", Toast.LENGTH_SHORT).show();
							ExerciseRunner.updateShownIntros(SHOWN_00_MAIN);
						}

						@Override
						public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
							//Toast.makeText(MainActivity.this, "onSequenceStep", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSequenceCanceled(TapTarget lastTarget) {
							//Toast.makeText(MainActivity.this, "onSequenceCanceled", Toast.LENGTH_SHORT).show();
						}
					}).start();
		}
	}

	@Override
	protected void onStop() {
		// Check Demo user
		if (ExerciseRunner.KEY_DEFAULT_USER_PREF.equals(ExerciseRunner.uid)) {
			FirebaseAuth.getInstance().signOut();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// Check Demo user
		if (ExerciseRunner.KEY_DEFAULT_USER_PREF.equals(ExerciseRunner.uid)) {
			FirebaseAuth.getInstance().signOut();
		}
		super.onDestroy();
	}
}