/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.basics;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.common.Const.SHOWN_05_BASE_SPACE;

import android.annotation.SuppressLint;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import org.nebobrod.schulteplus.common.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.STable;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.ui.ExResultArrayAdapter;
import org.nebobrod.schulteplus.databinding.ActivityBasicsBinding; // TODO: 01.10.2023 figure it out!
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.ui.TapTargetViewWr;

import java.util.Locale;
import java.util.Objects;

/**
 * A full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BasicsActivity extends AppCompatActivity {
	private static final String TAG = "BasicsActivity";
	// private DataRepos<ExResultBasics> repos;
	private STable exercise;
	private MutableLiveData<ExResult> resultLiveData = new MutableLiveData<>();

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

	/** OK - continue, do nothing
	 * (Basic exercise can not finish unsuccessfully) */
	DialogInterface.OnClickListener okListener = null;

	/** Cancel button means Complete (well finished exercise) */
	DialogInterface.OnClickListener cancelListener = (dialogInterface, i) -> {
		ExerciseRunner.setExResult(Objects.requireNonNull(resultLiveData.getValue()));
		ExerciseRunner.complete();
		finish();
		// resultLiveData.setValue(exercise.calculateResults());
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				btExit.performClick();
			}
		});

		binding = ActivityBasicsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		// repos = new DataRepos(ExResult.class);
		ExerciseRunner.getInstance();
		exercise = new STable(1, 1);
		ExerciseRunner.start(exercise);
		resultLiveData.setValue(ExerciseRunner.getExResult());


		mVisible = true;
		mControlsView = binding.fullscreenContentControls;
		mContentView = binding.fullscreenContent;
		btExit = binding.btExit;
		btDistraction = binding.btDistraction;
		tvCounter  = binding.tvCounter; tvCounter.setText("0");
		tvClock = binding.tvTime; tvClock.setText("0:00");
//		chmTime = binding.chmTime; // in Basics no alive clock is useful

		if (ExerciseRunner.isHinted()) {
			tvCounter.setVisibility(View.VISIBLE);
			tvClock.setVisibility(View.VISIBLE);
		} else {
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

				resultLiveData.setValue(exercise.calculateResults());

				// Call Dialog
				ExResultArrayAdapter.feedbackDialog(BasicsActivity.this,
						resultLiveData,
						getRes().getString(R.string.txt_continue_ex),
						okListener,
						cancelListener);
			}
		});
		btDistraction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Utils.feedbacks (view, feedbackHaptic, feedbackSound);
				exercise.isCorrectTurn(0);
				String s = String.valueOf(exercise.journal.size() - 1);
				tvCounter.setText(s);
				long time = (System.nanoTime()-timeStarted)/1000000000;
				s = String.format(Locale.ENGLISH, "%1$d:%2$02d", time/60,time%60);
				tvClock.setText(s);

				// TODO: 23.05.2024 check if it's necessary
				//resultLiveData.setValue(exercise.getResults());
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

/*
	private void z_newExerciseDialog(String s) {
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
*/

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
				// sg means simple graphics or static graphics
				drawableName = "sg_" + drawableName.substring(4);

				int resourceId;
				resourceId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
				if (drawableName.contains("dancing")) {
					if (resourceId != 0) {
						Glide.with(this)
								.asGif()
								.override(600, 400) // Limitation of size
								.load(resourceId)
								.apply(new RequestOptions().placeholder(R.drawable.ic_baseline_cached_24)) // for long loads
								.into(ivContent); // put GIF into ImageView
					}
				} else {
					ivContent.setImageResource(resourceId);
				}

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

	/**
	 * {@inheritDoc}
	 * <p>
	 * Dispatch onResume() to fragments.  Note that for better inter-operation
	 * with older versions of the platform, at the point of this call the
	 * fragments attached to the activity are <em>not</em> resumed.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// Onboarding intro
		if (ExerciseRunner.isShowIntro() &&
				(0 == (ExerciseRunner.getShownIntros() & SHOWN_05_BASE_SPACE))) {
			new TapTargetSequence(this)
					.targets(
							new TapTargetViewWr(this, mControlsView, getString(R.string.hint_base_space_title), getString(R.string.hint_base_space_desc)).getTapTarget()
					)
					.listener(new TapTargetSequence.Listener() {
						@Override
						public void onSequenceFinish() {
							ExerciseRunner.updateShownIntros(SHOWN_05_BASE_SPACE);
						}
						@Override
						public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) { }
						@Override
						public void onSequenceCanceled(TapTarget lastTarget) { }
					}).start();
		}
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