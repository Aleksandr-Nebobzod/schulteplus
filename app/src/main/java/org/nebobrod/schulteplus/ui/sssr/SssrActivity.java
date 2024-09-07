/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.sssr;

import static org.nebobrod.schulteplus.Utils.timeStampOfLocalDate;
import static org.nebobrod.schulteplus.Utils.timeStampU;
import static org.nebobrod.schulteplus.Utils.toPlainHtml;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.slider.Slider;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Const;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.ExResultSssr;
import org.nebobrod.schulteplus.databinding.ActivitySssrBinding;
import org.nebobrod.schulteplus.ui.RichEditorDialogFragment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SssrActivity extends AppCompatActivity {
	public static final String TAG = "SssrActivity";
	private ActivitySssrBinding binding;

	SssrViewModel viewModel;

	private List<ExResultSssr> exResultData;
	private ExResultSssr exResult;
	private ExerciseRunner runner;

	private DataRepos<ExResultSssr> repos;
	private MutableLiveData<ExResultSssr> resultLD = new MutableLiveData<>();
	private DialogInterface.OnClickListener cancelListener;
	private DialogInterface.OnClickListener restartListener;

	private LocalDate selectedDate;
	private SwitchCompat dataProvided;
	private TextView tvDate;
	private ImageButton btnPrev, btnNext;

	private PieChart pieChart;
	Legend legend;
	List<PieEntry> spheres;
	// SeekBars were not renamed to Sliders:
	Slider sbJob, sbChores, sbPhysical, sbFamily, sbFriends, sbLeisure, sbSleep, sbSssr;
	SeekBar sbEmotion, sbEnergy;
	private Slider.OnSliderTouchListener stopChangeListener;
	EditText etNote;

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

		// Start getting data
		viewModel = new ViewModelProvider(this).get(SssrViewModel.class);
		viewModel.fetchExResults();

		// Checker of edit mode (puts * to tvDate)
		dataProvided = binding.swDataProvided;
		dataProvided.setChecked(false);

		// Date is a main field of the exercise
		tvDate = binding.tvDate;
		tvDate.setFocusableInTouchMode(true);
		tvDate.setOnClickListener(view -> showDatePicker(view));
		selectedDate = LocalDate.now().plusDays(-1L); // Yesterday by default
		exResult = new ExResultSssr(selectedDate, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, "");
		changeRecord(selectedDate);

		// Minus and Plus Day
		btnPrev = binding.btnArrowLeft;
		btnPrev.setOnClickListener(view -> {
			if (selectedDate.equals(LocalDate.now().minusDays(30))) {
				Toast.makeText(this, R.string.msg_sssr_limit_of_past, Toast.LENGTH_SHORT).show();
				return;			// No less days allowed
			}
			etNote.clearFocus();
			selectedDate = selectedDate.plusDays(-1);
			changeRecord(selectedDate);
		});
		btnNext = binding.btnArrowRight;
		btnNext.setOnClickListener(view -> {
			if (selectedDate.equals(LocalDate.now())) {
				Toast.makeText(this, R.string.msg_sssr_limit_of_future, Toast.LENGTH_SHORT).show();
				return;			// No future days allowed
			}
			etNote.clearFocus();
			selectedDate = selectedDate.plusDays(1);
			changeRecord(selectedDate);
		});

		// Pie Data Chart
		runner = ExerciseRunner.getInstance();
		pieChart = binding.pieChart;

		legend = pieChart.getLegend();
		legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
		legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
		legend.setOrientation(Legend.LegendOrientation.VERTICAL);
		legend.setDrawInside(false);
		legend.setTextColor(getColor(R.color.colorOnPrimarySurface));
		legend.setWordWrapEnabled(true);
		legend.setEnabled(true); 			// Turn On the Legend

		// No description
//		pieChart.getDescription().setText(getString(R.string.title_sssr));
//		pieChart.getDescription().setEnabled(false);

		// Circle
		pieChart.setExtraOffsets(10, 0, 70, 0); // Paddings
		pieChart.setHoleRadius(25f); 				// Central Circle Radius
		pieChart.setTransparentCircleRadius(50f); 	// Central Circle Transparent Radius
		pieChart.setHoleColor(Color.WHITE);
		pieChart.setCenterText(getString(R.string.lbl_sssr_main));

		// Labels
		pieChart.setDrawEntryLabels(true); 			// Turn on labels
//		pieChart.setDrawMarkers(true);
		pieChart.setDrawRoundedSlices(true);
		pieChart.setDrawSlicesUnderHole(false);
		pieChart.setEntryLabelTextSize(12f);
		pieChart.setEntryLabelColor(getColor(R.color.colorSecondary));

		// Sliders refreshes Pie and exResultSssr
		stopChangeListener = new Slider.OnSliderTouchListener() {
			@Override
			public void onStartTrackingTouch(@NonNull Slider slider) {/*no-op*/}
			@Override
			public void onStopTrackingTouch(@NonNull Slider slider) {
				int value = (int) slider.getValue();
				switch (slider.getId()) {
					case R.id.sb_job:
						exResult.setJob(value);
						break;
					case R.id.sb_physical:
						exResult.setPhysical(value);
						break;
					case R.id.sb_leisure:
						exResult.setLeisure(value);
						break;
					case R.id.sb_family:
						exResult.setFamily(value);
						break;
					case R.id.sb_friends:
						exResult.setFriends(value);
						break;
					case R.id.sb_chores:
						exResult.setChores(value);
						break;
					case R.id.sb_sleep:
						exResult.setSleep(value);
						break;
					case R.id.sb_sssr:
						exResult.setSssr(value);
						break;
					default:
						Log.w(TAG, "onStopTrackingTouch Slider: MISSED case");
				}
				exResult.setTimeStampStart(timeStampOfLocalDate(selectedDate));
				exResult.setTimeStamp(timeStampU());
				dataProvided.setChecked(true);
				updateSlidersAndPie(exResult);
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
		sbSssr.setFocusedByDefault(true);

		// Ex Notes
		etNote = binding.etNote;
		etNote.clearFocus();
		etNote.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				// finished editing
				String oldNote = exResult.getNote();
				String newNote = toPlainHtml(Html.toHtml(etNote.getText(), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE));
				StyleSpan[] mSpans = etNote.getText().getSpans(0, etNote.length(), StyleSpan.class);
				if (!oldNote.equals(newNote)) {
					dataProvided.setChecked(true);
					exResult.setNote(newNote);
				}
			}
		});
		etNote.setOnLongClickListener(view -> {
			String htmlContent = Html.toHtml(etNote.getText(), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
			RichEditorDialogFragment dialog = RichEditorDialogFragment.newInstance(htmlContent);
			// listener from interface of RichEditorDialogFragment
			dialog.setOnNoteEditedListener(newNote -> {
				etNote.setText(Html.fromHtml(newNote, Html.FROM_HTML_MODE_LEGACY));
				// finished editing
				String oldNote = exResult.getNote();
				if (!oldNote.equals(newNote)) {
					dataProvided.setChecked(true);
					exResult.setNote(newNote);
				}
			});
			dialog.show(getSupportFragmentManager(), "RichEditorDialog");
			return true;
		});

		// SeekBars a bit different
		SeekBar.OnSeekBarChangeListener sbChanged = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				dataProvided.setChecked(true);
				switch (seekBar.getId()) {
					case R.id.sb_emotion:
						exResult.setLevelOfEmotion(progress - 2);
						break;
					case R.id.sb_energy:
						exResult.setLevelOfEnergy(progress - 1);
						break;
					default:
						Log.w(TAG, "onStopTrackingTouch SeekBar: MISSED case");
				}
//				exResult.setTimeStampStart(timeStampOfLocalDate(selectedDate));
//				exResult.setTimeStamp(timeStampU());
			}
		};
		sbEmotion = binding.sbEmotion;
		sbEmotion.setOnSeekBarChangeListener(sbChanged);
		sbEnergy = binding.sbEnergy;
		sbEnergy.setOnSeekBarChangeListener(sbChanged);

		updateSlidersAndPie(exResult);

		// Subscribe onto data refresh
		viewModel.getExercisesMapLD().observe(this, exercisesMap -> {
			if (exercisesMap != null) {
				// Get exResult
				exResult = exercisesMap.get(selectedDate);

				// UI updates
				updateSlidersAndPie(exResult);
			}
		});
	}



	/**
	 * {@inheritDoc}
	 * <p>
	 * Dispatch onPause() to fragments.
	 */
	@Override
	protected void onPause() {
		// We may need to save edited
		etNote.clearFocus();
		if (dataProvided.isChecked()) {
			viewModel.saveRecord(exResult);
		}
		super.onPause();
	}

	private void changeRecord(LocalDate date) {

		// We may need to save edited
		if (dataProvided.isChecked()) {
			viewModel.saveRecord(exResult);
		}

		// new date, new record and cleared switcher
		tvDate.setText(date.toString());
		if (viewModel.getExercisesMapLD() != null &&
				viewModel.getExercisesMapLD().getValue() != null) {
			exResult = viewModel.getExercisesMapLD().getValue().get(date);
			updateSlidersAndPie(exResult);
		}
		dataProvided.setChecked(false);
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
					LocalDate newDate  = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
					if (!selectedDate.equals(newDate)) {
						selectedDate = newDate;
						changeRecord(selectedDate);
					}
				}, selectedDate.getYear(), selectedDate.getMonthValue(), selectedDate.getDayOfMonth());

		// Limit the future and Show Dialog
		datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
		datePickerDialog.show();
	}

	private void updateSlidersAndPie(ExResultSssr result) {
		spheres = new ArrayList<>();
		int value;

		// runner.isSwSssrSssr() is always true
		value = result.getSssr();
		sbSssr.setValue(value);
		// set inner hole reflecting SSSR (+ to avoid zero breaking the diagram)
		pieChart.setHoleRadius(value * 5 + 5);
		//spheres.add(new PieEntry(value, getString(R.string.lbl_sssr_main)));

		// Other sssr-data depends on switcher in Preferences
		if (runner.isSwSssrJob()) {
			value = result.getJob();
			animateSlider(sbJob, value);
			sbJob.setValue(value);
			spheres.add(new PieEntry(value, getString(R.string.lbl_sssr_job)));
		} else {
			findViewById(R.id.tv_job).setVisibility(View.GONE);
			sbJob.setVisibility(View.GONE);
		}

		if (runner.isSwSssrPhysical()) {
			value = result.getPhysical();
			animateSlider(sbPhysical, value);
			sbPhysical.setValue(value);
			spheres.add(new PieEntry(value, getString(R.string.lbl_sssr_physical)));
		} else {
			findViewById(R.id.tv_physical).setVisibility(View.GONE);
			sbPhysical.setVisibility(View.GONE);
		}

		if (runner.isSwSssrLeisure()) {
			value = result.getLeisure();
			animateSlider(sbLeisure, value);
			sbLeisure.setValue(value);
			spheres.add(new PieEntry(value, getString(R.string.lbl_sssr_leisure)));
		} else {
			findViewById(R.id.tv_leisure).setVisibility(View.GONE);
			sbLeisure.setVisibility(View.GONE);
		}

		if (runner.isSwSssrFamily()) {
			value = result.getFamily();
			animateSlider(sbFamily, value);
			sbFamily.setValue(value);
			spheres.add(new PieEntry(value, getString(R.string.lbl_sssr_family)));
		} else {
			findViewById(R.id.tv_family).setVisibility(View.GONE);
			sbFamily.setVisibility(View.GONE);
		}

		if (runner.isSwSssrFriends()) {
			value = result.getFriends();
			animateSlider(sbFriends, value);
			sbFriends.setValue(value);
			spheres.add(new PieEntry(value, getString(R.string.lbl_sssr_friends)));
		} else {
			findViewById(R.id.tv_friends).setVisibility(View.GONE);
			sbFriends.setVisibility(View.GONE);
		}

		if (runner.isSwSssrChores()) {
			value = result.getChores();
			animateSlider(sbChores, value);
			sbChores.setValue(value);
			spheres.add(new PieEntry(value, getString(R.string.lbl_sssr_chores)));
		} else {
			findViewById(R.id.tv_chores).setVisibility(View.GONE);
			sbChores.setVisibility(View.GONE);
		}

		if (runner.isSwSssrSleep()) {
			value = result.getSleep();
			animateSlider(sbSleep, value);
			sbSleep.setValue(value);
			spheres.add(new PieEntry(value, getString(R.string.lbl_sssr_sleep)));
		} else {
			findViewById(R.id.tv_sleep).setVisibility(View.GONE);
			sbSleep.setVisibility(View.GONE);
		}

		// Raw dataset
		PieDataSet dataSet = new PieDataSet(spheres, getString(R.string.title_sssr));

		// Text Labels
		dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
		// Set Value Labels position
		dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
//		dataSet.setValueLinePart1Length(0.6f);  // Far from center
		dataSet.setValueLinePart2Length(0.5f);  // connector
		dataSet.setValueLineColor(Color.TRANSPARENT);
		// Slices between segments
		dataSet.setSliceSpace(1.0f);

		// Colors
		dataSet.setColors(
//				Color.MAGENTA,
				getColor(R.color.light_grey_D_green),
				getColor(R.color.light_grey_D_blue),
				getColor(R.color.light_grey_D_navy),
				getColor(R.color.light_grey_D_purple),
				getColor(R.color.light_grey_D_red),
				getColor(R.color.light_grey_D_orange),
				getColor(R.color.light_grey_D_yellow));

		// Pie data
		PieData pieData = new PieData(dataSet);
//		pieData.setValueFormatter(new PercentFormatter());
		// tie dataset
		pieChart.setData(pieData);

		// Show
//		pieChart.animateX(100, Easing.EaseOutQuad);
		pieChart.invalidate();

		// Notes and EE
		etNote.setText(Html.fromHtml(result.getNote(), Html.FROM_HTML_MODE_LEGACY));
		sbEmotion.setProgress(result.getLevelOfEmotion() + 2, true);
		sbEnergy.setProgress(result.getLevelOfEnergy() + 1);
	}

	private void animateSlider(Slider slider, int to) {
		long duration = Const.ANIM_STEP_MILLIS * 2;
		int from = (int) slider.getValue();

		if (from == to) return;

		ValueAnimator animator = ValueAnimator.ofFloat(from, to);
		animator.setDuration(duration);
		animator.addUpdateListener(animation -> {
			float animatedValue = (float) animation.getAnimatedValue();
			slider.setValue(animatedValue);
		});
		animator.start();
	}

	public void updatePieEntry(PieChart pieChart, List<PieEntry> entries, float newValue, String label) {
		// Find the element to refresh
		for (int i = 0; i < entries.size(); i++) {
			PieEntry entry = entries.get(i);
			if (entry.getLabel().equals(label)) {
				// Update Value
				entries.set(i, new PieEntry(newValue, label));
				pieChart.invalidate();
				break;
			}
		}
	}
}
