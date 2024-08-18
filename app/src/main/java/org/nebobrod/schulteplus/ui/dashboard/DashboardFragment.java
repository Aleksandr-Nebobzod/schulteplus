/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.dashboard;

import static org.nebobrod.schulteplus.Utils.getRes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
	private static final String TAG = "Dashboard";

	TabLayout tabLayout;
	private ViewPager2 pager;
	private DashboardPagerAdapter dpAdapter;
	DashboardViewModel dashboardViewModel;
	private FragmentDashboardBinding binding;


	String[] tabTiltles; // Arrays for tab titles


	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		dashboardViewModel =
				new ViewModelProvider(this).get(DashboardViewModel.class);
		// Binding and initiating
		binding = FragmentDashboardBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		pager = binding.pager;
		tabLayout = binding.tabLayout;

		// This prevents the bug#27 of App's backgrounding due to mis-click
		root.setOnClickListener(view -> {});

		// Language independent Values of Exercise Types
		tabTiltles = getRes().getStringArray(R.array.dashboard_pages);

		// Link the adapter to ViewPager2
		dpAdapter = new DashboardPagerAdapter(this);
		pager.setAdapter(dpAdapter);


		TabLayoutMediator tabLayoutMediator= new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy(){

			@Override
			public void onConfigureTab(TabLayout.Tab tab, int position) {
				tab.setText(tabTiltles[position]);
			}
		});
		tabLayoutMediator.attach();

		return root;
	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView: Called");
		binding = null;
	}
}