/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.home;

import static org.nebobrod.schulteplus.common.Const.SHOWN_03_STATA;
import static org.nebobrod.schulteplus.common.Const.SHOWN_04_NEWS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.databinding.FragmentHomeBinding;
import org.nebobrod.schulteplus.ui.TapTargetViewWr;

public class HomeFragment extends Fragment {

	private FragmentHomeBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		HomeViewModel homeViewModel =
				new ViewModelProvider(this).get(HomeViewModel.class);

		binding = FragmentHomeBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		final TextView textView = binding.textHome;
		textView.setText(R.string.txt_news);
		homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

		// Go to myurl.com when clicking on logo
		ImageView img = binding.ivBacground;

		img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Utils.openWebPage(getResources().getString(R.string.src_home_ipir_vk_url), getContext());;
			}
		});

		return root;
	}

	/**
	 * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
	 * has returned, but before any saved state has been restored in to the view.
	 * This gives subclasses a chance to initialize themselves once
	 * they know their view hierarchy has been completely created.  The fragment's
	 * view hierarchy is not however attached to its parent at this point.
	 *
	 * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed
	 *                           from a previous saved state as given here.
	 */
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Onboarding intro
/*		if (ExerciseRunner.isShowIntro() &&
				(0 == (ExerciseRunner.getShownIntros() & SHOWN_04_NEWS))) {
			new TapTargetSequence(requireActivity())
					.targets(
							new TapTargetViewWr(this, view, getString(R.string.hint_stata_source_title), getString(R.string.hint_stata_source_desc)).getTapTarget()
					)
					.listener(new TapTargetSequence.Listener() {
						@Override
						public void onSequenceFinish() {
							ExerciseRunner.updateShownIntros(SHOWN_04_NEWS);
						}
						@Override
						public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) { }
						@Override
						public void onSequenceCanceled(TapTarget lastTarget) { }
					}).start();
		}*/
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}