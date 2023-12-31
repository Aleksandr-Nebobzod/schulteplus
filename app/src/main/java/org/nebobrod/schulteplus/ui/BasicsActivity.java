package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.pHtml;
import static org.nebobrod.schulteplus.Utils.tHtml;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.STable;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.databinding.ActivityBasicsBinding; // TODO: 01.10.2023 figure it out!
import org.nebobrod.schulteplus.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BasicsActivity extends AppCompatActivity {
	private static final String TAG = "BasicsActivity";
	private STable exercise;

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;  //here was true

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * Some older devices needs a small delay between UI widget updates
	 * and a change of the status and navigation bar.
	 */
	private static final int UI_ANIMATION_DELAY = 300;

	private final Handler mHideHandler = new Handler(Looper.myLooper());
	private View mContentView;
	private final Runnable mHidePart2Runnable = new Runnable() {
		@SuppressLint("InlinedApi")
		@Override
		public void run() {
			// Delayed removal of status and navigation bar
			if (Build.VERSION.SDK_INT >= 30) {
				mContentView.getWindowInsetsController().hide(
						WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
			} else {
				// Note that some of these constants are new as of API 16 (Jelly Bean)
				// and API 19 (KitKat). It is safe to use them, as they are inlined
				// at compile-time and do nothing on earlier devices.
				mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			}
		}
	};

	private View mControlsView;
	private View btExit;
	private View btDistraction;
	private TextView tvCounter, tvClock;
//	private Chronometer chmTime;

	private final Runnable mShowPart2Runnable = new Runnable() {

		@Override
		public void run() {
// this autogenerated block is turned off (dont need to hide mControlsView)
//			// Delayed display of UI elements
//			ActionBar actionBar = getSupportActionBar();
//			if (actionBar != null) {
//				actionBar.show();
//			}
//			mControlsView.setVisibility(View.VISIBLE);
		}
	};
	private boolean mVisible;
	private final Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
// this autogenerated block is turned off (dont need to hide mControlsView)
//			hide();
		}
	};
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (AUTO_HIDE) {
						delayedHide(AUTO_HIDE_DELAY_MILLIS);
					}
					break;
				case MotionEvent.ACTION_UP:
//					this autogenerated text to be commented because calculates twice extra
//					view.performClick();
					break;
				default:
					break;
			}
			return false;
		}
	};
	private ActivityBasicsBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityBasicsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		exercise = new STable(1, 1);
		ExerciseRunner.getInstance();
		ExerciseRunner.savePreferences(exercise);


		mVisible = true;
		mControlsView = binding.fullscreenContentControls;
		mContentView = binding.fullscreenContent;
		btExit = binding.btExit;
		btDistraction = binding.btDistraction;
		tvCounter  = binding.tvCounter; tvCounter.setText("0");
		tvClock = binding.tvTime; tvClock.setText("0:00");
//		chmTime = binding.chmTime; // in Basics no alive clock is useful

		if (!ExerciseRunner.isHinted()) {
			tvCounter.setVisibility(View.GONE);
			tvClock.setVisibility(View.GONE);
		}
		long timeStarted = System.nanoTime();

		boolean feedbackHaptic = ExerciseRunner.getPrefHaptic();
		boolean feedbackSound = ExerciseRunner.getPrefSound();

		hide();
		// Set up the user interaction to manually show or hide the system UI.
		mContentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggle();
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		//binding.btDistraction.setOnTouchListener(mDelayHideTouchListener);

		btExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btDistraction.performClick();
				newExerciseDialog(Utils.getRes().getString(R.string.lbl_time) + ":" + tHtml()  + bHtml(tvClock.getText().toString()) + pHtml()
									+ Utils.getRes().getString(R.string.lbl_events) + ":" + tHtml()  + bHtml(tvCounter.getText().toString()) + pHtml()
									+ pHtml() + bHtml(getResources().getString(R.string.txt_continue_ex) + "?"));
			}
		});
		btDistraction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Utils.feedbacks (view, feedbackHaptic, feedbackSound);
				exercise.checkTurn(0);
				String s = String.valueOf(exercise.journal.size() - 1);
				tvCounter.setText(s);
				long time = (System.nanoTime()-timeStarted)/1000000000;
				s = String.format("%1$d:%2$02d", time/60,time%60);
				tvClock.setText(s);
			}
		});

		// Get external intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		ImageView ivContent = binding.fullscreenContent;

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if (type.startsWith("image/")) {
				Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				if (imageUri != null) {
					try {
						ivContent.setImageURI(imageUri);
						ivContent.setTag("external");
					} catch (Exception e) {
						Toast.makeText(this, R.string.err_unknown, Toast.LENGTH_SHORT).show();
					}
					}
				}
			}
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
//				exercise.reset();
				dialogInterface.dismiss();
			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getText(R.string.lbl_no), (dialogInterface, i) -> {
			//dialogInterface.dismiss();
			exercise.setFinished(true);
			ExerciseRunner.savePreferences(exercise);
			finish();
		});

		alertDialog.show();
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
/*
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			setContentView(R.layout.activity_basics); // it will use .xml from /res/layout
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			setContentView(R.layout.activity_basics); // it will use xml from /res/layout-land
		}*/
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Read values from the "savedInstanceState"-object and put them in your textview
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Save the values you need from your textview into "outState"-object
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);

//		chmTime.setBase(SystemClock.elapsedRealtime());
//		chmTime.start();

		ExerciseRunner runner = ExerciseRunner.getInstance();
		runner.loadPreference();

		// Take preference i.e. key="gcb_bas_dot"
		// and make drawableName = "sg_bas_dot" and put it into iv:
		try {
			ImageView ivContent= findViewById(R.id.fullscreen_content);
			if ("empty".equals(ivContent.getTag())) {
				String drawableName =  runner.getExType();
				drawableName = "sg_" + drawableName.substring(4);
				int resourceId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
				ivContent.setImageResource(resourceId);
				ivContent.setTag(drawableName);
				}
		} catch (Exception e) {
			Log.e(TAG, "onPostCreate: ", e);
		}


	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
	}

	@Override
	public void onBackPressed() {
		Context context = this;

		btDistraction.performClick();

		DialogInterface.OnClickListener okListener = (dialogInterface, i) -> {
			// Means continue ex i.e. do nothing
		};
		DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				exercise.setFinished(true);
				ExerciseRunner.savePreferences(exercise);
				finish();
			}
		};

		Utils.resultDialog(context, getRes().getString(R.string.txt_continue_ex) + "?",
				okListener,
				cancelListener);
//		super.onBackPressed();
	}

	private void toggle() {
		if (mVisible) {
			hide();
		} else {
			 show();
		}
	}

	private void hide() {
		// Hide UI first
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		//mControlsView.setVisibility(View.GONE);
		mVisible = false;

		// Schedule a runnable to remove the status and navigation bar after a delay
		mHideHandler.removeCallbacks(mShowPart2Runnable);
		mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
	}

	private void show() {
		// Show the system bar
		if (Build.VERSION.SDK_INT >= 30) {
			mContentView.getWindowInsetsController().show(
					WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
		} else {
			mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
		}
		mVisible = true;

		// Schedule a runnable to display UI elements after a delay
		mHideHandler.removeCallbacks(mHidePart2Runnable);
		mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
	}

	/**
	 * Schedules a call to hide() in delay milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}