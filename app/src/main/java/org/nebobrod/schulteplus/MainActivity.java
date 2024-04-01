package org.nebobrod.schulteplus;


import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.showSnackBarConfirmation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.nebobrod.schulteplus.databinding.ActivityMainBinding;
import org.nebobrod.schulteplus.Log;
//import org.nebobrod.schulteplus.ui.BasicsActivity;
import org.nebobrod.schulteplus.fbservices.LoginActivity;
import org.nebobrod.schulteplus.fbservices.SignupActivity;
import org.nebobrod.schulteplus.fbservices.UserFbData;
import org.nebobrod.schulteplus.fbservices.UserHelper;
import org.nebobrod.schulteplus.ui.BasicsActivity;
import org.nebobrod.schulteplus.ui.PopupSettingsFragment;
import org.nebobrod.schulteplus.ui.SchulteActivity02;

public class MainActivity extends AppCompatActivity implements UserFbData.UserHelperCallback {
	public static final String TAG = "MainActivity";
//	private static MainActivity instance;

	FirebaseAuth fbAuth;
	FirebaseUser user = null;
	UserHelper userHelper;
	UserFbData userFbData;

	private ActivityMainBinding binding;
	ExerciseRunner runner;
	private FloatingActionButton fabLaunch;

/*	public MainActivity() {
		instance = this;
	}*/

/*
	public static MainActivity getInstance() {
		return instance;
	}
*/

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
//		getMenuInflater().inflate(R.menu.menu_main, menu);
		getMenuInflater().inflate(R.menu.main, menu); // this is a one button
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.menu_settings:
				((AppCompatDialogFragment) Fragment.instantiate(this, PopupSettingsFragment.class.getName()))
						.show(getSupportFragmentManager(), PopupSettingsFragment.class.getName());
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());


		if(getIntent() != null & getIntent().hasExtra("user"))
		{
			userHelper = getIntent().getExtras().getParcelable("user");

//			etEmail.setText(getIntent().getExtras().getString("email",""));
//			etName.setText(getIntent().getExtras().getString("name", ""));
//			etPassword.setText(getIntent().getExtras().getString("password", ""));
		}


		// if no user at all
		runner = ( null != userHelper ? ExerciseRunner.getInstance(userHelper) : ExerciseRunner.getInstance());

		//ActionBar
		androidx.appcompat.app.ActionBar mainActionBar = this.getSupportActionBar();
		int abColor =  getWindow().getStatusBarColor();
		mainActionBar.setLogo(R.drawable.ic_ab_schulte_plus);
		mainActionBar.setDisplayUseLogoEnabled(true);
		mainActionBar.setDisplayShowHomeEnabled(true);
/*** ActionBar methods tested:
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
		//mainActionBar.openOptionsMenu(); */

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

//		ExerciseRunner.getInstance();
		fabLaunch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Class activity = SchulteActivity02.class;
				Intent intent = null;
				String exType = runner.getExType();
//				runner.savePreferences(null);
				ExerciseRunner.loadPreference();
				// done: 21.09.2023 here we need to choose Activity by switch: (ExerciseRunner.getTypeOfExercise())
				// gcb_bas_dbl_dot, gcb_bas_circles_rb, schulte_1_sequence
				switch (exType.substring(0,7)){
					case "gcb_bas":
						activity = BasicsActivity.class;
						break;
					case "gcb_sch":
						activity = SchulteActivity02.class;
						break;
					default: Toast.makeText(MainActivity.this, TAG+ getResources().getString(R.string.err_unknown), Toast.LENGTH_SHORT).show();
				}

				//intent.putExtra(KEY_RUNNER, ExerciseRunner.getTypeOfExercise()); // actually it's not necessary since we got Singleton for ExerciseRunner
				if (null != activity) {
					intent = new Intent(MainActivity.this, activity);
					startActivity(intent);
				} else Toast.makeText(MainActivity.this, TAG+ getResources().getString(R.string.err_unknown), Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
//		runner.loadPreference(getApplicationContext());
	}

	@Override
	public void onBackPressed() {
		finishAffinity();
//		finishAndRemoveTask();
//		finish();
//		super.onBackPressed();
	}

	@Override
	public void onCallback(@Nullable UserHelper fbDbUser) {
		if (fbDbUser == null) {
			showSnackBarConfirmation(this, getRes().getString(R.string.err_unknown) + " ", null);
		} else {
			// TODO: 19.11.2023 load profile from server

			Toast.makeText(this, getRes().getString(R.string.txt_welcome_back)
					+ ", " + fbDbUser.getName() + "!", Toast.LENGTH_SHORT).show();
		}
	}
}