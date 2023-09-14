package org.nebobrod.schulteplus;

import static org.nebobrod.schulteplus.ExerciseRunner.KEY_RUNNER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.nebobrod.schulteplus.databinding.ActivityMainBinding;
import org.nebobrod.schulteplus.ui.SchulteActivity02;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;
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

		ExerciseRunner.getInstance(this);
		fabLaunch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, SchulteActivity02.class);
				intent.putExtra(KEY_RUNNER, ExerciseRunner.getTypeOfExercise()); // 0 means new book, so Edit will create new book
				startActivity(intent);
			}
		});
	}

}