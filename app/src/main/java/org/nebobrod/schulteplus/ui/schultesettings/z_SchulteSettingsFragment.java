package org.nebobrod.schulteplus.ui.schultesettings;

import static org.nebobrod.schulteplus.Utils.intFromString;


import androidx.lifecycle.ViewModelProvider;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.R;

public class z_SchulteSettingsFragment extends Fragment {

	private z_SchulteSettingsViewModel mViewModel;
	private View view;
	private EditText etWidth, etHeight;
	private ExerciseRunner runner = ExerciseRunner.getInstance(getContext());
	private String[] exTypes;

	public static z_SchulteSettingsFragment newInstance() {
		return new z_SchulteSettingsFragment();
	}

	@Override
	public void onPause() {
//		Toast.makeText(getContext(), "onPause", Toast.LENGTH_SHORT).show();
		// here we call overridden methods to complete settings
		etWidth.getOnFocusChangeListener().onFocusChange(etWidth, true);
		etHeight.getOnFocusChangeListener().onFocusChange(etHeight, true);
		super.onPause();
	}

	/*@Override
	public void onStop() {
		Toast.makeText(getContext(), "onStop", Toast.LENGTH_SHORT).show();
		super.onStop();
	}*/

	/*@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		Toast.makeText(getContext(), "onSaveInstanceState", Toast.LENGTH_SHORT).show();
		super.onSaveInstanceState(outState);
	}*/

	@Override
	public void onResume() {
		runner.setPreference(getContext());
		runner.setExType(exTypes[0]); //should be "schulte_1_sequence"

		super.onResume();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.z_fragment_schulte_settings, container, false);

		Resources res = getResources();
		exTypes = res.getStringArray(R.array.ex_type);

		etWidth = view.findViewById(R.id.et_x_size);
		etWidth.setText("" + runner.getX());
		etHeight= view.findViewById(R.id.et_y_size);
		etHeight.setText("" + runner.getY());

		// set X & Y values to Runner (and for future: https://stackoverflow.com/a/8063840/20456581)
		etWidth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				byte value = (byte) intFromString("" + etWidth.getText());
				if (value < 1 || value > 10){
					Toast.makeText(getContext(), R.string.hnt_input_height, Toast.LENGTH_SHORT).show();
					value = 5;
					etWidth.setText("" + value);
				}

				runner.setX(value);
			}
		});
		etHeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				byte value = (byte) intFromString("" + etHeight.getText());
				if (value < 1 || value > 10){
					Toast.makeText(getContext(), R.string.hnt_input_height, Toast.LENGTH_SHORT).show();
					value = 5;
					etHeight.setText("" + value);
				}
				runner.setY(value);
			}
		});
		// they say it has to be cleaned after usage
//		exTypes.recycle();
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(z_SchulteSettingsViewModel.class);
		// TODO: Use the ViewModel

	}

}