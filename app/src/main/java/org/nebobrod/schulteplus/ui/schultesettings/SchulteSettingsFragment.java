package org.nebobrod.schulteplus.ui.schultesettings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nebobrod.schulteplus.R;

public class SchulteSettingsFragment extends Fragment {

	private SchulteSettingsViewModel mViewModel;

	public static SchulteSettingsFragment newInstance() {
		return new SchulteSettingsFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_schulte_settings, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(SchulteSettingsViewModel.class);
		// TODO: Use the ViewModel
	}

}