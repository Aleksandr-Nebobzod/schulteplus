package org.nebobrod.schulteplus.ui;

import androidx.appcompat.app.AppCompatActivity;

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

public class SchulteActivity02 extends AppCompatActivity {
	public static final String TAG = "SchulteActivity02";
	private GridView mGrid;
	private STable exercise;
	private GridAdapter mAdapter;

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
						Toast.makeText(SchulteActivity02.this, "Won within: " +
								exercise.getResults(), Toast.LENGTH_LONG).show();
						//setResult(Activity.RESULT_OK);
						finish();
					}
					exercise.shuffle();
					mAdapter.notifyDataSetChanged();
				}
				Log.d(TAG, "onItemClick: " + exercise.journal.get(exercise.journal.size()-1));
			}
		});

	}
}