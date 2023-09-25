package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.ExerciseRunner.KEY_RUNNER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.EditTextPreference;

import org.nebobrod.schulteplus.databinding.ActivityMainBinding;
//import org.nebobrod.schulteplus.ui.BasicsActivity;
import org.nebobrod.schulteplus.ui.BasicsActivity;
import org.nebobrod.schulteplus.ui.SchulteActivity02;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = "MainActivity";

	private ActivityMainBinding binding;
	ExerciseRunner runner = ExerciseRunner.getInstance(this);
	private FloatingActionButton fabLaunch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

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

		ExerciseRunner.getInstance(getApplicationContext());
		fabLaunch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Class activity = SchulteActivity02.class;
				Intent intent = null;
				String exType = runner.getExType();
				// TODO: 21.09.2023 here we need to choose Activity by switch: (ExerciseRunner.getTypeOfExercise())
//		gcb_bas_dbl_dot, gcb_bas_circles_rb, schulte_1_sequence
				switch (exType.substring(0,7)){
					case "gcb_bas":
						activity = BasicsActivity.class;
						break;
					case "schulte":
						activity = SchulteActivity02.class;
						break;
					default: Toast.makeText(MainActivity.this, TAG+R.string.err_unknown, Toast.LENGTH_SHORT).show();
				}

				//intent.putExtra(KEY_RUNNER, ExerciseRunner.getTypeOfExercise()); // actually it's not necessary since we got Singleton for ExerciseRunner
				if (null != activity) {
					intent = new Intent(MainActivity.this, activity);
					startActivity(intent);
				} else Toast.makeText(MainActivity.this, TAG+ getResources().getString(R.string.err_unknown), Toast.LENGTH_LONG).show();
			}
		});
	}

}