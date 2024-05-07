/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.GridAdapter;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.SCell;
import org.nebobrod.schulteplus.common.STable;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.ExResultArrayAdapter;

import static org.nebobrod.schulteplus.Utils.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Objects;

public class SchulteActivity02 extends AppCompatActivity {
	public static final String TAG = "SchulteActivity02";
	private GridView mGrid;
	private STable exercise;
	private GridAdapter mAdapter;
	private ExerciseRunner runner;
	private ExToolbar exToolbar;

	private DataRepos repos;
	private MutableLiveData<ExResult> resultLiveData = new MutableLiveData<>();
	private DialogInterface.OnClickListener cancelListener;
	private DialogInterface.OnClickListener restartListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_schulte02);
		// lock orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		Intent intent = getIntent();
		if (null == intent) {
			Toast.makeText(this, "" + this.getString(R.string.err_no_data), Toast.LENGTH_SHORT).show();
			finish();
		}

		runner = ExerciseRunner.getInstance();
		boolean feedbackHaptic = runner.getPrefHaptic();
		boolean feedbackSound = runner.getPrefSound();

		// Dialog buttons listeners
		cancelListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				repos.create(resultLiveData.getValue());
				finish();
			}
		};
		restartListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

				Log.d(TAG, "onClick: " + "note: " + resultLiveData.getValue().getNote() +
						" levelOfEmotion: " + resultLiveData.getValue().getLevelOfEmotion() +
						" sbEnergyLevel: " + resultLiveData.getValue().getLevelOfEnergy());
				repos.create(resultLiveData.getValue());
				exercise.reset();
				exToolbar.init();
				mAdapter.notifyDataSetChanged();
			}
		};

		// Prepare exercise
		mGrid = (GridView)findViewById(R.id.gvArea);
		ExerciseRunner.getInstance();
		ExerciseRunner.loadPreference(); // TODO: 03.05.2024 check necessity
		exercise = new STable( runner.getX(), runner.getY(), ExerciseRunner.probDx(), ExerciseRunner.probDy(), ExerciseRunner.probW());
		ExerciseRunner.savePreferences(exercise);
		repos = new DataRepos();

		// Toolbar for exercise initiation (if hints are chosen)
		exToolbar = new ExToolbar(findViewById(R.id.tb_custom));

		// Prepare exercise field
		mGrid.setNumColumns(exercise.getX());
		mGrid.setEnabled(true);
		mAdapter = new GridAdapter(this, exercise);
		mGrid.setAdapter(mAdapter);
		mGrid.setLongClickable(true);

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
						ExerciseRunner.savePreferences(exercise);
						resultLiveData.setValue(exercise.getResults());
						ExResultArrayAdapter.feedbackDialog(SchulteActivity02.this,
								resultLiveData,
								getRes().getString(R.string.txt_ex_done_1) + "! " + getRes().getString(R.string.txt_continue_ex) + "?",
								restartListener,
								cancelListener);

//						newExerciseDialog(exercise.getResults().toMap() + pHtml() + pHtml() + bHtml(getRes().getString(R.string.txt_one_more_q)));
					} else { // continue ex
						exercise.shuffle();
						mAdapter.notifyDataSetChanged();
					}
				// Display an error
				} else if (ExerciseRunner.isHinted()) {
					exToolbar.plusMistake();
					setViewZOrder((ViewGroup) adapterView, view, true);
					animThrob(view, Color.valueOf(getColor(R.color.light_grey_A_red)));
				}
				exToolbar.setHintExpectedTurn(exercise.getExpectedValue());
				exToolbar.setHintCounter(exercise.journal.size() - 1);
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
	}

	@Override
	public void onBackPressed() {
		ExResultArrayAdapter.feedbackDialog(this,
				null,
				getRes().getString(R.string.txt_ex_not_done) + "! " + getRes().getString(R.string.txt_continue_ex) + "?",
				null,
				(dialogInterface, i) -> finish());
		// TODO: 29.04.2024 check what to change for deprecated:
		super.onBackPressed();
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Read values from the "savedInstanceState"-object and put them in your textview
		exToolbar.refresh();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Save the values you need from your textview into "outState"-object
		super.onSaveInstanceState(outState);
	}

	class ExToolbar {
		 androidx.appcompat.widget.Toolbar toolbar;
		 TextView tvExpectedTurn, tvCounter, tvMistakes;
		 int hintExpectedTurn, hintCounter, hintMistakes;
		 Chronometer chmTime;

		public ExToolbar(Toolbar toolbar) {
			this.toolbar = toolbar;
			this.init();
		}

		private void init() {
			hintCounter = hintMistakes = 0;
			hintExpectedTurn = exercise.getExpectedValue();

			tvExpectedTurn = toolbar.findViewById(R.id.tv_expected_turn);
			tvExpectedTurn.setText("" + hintExpectedTurn);
			tvCounter = toolbar.findViewById(R.id.tv_counter);
			tvCounter.setText("0");
			tvMistakes = toolbar.findViewById(R.id.tv_mistakes);
			tvMistakes.setText("0");
			chmTime = toolbar.findViewById(R.id.chm_time);
			chmTime.setBase(SystemClock.elapsedRealtime());
			chmTime.start();

			if (runner.isHinted()) {
				toolbar.setVisibility(View.VISIBLE);
			} else {
				toolbar.setVisibility(View.GONE);
			}
		}

		private void refresh() {
			tvExpectedTurn.setText("" + exercise.getExpectedValue());
			tvCounter.setText("" + hintCounter);
			tvMistakes.setText("" + hintMistakes);
//			chmTime.setBase(SystemClock.elapsedRealtime());
			chmTime.start();
		}

		public int getHintExpectedTurn() {
			return hintExpectedTurn;
		}

		public void setHintExpectedTurn(int hintExpectedTurn) {
			this.hintExpectedTurn = hintExpectedTurn;
			tvExpectedTurn.setText("" + hintExpectedTurn);
		}

		public int getHintCounter() {
			return hintCounter;
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