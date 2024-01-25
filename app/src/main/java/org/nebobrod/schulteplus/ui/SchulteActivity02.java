package org.nebobrod.schulteplus.ui;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.GridAdapter;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.SCell;
import org.nebobrod.schulteplus.STable;
import org.nebobrod.schulteplus.Utils;
import static org.nebobrod.schulteplus.Utils.*;
//import static org.nebobrod.schulteplus.Utils.bHtml;
//import static org.nebobrod.schulteplus.Utils.getRes;
//import static org.nebobrod.schulteplus.Utils.pHtml;

import java.util.Objects;

public class SchulteActivity02 extends AppCompatActivity {
	public static final String TAG = "SchulteActivity02";
	private GridView mGrid;
	private STable exercise;
	private GridAdapter mAdapter;
	ExerciseRunner runner;
	private ExToolbar exToolbar;


	class ExToolbar {
		 androidx.appcompat.widget.Toolbar toolbar;
		 TextView tvExpectedTurn, tvCounter, tvMistakes;
		 int iExpectedTurn, iCounter, iMistakes;
		 Chronometer chmTime;

		public ExToolbar(Toolbar toolbar) {
			this.toolbar = toolbar;
			this.init();
			if (!runner.isHinted()) {
				toolbar.setVisibility(View.GONE);
			}
		}

		private void init() {
			iCounter = iMistakes = 0;
			iExpectedTurn = exercise.getExpectedValue();

			tvExpectedTurn = toolbar.findViewById(R.id.tv_expected_turn);
			tvExpectedTurn.setText("" + iExpectedTurn);
			tvCounter = toolbar.findViewById(R.id.tv_counter);
			tvCounter.setText("0");
			tvMistakes = toolbar.findViewById(R.id.tv_mistakes);
			tvMistakes.setText("0");
			chmTime = toolbar.findViewById(R.id.chm_time);
			chmTime.setBase(SystemClock.elapsedRealtime());
			chmTime.start();
		}

		private void refresh() {
			tvExpectedTurn.setText("" + exercise.getExpectedValue());
			tvCounter.setText("" + iCounter);
			tvMistakes.setText("" + iMistakes);
//			chmTime.setBase(SystemClock.elapsedRealtime());
			chmTime.start();
		}

		public int getiExpectedTurn() {
			return iExpectedTurn;
		}

		public int getiCounter() {
			return iCounter;
		}

		public int getiMistakes() {
			return iMistakes;
		}

		public Chronometer getChmTime() {
			return chmTime;
		}

		public void setiExpectedTurn(int iExpectedTurn) {
			this.iExpectedTurn = iExpectedTurn;
			tvExpectedTurn.setText("" + iExpectedTurn);
		}

		public void setiCounter(int iCounter) {
			this.iCounter = iCounter;
			tvCounter.setText("" + iCounter);
		}

		public void setiMistakes(int iMistakes) {
			this.iMistakes = iMistakes;
			tvMistakes.setText("" + iMistakes);
		}

		public void plusMistake() {
			tvMistakes.setText("" + ++iMistakes);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_schulte02);
		Intent intent = getIntent();
		runner = ExerciseRunner.getInstance();
		boolean feedbackHaptic = runner.getPrefHaptic();
		boolean feedbackSound = runner.getPrefSound();

		if (null == intent) {
			Toast.makeText(this, "" + this.getString(R.string.err_no_data), Toast.LENGTH_SHORT).show();
			finish();
		}

		mGrid = (GridView)findViewById(R.id.gvArea);
		ExerciseRunner.loadPreference();
		exercise = new STable(runner.getX(), runner.getY(), ExerciseRunner.probDx(), ExerciseRunner.probDy(), ExerciseRunner.probW());
		ExerciseRunner.savePreferences(exercise);

		// Toolbar for exercise initiation (if hints are chosen)
		exToolbar = new ExToolbar(findViewById(R.id.tb_custom));

		mGrid.setNumColumns(exercise.getX());
		mGrid.setEnabled(true);



		mAdapter = new GridAdapter(this, exercise);
		mGrid.setAdapter(mAdapter);

		mGrid.setLongClickable(true);

		mGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				int expected = exercise.getExpectedPosition();

				View v = adapterView.getChildAt(expected);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//					animThrob(v, Color.valueOf(getColor(R.color.light_grey_A_green)));
					animThrob(v, null);

				}
				return true;
			}
		});

		mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				feedbacks (v, feedbackHaptic, feedbackSound);
				SCell currentCell = exercise.getArea().get(position);
				//Toast.makeText(SchulteActivity02.this, position+"_" + currentCell.getValue(), Toast.LENGTH_SHORT).show();
//				if (position==1) Utils.showSnackBar(SchulteActivity02.this, position +"");
				if (exercise.checkTurn(position)) {
					if (!exercise.endChecked()) { // continue ex
						exercise.shuffle();
					} else { // of Fin if no next turn needed
						ExerciseRunner.savePreferences(exercise);
						newExerciseDialog(exercise.getResults() +
								pHtml() + pHtml() + bHtml(getRes().getString(R.string.txt_one_more_q)));
					}
					mAdapter.notifyDataSetChanged();

				} else if (ExerciseRunner.isHinted()) {
					exToolbar.plusMistake();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						animThrob(v, Color.valueOf(getColor(R.color.light_grey_A_red)));
					}
				}
				exToolbar.setiExpectedTurn(exercise.getExpectedValue());
				exToolbar.setiCounter(exercise.journal.size() - 1);
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
		Context context = this;
		DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				finish();
			}
		};

		Utils.resultDialog(context, getRes().getString(R.string.txt_continue_ex) + "?",
				null,
				cancelListener);
//		super.onBackPressed();
	}

	private void newExerciseDialog(String s) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		final FrameLayout frameView = new FrameLayout(this);
		builder.setView(frameView);
		final AlertDialog alertDialog = builder.create();

		//alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		alertDialog.getWindow().setDimAmount(0);

		LayoutInflater inflater = alertDialog.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.activity_schulte_result_df, frameView);
		TextView txtTitle, txtMessage;
		Button btnOk, btnCancel;

		txtTitle = dialoglayout.findViewById(R.id.txtTitle);
		txtMessage = dialoglayout.findViewById(R.id.txtMessage);
		btnCancel = dialoglayout.findViewById(R.id.btnCancel);
		btnOk = dialoglayout.findViewById(R.id.btnOK);

		txtTitle.setText(R.string.title_result);
		txtMessage.setText(Html.fromHtml(s));

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getText(R.string.lbl_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				exercise.reset();
				exToolbar.init();
				mAdapter.notifyDataSetChanged();
			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getText(R.string.lbl_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				//dialogInterface.dismiss();
				finish();
			}
		});

		alertDialog.show();
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
}