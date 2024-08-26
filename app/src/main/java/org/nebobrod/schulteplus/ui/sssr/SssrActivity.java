/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.sssr;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.slider.Slider;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.ExResultSssr;
import org.nebobrod.schulteplus.databinding.ActivitySssrBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SssrActivity extends AppCompatActivity {
	public static final String TAG = "SssrActivity";
	private ActivitySssrBinding binding;
	private ExResultSssr exResultSssr;
	private ExerciseRunner runner;

	private DataRepos<ExResultSssr> repos;
	private MutableLiveData<ExResultSssr> resultLD = new MutableLiveData<>();
	private DialogInterface.OnClickListener cancelListener;
	private DialogInterface.OnClickListener restartListener;

	private LocalDate selectedDate;
	private TextView tvDate;
	private ImageButton btnPrev, btnNext;

	private PieChart pieChart;
	List<PieEntry> spheres;
	// SeekBars were not renamed to Sliders:
	Slider sbJob, sbChores, sbPhysical, sbFamily, sbFriends, sbLeisure, sbSleep, sbSssr;
	private Slider.OnSliderTouchListener stopChangeListener;
	EditText etSssr;

	/**
	 * {@inheritDoc}
	 * <p>
	 * Perform initialization of all fragments.
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		binding = ActivitySssrBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		// repos = new DataRepos(ExResult.class);


		tvDate = binding.tvDate;
		tvDate.setOnClickListener(view -> showDatePicker(view));
		selectedDate = LocalDate.now();
		updateTvDate(selectedDate);

		// Minus and Plus Day
		btnPrev = binding.btnArrowLeft;
		btnPrev.setOnClickListener(view -> {
			selectedDate = selectedDate.plusDays(-1);
			updateTvDate(selectedDate);
		});
		btnNext = binding.btnArrowRight;
		btnNext.setOnClickListener(view -> {
			if (selectedDate.equals(LocalDate.now())) {
				return;			// No future days allowed
			}
			selectedDate = selectedDate.plusDays(1);
			updateTvDate(selectedDate);
		});

		// Pie Data Chart
		runner = ExerciseRunner.getInstance();
		exResultSssr = new ExResultSssr(5, 5, 5, 5, 5, 5, 5, 5, 0, 0, "");
		pieChart = binding.pieChart;

		stopChangeListener = new Slider.OnSliderTouchListener() {
			@Override
			public void onStartTrackingTouch(@NonNull Slider slider) {/*no-op*/}
			@Override
			public void onStopTrackingTouch(@NonNull Slider slider) {
				int value = (int) slider.getValue();
				switch (slider.getId()) {
					case R.id.sb_job:
						exResultSssr.setJob(value);
						break;
					case R.id.sb_physical:
						exResultSssr.setPhysical(value);
						break;
					case R.id.sb_leisure:
						exResultSssr.setLeisure(value);
						break;
					case R.id.sb_family:
						exResultSssr.setFamily(value);
						break;
					case R.id.sb_friends:
						exResultSssr.setFriends(value);
						break;
					case R.id.sb_chores:
						exResultSssr.setChores(value);
						break;
					case R.id.sb_sleep:
						exResultSssr.setSleep(value);
						break;
					case R.id.sb_sssr:
						exResultSssr.setSssr(value);
						break;
				}
				updateSlidersAndPie(exResultSssr);
			}
		};

		sbJob = binding.sbJob;
		sbJob.addOnSliderTouchListener(stopChangeListener);
		sbPhysical = binding.sbPhysical;
		sbPhysical.addOnSliderTouchListener(stopChangeListener);
		sbLeisure = binding.sbLeisure;
		sbLeisure.addOnSliderTouchListener(stopChangeListener);
		sbFamily = binding.sbFamily;
		sbFamily.addOnSliderTouchListener(stopChangeListener);
		sbFriends = binding.sbFriends;
		sbFriends.addOnSliderTouchListener(stopChangeListener);
		sbChores = binding.sbChores;
		sbChores.addOnSliderTouchListener(stopChangeListener);
		sbSleep = binding.sbSleep;
		sbSleep.addOnSliderTouchListener(stopChangeListener);
		sbSssr = binding.sbSssr;
		sbSssr.addOnSliderTouchListener(stopChangeListener);

		updateSlidersAndPie(exResultSssr);

	}

	private void updateTvDate(LocalDate date) {
/*		year = date.getYear();
		month = date.getMonthValue();
		day = date.getDayOfMonth();*/
		tvDate.setText(date.toString());
	}

	public void showDatePicker(View view) {
		// Create DatePickerDialog
/*		MaterialStyledDatePickerDialog datePickerDialog = new MaterialStyledDatePickerDialog(
				this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

					}
				},
				year, month, day);*/
		DatePickerDialog datePickerDialog = new DatePickerDialog(
				this,
				(datePicker, selectedYear, selectedMonth, selectedDay) -> {
					// When the user chose a date -- Refresh it
					selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
					tvDate.setText(selectedDate.toString());
				}, selectedDate.getYear(), selectedDate.getMonthValue(), selectedDate.getDayOfMonth());

		// Limit future and Show Dialog
		datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
		datePickerDialog.show();
	}

	private void updateSlidersAndPie(ExResultSssr result) {
		spheres = new ArrayList<>();

		if (runner.isSwSssrJob()) {
			sbJob.setValue(result.getJob());
			spheres.add(new PieEntry(result.getJob(), getString(R.string.lbl_sssr_job)));
		} else {
			findViewById(R.id.tv_job).setVisibility(View.GONE);
			sbJob.setVisibility(View.GONE);
		}

		if (runner.isSwSssrPhysical()) {
			sbPhysical.setValue(result.getPhysical());
			spheres.add(new PieEntry(result.getPhysical(), getString(R.string.lbl_sssr_physical)));
		} else {
			findViewById(R.id.tv_physical).setVisibility(View.GONE);
			sbPhysical.setVisibility(View.GONE);
		}

		if (runner.isSwSssrLeisure()) {
			sbLeisure.setValue(result.getLeisure());
			spheres.add(new PieEntry(result.getLeisure(), getString(R.string.lbl_sssr_leisure)));
		} else {
			findViewById(R.id.tv_leisure).setVisibility(View.GONE);
			sbLeisure.setVisibility(View.GONE);
		}

		if (runner.isSwSssrFamily()) {
			sbFamily.setValue(result.getFamily());
			spheres.add(new PieEntry(result.getFamily(), getString(R.string.lbl_sssr_family)));
		} else {
			findViewById(R.id.tv_family).setVisibility(View.GONE);
			sbFamily.setVisibility(View.GONE);
		}

		if (runner.isSwSssrFriends()) {
			sbFriends.setValue(result.getFriends());
			spheres.add(new PieEntry(result.getFriends(), getString(R.string.lbl_sssr_friends)));
		} else {
			findViewById(R.id.tv_friends).setVisibility(View.GONE);
			sbFriends.setVisibility(View.GONE);
		}

		if (runner.isSwSssrChores()) {
			sbChores.setValue(result.getChores());
			spheres.add(new PieEntry(result.getChores(), getString(R.string.lbl_sssr_chores)));
		} else {
			findViewById(R.id.tv_chores).setVisibility(View.GONE);
			sbChores.setVisibility(View.GONE);
		}

		if (runner.isSwSssrSleep()) {
			sbSleep.setValue(result.getSleep());
			spheres.add(new PieEntry(result.getSleep(), getString(R.string.lbl_sssr_sleep)));
		} else {
			findViewById(R.id.tv_sleep).setVisibility(View.GONE);
			sbSleep.setVisibility(View.GONE);
		}

		if (runner.isSwSssrSssr()) {
			sbSssr.setValue(result.getSssr());
			spheres.add(new PieEntry(result.getSssr(), getString(R.string.lbl_sssr_main)));
		} else {
			findViewById(R.id.tv_sssr).setVisibility(View.GONE);
			sbSssr.setVisibility(View.GONE);
		}

		// Raw dataset
		PieDataSet dataSet = new PieDataSet(spheres, "Categories");

		// Colors
		dataSet.setColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA);

		// Pie dataset
		PieData pieData = new PieData(dataSet);

		// tie dataset
		pieChart.setData(pieData);

		// Show
//		pieChart.animateX(100, Easing.EaseOutQuad);
		pieChart.invalidate();
	}

	public void updatePieEntry(PieChart pieChart, List<PieEntry> entries, float newValue, String label) {
		// Найдите элемент, который нужно обновить
		for (int i = 0; i < entries.size(); i++) {
			PieEntry entry = entries.get(i);
			if (entry.getLabel().equals(label)) {
				// Замените старое значение на новое
				entries.set(i, new PieEntry(newValue, label));
				pieChart.invalidate();
				break;
			}
		}
	}
}
