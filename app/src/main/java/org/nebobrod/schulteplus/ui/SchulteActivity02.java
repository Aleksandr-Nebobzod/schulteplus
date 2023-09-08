package org.nebobrod.schulteplus.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

import org.nebobrod.schulteplus.GridAdapter;
import org.nebobrod.schulteplus.R;

public class SchulteActivity02 extends AppCompatActivity {

	private GridView mGrid;
	private GridAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schulte02);

		mGrid = (GridView)findViewById(R.id.field);
		mGrid.setNumColumns(6);
		mGrid.setEnabled(true);

		mAdapter = new GridAdapter(this, 6, 6);
		mGrid.setAdapter(mAdapter);

	}
}