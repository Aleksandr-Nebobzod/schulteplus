package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.intFromString;
import static org.nebobrod.schulteplus.Utils.pHtml;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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

import com.google.android.material.snackbar.Snackbar;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.GridAdapter;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.SCell;
import org.nebobrod.schulteplus.STable;
import org.nebobrod.schulteplus.Utils;

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
			iExpectedTurn = exercise.getTurnNumber();

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
			tvExpectedTurn.setText("" + exercise.getTurnNumber());
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
		runner = ExerciseRunner.getInstance(getApplicationContext());
		boolean feedbackHaptic = runner.getPrefHaptic();
		boolean feedbackSound = runner.getPrefSound();

		if (null == intent) {
			Toast.makeText(this, "" + this.getString(R.string.err_no_data), Toast.LENGTH_SHORT).show();
			finish();
		}

		mGrid = (GridView)findViewById(R.id.gvArea);
		exercise = new STable(runner.getX(), runner.getY(), mGrid.getContext());
		ExerciseRunner.getInstance(getApplicationContext());
		ExerciseRunner.savePreferences(exercise);

		// Toolbar for exercise initiation (if hints are chosen)
		exToolbar = new ExToolbar(findViewById(R.id.tb_custom));

		mGrid.setNumColumns(exercise.getX());
		mGrid.setEnabled(true);



		mAdapter = new GridAdapter(this, exercise);
		mGrid.setAdapter(mAdapter);

		mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				Utils.feedbacks (v, feedbackHaptic, feedbackSound);
				SCell currentCell = exercise.getArea().get(position);
				//Toast.makeText(SchulteActivity02.this, position+"_" + currentCell.getValue(), Toast.LENGTH_SHORT).show();
				if (position==1) Utils.showSnackBar(SchulteActivity02.this, position +"");
				if (exercise.checkTurn(position)) {
					if (!exercise.endChecked()) {
						exercise.shuffle();
					} else { // if no next turn needed
						ExerciseRunner.savePreferences(exercise);
						newExerciseDialog(exercise.getResults() +
								pHtml() + pHtml() + bHtml(getResources().getString(R.string.txt_one_more_q)));
					}
					mAdapter.notifyDataSetChanged();

				} else if ("showMistakes"=="showMistakes") {
					exToolbar.plusMistake();
				}
				exToolbar.setiExpectedTurn(exercise.getTurnNumber());
				exToolbar.setiCounter(exercise.journal.size() - 1);
				Log.d(TAG, "onItemClick: " + exercise.journal.get(exercise.journal.size() - 1));

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		Objects.requireNonNull(getSupportActionBar()).hide();
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