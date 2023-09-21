package org.nebobrod.schulteplus.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.GridAdapter;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.SCell;
import org.nebobrod.schulteplus.STable;

public class SchulteActivity02 extends AppCompatActivity implements ResultDialogFragment.OnInputListener {
	public static final String TAG = "SchulteActivity02";
	public boolean continueExercise = true; // changes at click [Cancel]
	private GridView mGrid;
	private STable exercise;
	private GridAdapter mAdapter;

	@Override
	public void sendInput(boolean input) {
		Log.d(TAG, "sendInput: got the input: " + input);

		continueExercise = input;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schulte02);
		Intent intent = getIntent();
		ExerciseRunner runner = ExerciseRunner.getInstance(this);

		if (null == intent) {
			Toast.makeText(this, "" + this.getString(R.string.err_no_data), Toast.LENGTH_SHORT).show();
			finish();
		}

		mGrid = (GridView)findViewById(R.id.gvArea);
		exercise = new STable(runner.getX(), runner.getY(), mGrid.getContext());
		exercise.shuffle();

		mGrid.setNumColumns(exercise.getX());
		mGrid.setEnabled(true);

		mAdapter = new GridAdapter(this, exercise);
		mGrid.setAdapter(mAdapter);

		mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				SCell currentCell = exercise.getArea().get(position);
				//Toast.makeText(SchulteActivity02.this, position+"_" + currentCell.getValue(), Toast.LENGTH_SHORT).show();
				if (exercise.checkTurn(position)) {
					if (exercise.checkEnd()) {
//						Toast.makeText(SchulteActivity02.this, "Won within: " +
//								exercise.getResults(), Toast.LENGTH_LONG).show();
						showResult("Won within: " + exercise.getResults() + "\n" + getResources().getString(R.string.txt_continue));
//						ResultDialogFragment resultDialogFragment = ResultDialogFragment.newInstance(exercise.getResults() + "\n" + getResources().getString(R.string.txt_continue));
//						resultDialogFragment.show(getSupportFragmentManager(), "dialog");
						setResult(Activity.RESULT_OK);
						if (continueExercise) {
							// TODO: 21.09.2023 save result
							exercise = null;
							exercise = new STable(runner.getX(), runner.getY(), mGrid.getContext());
						} else {
							finish();
						}
					}
					exercise.shuffle();
					mAdapter.notifyDataSetChanged();
				}
				Log.d(TAG, "onItemClick: " + exercise.journal.get(exercise.journal.size()-1));
			}
		});

	}
	public void showResult (String message) {

		// DialogFragment.show() takes care of adding the fragment
		// in a transaction.  We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		DialogFragment newFragment = ResultDialogFragment.newInstance(message);
		newFragment.show(ft, "dialog");
	}
}