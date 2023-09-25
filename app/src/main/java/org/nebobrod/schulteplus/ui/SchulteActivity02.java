package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.pHtml;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
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
					if (!exercise.endChecked()) {
						exercise.shuffle();
					} else { // if no next turn needed
						newExerciseDialog(exercise.getResults() +
								pHtml() + pHtml() + bHtml(getResources().getString(R.string.txt_one_more_q)));
					}
					mAdapter.notifyDataSetChanged();

				}
				Log.d(TAG, "onItemClick: " + exercise.journal.get(exercise.journal.size()-1));
			}
		});

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
}