package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.ExerciseRunner.KEY_RUNNER;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.os.OperationCanceledException;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.EditTextPreference;

import org.nebobrod.schulteplus.accounts.AccountUtils;
import org.nebobrod.schulteplus.accounts.AuthPreferences;
import org.nebobrod.schulteplus.databinding.ActivityMainBinding;
//import org.nebobrod.schulteplus.ui.BasicsActivity;
import org.nebobrod.schulteplus.ui.BasicsActivity;
import org.nebobrod.schulteplus.ui.PopupSettingsFragment;
import org.nebobrod.schulteplus.ui.SchulteActivity02;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = "MainActivity";
	 //Authentication
	 private static final int REQ_SIGNUP = 1;
	 private AccountManager mAccountManager;
	 private AuthPreferences mAuthPreferences;
	 private String authToken;

	private static MainActivity instance;

	private ActivityMainBinding binding;
	ExerciseRunner runner = ExerciseRunner.getInstance(this);
	private FloatingActionButton fabLaunch;

	public MainActivity() {
		instance = this;
	}
	public static MainActivity getInstance() {
		return instance;
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu); // this is a one button-menu
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				((AppCompatDialogFragment) Fragment.instantiate(this, PopupSettingsFragment.class.getName()))
						.show(getSupportFragmentManager(), PopupSettingsFragment.class.getName());
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		//Authentication:
		authToken = null;
		mAuthPreferences = new AuthPreferences(this);
		mAccountManager = AccountManager.get(this);

		GetAuthTokenCallback atbf;
		atbf = new GetAuthTokenCallback();

		// Ask for an auth token
		mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, atbf, null);

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

		ExerciseRunner.getInstance(getApplicationContext());
		fabLaunch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Class activity = SchulteActivity02.class;
				Intent intent = null;
				String exType = runner.getExType();
				runner.savePreferences();
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
		runner.getPreference(getApplicationContext());
		super.onResume();
	}

	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;

			try {
				bundle = result.getResult();

				final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (null != intent) {
					startActivityForResult(intent, REQ_SIGNUP);
				} else {
					authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

					// Save session username & auth token
					mAuthPreferences.setAuthToken(authToken);
					mAuthPreferences.setUsername(accountName);

					String i = "A.U.T.H.E.N.T.I.C.A.T.I.O.N. .D.A.T.A:";
					i+="\nRetrieved auth token: " + authToken;
					i+="\nSaved account name: " + mAuthPreferences.getAccountName();
					i+="\nSaved auth token: " + mAuthPreferences.getAuthToken();
					Log.d(TAG, i);

					// If the logged account didn't exist, we need to create it on the device
					Account account = AccountUtils.getAccount(MainActivity.this, accountName);
					if (null == account) {
						account = new Account(accountName, AccountUtils.ACCOUNT_TYPE);
						mAccountManager.addAccountExplicitly(account, bundle.getString(LoginActivity.PARAM_USER_PASSWORD), null);
						mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
					}
				}
			} catch(OperationCanceledException e) {
				// If signup was cancelled, force activity termination
				finish();
			} catch(Exception e) {
				e.printStackTrace();
			}

		}

	}
}