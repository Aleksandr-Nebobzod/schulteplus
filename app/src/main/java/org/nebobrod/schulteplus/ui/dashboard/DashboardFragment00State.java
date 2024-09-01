/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.dashboard;

import static org.nebobrod.schulteplus.Utils.localDateOfTimeStamp;

import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.databinding.FragmentDashboard00StateBinding;
import org.nebobrod.schulteplus.ui.ExResultCardViewAdapter;
import org.nebobrod.schulteplus.ui.KeyValueView;
import org.nebobrod.schulteplus.ui.SpCalendarView;

import com.google.android.material.color.MaterialColors;
//import org.webjars.bower.CalendarHeatMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.datepicker.CalendarConstraints;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment00State extends Fragment {
	private static final String TAG = "Dashboard_State";

	private DashboardViewModel dashboardViewModel;
	private FragmentDashboard00StateBinding binding;

	TextView tvTitle, tvTitleOfList, tvNoData;
	KeyValueView kvvDays, kvvDaysTrained, kvvHours, kvvPsycoins;
	List<SpCalendarView.DayData> events = null;
	SpCalendarView cView;
	private List<ExResult> exResultList = new ArrayList<>();
	private ExResultCardViewAdapter exResultCvAdapter;
	private RecyclerView rvExResult;

	/**
	 * Called to do initial creation of a fragment.
	 * @param savedInstanceState If the fragment is being re-created from
	 *                           a previous saved state, this is the state.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dashboardViewModel =
				new ViewModelProvider(this).get(DashboardViewModel.class);

		// Calendar data example
/*
		List<SpCalendarView.DayData> events = new ArrayList<>();
		events.add(new SpCalendarView.DayData(LocalDate.of(2024, 8, 15), 6));
		events.add(new SpCalendarView.DayData(LocalDate.of(2024, 8, 5), 5));
		events.add(new SpCalendarView.DayData(LocalDate.of(2024, 8, 4), 4));
		events.add(new SpCalendarView.DayData(LocalDate.of(2024, 8, 3), 3));
		events.add(new SpCalendarView.DayData(LocalDate.of(2024, 8, 2), 2));
		events.add(new SpCalendarView.DayData(LocalDate.of(2024, 8, 1), 1));
*/
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Binding and initiating
		binding = FragmentDashboard00StateBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
			// This prevents the bug#27 of App's backgrounding due to mis-click
			root.setOnClickListener(view -> {});


		tvTitle = binding.tvTitleOfSummary;
		kvvDays = binding.kvvDaysTogether;
		kvvDaysTrained = binding.kvvDaysTrained;
		kvvHours = binding.kvvHoursTrained;
		kvvPsycoins = binding.kvvPsycoins;


		tvTitleOfList = binding.tvTitleOfList;

		cView = binding.calendarView;
//		cView.setupCalendar(null,0,0);
		cView.setupCalendar(null,
				MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorSurface, Color.GRAY),
				MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimaryVariant, Color.GREEN));
		cView.scrollToMonth(YearMonth.now());
		cView.setOnDateClickListener(new SpCalendarView.OnDateClickListener() {
			@Override
			public void onDateClicked(LocalDate date) {

				updateRvExResult(date);
			}
		});

		tvNoData = binding.tvNoData;
		rvExResult = binding.rvExresult;
		rvExResult.setLayoutManager(new LinearLayoutManager(getContext()));
		exResultCvAdapter = new ExResultCardViewAdapter(exResultList);
		rvExResult.setAdapter(exResultCvAdapter);

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
		dashboardViewModel.getExContributionsLiveData().observe(getViewLifecycleOwner(), dayDataList -> {
			if (dayDataList != null) {
				// Update Calendar
/*				cView.setupCalendar((List<SpCalendarView.DayData>)dayDataList,
						MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorOnPrimarySurface, Color.GRAY),
						MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorAccent, Color.GREEN));*/
				cView.updateCalendar((List<SpCalendarView.DayData>)dayDataList);
			}

			// Calendar sub-list
			updateRvExResult(LocalDate.now());
		});

		dashboardViewModel.getDaysLD().observe(getViewLifecycleOwner(), new Observer() {
			@Override
			public void onChanged(Object o) {
				// Brief statistics
				updateStata();
			}
		});

		dashboardViewModel.getPsyCoinsLD().observe(getViewLifecycleOwner(), new Observer() {
			@Override
			public void onChanged(Object o) {
				// Brief statistics
				updateStata();
			}
		});
	}

	private void updateStata() {
		LocalDate creationDate = LocalDate.parse(ExerciseRunner.getUserHelper().getDateCreated(), DateTimeFormatter.ISO_DATE_TIME);
		kvvDays.setValueText(String.valueOf(ChronoUnit.DAYS.between(creationDate, LocalDate.now(ZoneId.of("UTC")))));

		kvvDaysTrained.setValueText(String.valueOf(dashboardViewModel.getDaysLD().getValue()));
		kvvHours.setValueText(String.valueOf(ExerciseRunner.getUserHelper().getHours()));
		kvvPsycoins.setValueText(String.valueOf(dashboardViewModel.getPsyCoinsLD().getValue()));
	}

	private void updateRvExResult(LocalDate date) {

		// Get raw data
		List<ExResult> newExerciseList = (List<ExResult> ) dashboardViewModel.getExCalendarLiveData().getValue();
		if (newExerciseList == null || newExerciseList.size() < 1) {
			rvExResult.setVisibility(View.GONE);
			tvNoData.setVisibility(View.VISIBLE);
			return;
		}

		// Filter the clicked date
		exResultList.clear();
		for (ExResult res : newExerciseList) {
			if (date.equals(localDateOfTimeStamp(res.getTimeStamp()))) {
				exResultList.add(res);
			}
		}

		// Show the new list or no-data message
		if (exResultList.size() > 0) {
			exResultCvAdapter.notifyDataSetChanged();
			rvExResult.setVisibility(View.VISIBLE);
			tvNoData.setVisibility(View.GONE);

			// update the title
			tvTitleOfList.setText(String.format("%s %s, %s:", getString(R.string.lbl_exercises), date.toString(), exResultList.size()));
			Log.d(TAG, "updateRvExResult: " + exResultList);
		} else {
			rvExResult.setVisibility(View.GONE);
			tvNoData.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume: Called");

		dashboardViewModel.fetchExCalendar();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView: Called");
		binding = null;
	}
}