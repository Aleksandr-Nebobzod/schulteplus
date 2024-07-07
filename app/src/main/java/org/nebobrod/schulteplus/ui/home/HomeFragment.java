/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.home;

import static org.nebobrod.schulteplus.Utils.timeStampToDateLocal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.SnackBarManager;
import org.nebobrod.schulteplus.data.AdminNote;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.databinding.FragmentHomeBinding;

import java.util.List;

public class HomeFragment extends Fragment {
	private static final String TAG = "HomeFragment";

	private FragmentHomeBinding binding;
	HomeViewModel homeViewModel;
	View root;
	ImageView ivBackpic;
	TextView textHome;
	TextView tvNews;
	SnackBarManager snackBarManager;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

		binding = FragmentHomeBinding.inflate(inflater, container, false);
		root = binding.getRoot();

		tvNews = binding.tvNewsIndicator;
		snackBarManager = new SnackBarManager(requireActivity()).setPostponed(true);

		textHome = binding.textHome;
		textHome.setText(R.string.txt_news);
		homeViewModel.getText().observe(getViewLifecycleOwner(), textHome::setText);

		// Go to myurl.com when clicking on the dummy picture
		ivBackpic = binding.ivBackpic;
		ivBackpic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Utils.openWebPage(getResources().getString(R.string.src_psychonetics_social_media), getContext());
			}
		});

		homeViewModel.init();
/*		root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// Start the data process
				homeViewModel.init();

				// Remove the listener to prevent it from being called multiple times
				root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});*/

		return root;
	}

	/**
	 * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
	 * has returned, but before any saved state has been restored in to the view.
	 */
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		homeViewModel.getmNewsIndicator().observe(getViewLifecycleOwner(), s -> {

			// Update clickable TextView News Indicator
			tvNews.setText(s);
			if (s.equals(getString(R.string.msg_news_reminder_yes))) {

				loadMessages(homeViewModel.getmAdminNotes().getValue());

				tvNews.setTextColor(getResources().getColor(R.color.light_grey_A_red, null));
				tvNews.setOnClickListener(view1 -> {
					snackBarManager.setPostponed(false).showAllQueue(() -> {

						// No news:
						tvNews.setTextColor(getResources().getColor(R.color.light_grey_A, null));
						tvNews.setText(R.string.msg_news_reminder_no);
						tvNews.setOnClickListener(null);
					});
				});
			} else {
				tvNews.setTextColor(getResources().getColor(R.color.light_grey_A, null));
				tvNews.setText(R.string.msg_news_reminder_no);
				tvNews.setOnClickListener(null);
			}
		});


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

	private void loadMessages(List<AdminNote> list) {

		String _message = "...";
		for (AdminNote an : list) {
			_message = timeStampToDateLocal(an.getTimeStamp()) +
					": <b>" + an.getTitle() +
					"</b><br>" + an.getMessage();
			snackBarManager.queueMessage(_message, new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AdminNote anUpdated = an.clone();
					anUpdated.setTimeStampConfirmed(Utils.timeStampU());
					new DataOrmRepo<>(AdminNote.class).create(anUpdated);
				}
			});
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}