/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.schulte;

import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.GridAdapter;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.SCell;
import org.nebobrod.schulteplus.common.STable;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.ui.ExResultArrayAdapter;

import static org.nebobrod.schulteplus.Utils.*;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_COLOR_BLUE;
import static org.nebobrod.schulteplus.common.Const.KEY_SYMBOL_TYPE_COLOR_RED;
import static org.nebobrod.schulteplus.common.Const.SHOWN_06_SCHULTE_SPACE;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.Objects;

public class SchulteActivity extends AppCompatActivity {
	public static final String TAG = "SchulteActivity02";
	private GridView mGrid;
	private STable exercise;
	private GridAdapter mAdapter;
	private ExerciseRunner runner;
	private ExToolbar exToolbar;

	private DataRepos<ExResult> repos;
	private MutableLiveData<ExResult> resultLiveData = new MutableLiveData<>();
	private DialogInterface.OnClickListener cancelListener;
	private DialogInterface.OnClickListener restartListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_schulte);
		// lock orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		Intent intent = getIntent();
		if (null == intent) {
			Toast.makeText(this, "" + this.getString(R.string.err_no_data), Toast.LENGTH_SHORT).show();
			finish();
		}

		repos = new DataRepos(ExResult.class);
		runner = ExerciseRunner.getInstance();
		boolean feedbackHaptic = runner.getPrefHaptic();
		boolean feedbackSound = runner.getPrefSound();

		getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				ExResultArrayAdapter.feedbackDialog(SchulteActivity.this,
						null,
						getRes().getString(R.string.txt_ex_not_done) + "! " + getRes().getString(R.string.txt_continue_ex),
						null,
						(dialogInterface, i) -> finish());
			}
		});

		// Dialog buttons listeners
		cancelListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
//				repos.put(resultLiveData.getValue());
				ExerciseRunner.complete();
				finish();
			}
		};
		restartListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

				Log.d(TAG, "onClick: " + "note: " + resultLiveData.getValue().getNote() +
						" levelOfEmotion: " + resultLiveData.getValue().getLevelOfEmotion() +
						" sbEnergyLevel: " + resultLiveData.getValue().getLevelOfEnergy());
				ExerciseRunner.complete();
/*				exercise.reset();
				exToolbar.init();
				mAdapter.notifyDataSetChanged();*/
				initArea();
			}
		};

		// Prepare exercise
		mGrid = (GridView)findViewById(R.id.gvArea);
		//ExerciseRunner.getInstance();
		//ExerciseRunner.loadPreference(); // TODO: 03.05.2024 check necessity

		initArea();


		// Animate expected Cell (if lost)
		mGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				int expected = exercise.getExpectedPosition();
				View hintView = adapterView.getChildAt(expected);

				setViewZOrder((ViewGroup) adapterView, hintView, true);
//				animThrob(hintView, Color.valueOf(getColor(R.color.light_grey_A_green)));
				animThrob(hintView, null);
				return true;
			}
		});

		// Turn-click
		mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				feedbacks (view, feedbackHaptic, feedbackSound);
				SCell currentCell = exercise.getArea().get(position);
				//Toast.makeText(SchulteActivity02.this, position+"_" + currentCell.getValue(), Toast.LENGTH_SHORT).show();
//				if (position==1) Utils.showSnackBar(SchulteActivity02.this, position +"");
				if (exercise.isCorrectTurn(position)) {
					if (exercise.checkIsFinished()) {
						resultLiveData.setValue(exercise.calculateResults());
						ExerciseRunner.setExResult(Objects.requireNonNull(resultLiveData.getValue()));
						ExResultArrayAdapter.feedbackDialog(SchulteActivity.this,
								resultLiveData,
								getRes().getString(R.string.txt_ex_done_1) + "! " + getRes().getString(R.string.txt_continue_ex),
								restartListener,
								cancelListener);

//						newExerciseDialog(exercise.getResults().toMap() + pHtml() + pHtml() + bHtml(getRes().getString(R.string.txt_one_more_q)));
					} else { // continue ex
						if (ExerciseRunner.isShuffled()) {
							exercise.shuffle(); // if shuffle option in user Prefs
							mAdapter.notifyDataSetChanged();
						}
						// Update status bar
						exToolbar.refresh(exercise);
					}
				// Display an error
				} else if (ExerciseRunner.isHinted()) {
					exToolbar.plusMistake();
					exToolbar.refresh(exercise);
					setViewZOrder((ViewGroup) adapterView, view, true);
					animThrob(view, Color.valueOf(getColor(R.color.light_grey_A_red)));
				}
				Log.d(TAG, "onItemClick: " + exercise.journal.get(exercise.journal.size() - 1));
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (null != getSupportActionBar()) {
			Objects.requireNonNull(getSupportActionBar()).hide();
		}

		// Onboarding intro
		if (ExerciseRunner.isShowIntro() &&
				(0 == (ExerciseRunner.getShownIntros() & SHOWN_06_SCHULTE_SPACE))) {
			new TapTargetSequence(this)
					.targets(
							TapTarget.forBounds(new Rect(200, 100, 200, 100), getString(R.string.hint_schulte_space_title), getString(R.string.hint_schulte_space_desc))
									.outerCircleAlpha(0.9f)
									.outerCircleColor(R.color.black)
									.textColor(R.color.light_grey_A)
									.targetRadius(200)
									.transparentTarget(true)
									.cancelable(true)
					)
					.listener(new TapTargetSequence.Listener() {
						@Override
						public void onSequenceFinish() {
							ExerciseRunner.updateShownIntros(SHOWN_06_SCHULTE_SPACE);
						}
						@Override
						public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) { }
						@Override
						public void onSequenceCanceled(TapTarget lastTarget) { }
					}).start();
		}
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Read values from the "savedInstanceState"-object and put them in your textview
		exToolbar.refresh(exercise);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Save the values you need from your textview into "outState"-object
		super.onSaveInstanceState(outState);
	}

	/**
	 * On start preparations
	 */
	private void initArea() {
		exercise = new STable( runner.getX(), runner.getY(), ExerciseRunner.probDx(), ExerciseRunner.probDy(), ExerciseRunner.probW());
		ExerciseRunner.setExercise(exercise);

		// Toolbar for exercise initiation (if hints are chosen)
		exToolbar = new ExToolbar(findViewById(R.id.tb_custom));

		// Prepare exercise field
		mGrid.setNumColumns(exercise.getX());
		mGrid.setEnabled(true);
		mAdapter = new GridAdapter(this, exercise, ExerciseRunner.isSquared(), ExerciseRunner.getPrefTextScale());
		mGrid.setAdapter(mAdapter);
		mGrid.setLongClickable(true);

		if (ExerciseRunner.isCountDown()) {
			Utils.countdownDialog(this, new Runnable() {
				@Override
				public void run() {
					exercise.shuffle();
					mAdapter.notifyDataSetChanged();
				}
			});
		} else {
			exercise.shuffle();
			mAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * Exercise area toolbar
	 */
	class ExToolbar {
		 androidx.appcompat.widget.Toolbar toolbar;
		 TextView tvExpectedTurn, tvCounter, tvMistakes;
		 int hintCounter, hintMistakes;
		 String hintExpectedTurn;
		 Chronometer chmTime;

		public ExToolbar(Toolbar toolbar) {
			this.toolbar = toolbar;
			this.init();
		}

		private void init() {
			hintCounter = hintMistakes = 0;
			tvExpectedTurn = toolbar.findViewById(R.id.tv_expected_turn);
			tvCounter = toolbar.findViewById(R.id.tv_counter);
			tvMistakes = toolbar.findViewById(R.id.tv_mistakes);
			chmTime = toolbar.findViewById(R.id.chm_time);
			refresh(exercise);
			if (ExerciseRunner.isCountDown()) {
				chmTime.setBase(SystemClock.elapsedRealtime() + 4000);
			}
			chmTime.start();

			if (runner.isHinted()) {
				toolbar.setVisibility(View.VISIBLE);
			} else {
				toolbar.setVisibility(View.GONE);
			}
		}

		private void refresh(STable exercise) {
			setHintCounter(exercise.journal.size() - 1);
			if (KEY_SYMBOL_TYPE_COLOR_RED.equals(ExerciseRunner.getSymbolType()) ||
					KEY_SYMBOL_TYPE_COLOR_BLUE.equals(ExerciseRunner.getSymbolType())) {
				setHintExpectedColor(exercise.getExpectedColor());
				tvExpectedTurn.setText(exercise.getExpectedValue() + "");
			} else {
				setHintExpectedTurn(exercise.getExpectedText());
			}
			tvCounter.setText("" + hintCounter);
			tvMistakes.setText("" + hintMistakes);
		}

		public void setHintExpectedTurn(String hintExpectedTurn) {
			this.hintExpectedTurn = hintExpectedTurn;
			tvExpectedTurn.setText("" + hintExpectedTurn);
		}
		public void setHintExpectedColor(int hintExpectedColor) {
			tvExpectedTurn.setBackgroundColor(hintExpectedColor);
		}

		public void setHintCounter(int hintCounter) {
			this.hintCounter = hintCounter;
			tvCounter.setText("" + hintCounter);
		}

		public int getHintMistakes() {
			return hintMistakes;
		}

		public void setHintMistakes(int hintMistakes) {
			this.hintMistakes = hintMistakes;
			tvMistakes.setText("" + hintMistakes);
		}

		public Chronometer getChmTime() {
			return chmTime;
		}

		public void plusMistake() {
			tvMistakes.setText("" + ++hintMistakes);
		}
	}
}