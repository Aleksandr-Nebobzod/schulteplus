/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.dashboard;

import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.common.Const.SHOWN_03_STATA;
import static org.nebobrod.schulteplus.common.Const.TIMESTAMP_FIELD_NAME;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.AchievementArrayAdapter;
import org.nebobrod.schulteplus.databinding.FragmentDashboard01AchievementsBinding;
import org.nebobrod.schulteplus.ui.TapTargetViewWr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardFragment01Achievements extends Fragment {
	private static final String TAG = "Dashboard_Achievements";

	DashboardViewModel dashboardViewModel;
	private FragmentDashboard01AchievementsBinding binding;
	RadioGroup rgSource;
	TextView tvTitle;

	ListView elvChart;						// Main data chart
	ArrayList<Achievement> listAchievement;
	ArrayAdapter<Achievement> achievementAdapter;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		dashboardViewModel =
				new ViewModelProvider(this).get(DashboardViewModel.class);
		// Binding and initiating
		binding = FragmentDashboard01AchievementsBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
			// This prevents the bug#27 of App's backgrounding due to mis-click
			root.setOnClickListener(view -> {});

		rgSource = binding.rgSource;			// RadioGroup report Filter
		rgSource.setOnCheckedChangeListener((radioGroup, checkedId) -> {
					//Change Local or WWW datasource & www-Filters

			// Pass checked radioButton to viewModel
			RadioButton _rb = getView().findViewById(checkedId);
			tvTitle.setText(_rb.getHint());
			dashboardViewModel.setFilter(_rb.getText().toString());
			dashboardViewModel.fetchAchievements();
		});

		tvTitle = binding.tvTitle;
		elvChart = binding.elvDashboard;		// Report chart
		elvChart.setOnItemLongClickListener((adapterView, view, i, l) -> {

			// Copy data to clipboard
			ListAdapter _adapter = ((ListView) adapterView).getAdapter();
			int count = _adapter.getCount();
			if (count == 0) {
				return false; // if empty
			}
			// Define item's Class
			Object item = _adapter.getItem(0);
			Class<?> itemClass = item.getClass();

			// Create StringBuilder to gather text
			StringBuilder stringBuilder = new StringBuilder();

			for (int j = 0; j < count; j++) {
				item = _adapter.getItem(j);
				try {
					Method method = itemClass.getDeclaredMethod("toTabSeparatedString");
					String result = (String) method.invoke(item);
					stringBuilder.append(result).append("\n");
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					// If toTabSeparatedString() doesn't exist
					stringBuilder.append(item.toString()).append("\n");
				}
			}
			// Copy text to clipboard
			ClipboardManager clipboard = (ClipboardManager) getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("label", stringBuilder.toString());
			clipboard.setPrimaryClip(clip);

			Toast.makeText(getAppContext(), "Table copied to clipboard", Toast.LENGTH_SHORT).show();
			return true;
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

		// Watch for LiveData and refresh UI
		dashboardViewModel.getAchievLD().observe(getViewLifecycleOwner(), results -> {
			if (results == null || ((List<Object>)results).size() < 1) {
				Toast.makeText(Utils.getAppContext(), getString(R.string.err_no_data), Toast.LENGTH_SHORT).show();
				return;
			}

			boolean isLocal = (Objects.requireNonNull(dashboardViewModel.getFilter().getValue()).equals(Utils.getRes().getString(R.string.lbl_datasource_local)));
			// check type of data
			if (((List<Object>)results).get(0) instanceof Achievement) {
				listAchievement = (ArrayList<Achievement>) results;
				achievementAdapter = new AchievementArrayAdapter(getAppContext(),
						Utils.markupListAsGroupedBy(listAchievement, TIMESTAMP_FIELD_NAME),
						isLocal);
				elvChart.setAdapter(achievementAdapter);
				achievementAdapter.notifyDataSetChanged();
			}
		});


		// Onboarding intro -- skip on 240819 yet
/*		if (ExerciseRunner.isShowIntro() &&
				(0 == (ExerciseRunner.getShownIntros() & SHOWN_03_STATA))) {
			new TapTargetSequence(requireActivity())
					.targets(
							new TapTargetViewWr(this, elvChart, getString(R.string.hint_stata_dashboard_title), getString(R.string.hint_stata_dashboard_desc)).getTapTarget(),
							new TapTargetViewWr(this, rgSource, getString(R.string.hint_stata_source_title), getString(R.string.hint_stata_source_desc)).getTapTarget()
					)
					.listener(new TapTargetSequence.Listener() {
						@Override
						public void onSequenceFinish() {
							ExerciseRunner.updateShownIntros(SHOWN_03_STATA);
						}
						@Override
						public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) { }
						@Override
						public void onSequenceCanceled(TapTarget lastTarget) { }
					}).start();
		}*/
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume: Called");

		// Pass checked radioButton to viewModel
		RadioButton _rb = getView().findViewById(rgSource.getCheckedRadioButtonId());
		dashboardViewModel.setFilter(_rb.getText().toString());

		dashboardViewModel.fetchAchievements();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView: Called");
		binding = null;
	}
}