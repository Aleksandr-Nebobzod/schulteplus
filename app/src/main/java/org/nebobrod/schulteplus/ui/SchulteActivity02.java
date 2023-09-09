package org.nebobrod.schulteplus.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.nebobrod.schulteplus.GridAdapter;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.SCell;
import org.nebobrod.schulteplus.STable;

public class SchulteActivity02 extends AppCompatActivity {

	private GridView mGrid;
	private STable exercise;
	private GridAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schulte02);

		exercise = new STable(5, 5);
		exercise.shuffle();

		mGrid = (GridView)findViewById(R.id.gvArea);
		mGrid.setNumColumns(exercise.getX());
		mGrid.setEnabled(true);

		mAdapter = new GridAdapter(this, exercise);
		mGrid.setAdapter(mAdapter);

		mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				SCell currentCell = exercise.getArea().get(position);
				Toast.makeText(SchulteActivity02.this, position+"_" + currentCell.getValue(), Toast.LENGTH_SHORT).show();
				exercise.shuffle();
				mAdapter.notifyDataSetChanged();
			}
		});

	}
}