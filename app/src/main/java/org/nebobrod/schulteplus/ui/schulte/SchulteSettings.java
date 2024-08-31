/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.schulte;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.overlayBadgedIcon;
import static org.nebobrod.schulteplus.common.Const.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import org.nebobrod.schulteplus.common.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.STable;
import org.nebobrod.schulteplus.data.ExType;

import java.util.ArrayList;
import java.util.Objects;

public class SchulteSettings extends PreferenceFragmentCompat implements SurfaceHolder.Callback, DrawerPreference.SurfaceViewCallback {
	// TODO: 28.09.2023 this and BasicSettings class are mostly same (think how to unify them)
	private static final String TAG = "SchulteSettings";
	ArrayList<Preference> exerciseTypeCheckBoxes = new ArrayList<>();
	androidx.preference.CheckBoxPreference chosen;
	private ExerciseRunner runner = ExerciseRunner.getInstance();
	private String[] exTypes;

	private boolean canDraw = false;
	private DrawerPreference drawerPreference;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Paint paint;


	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

		setPreferencesFromResource(R.xml.preferences_schulte, rootKey);

		exTypes = getRes().getStringArray(R.array.ex_type);
		initiateExerciseTypes();

		PreferenceScreen screen = this.getPreferenceScreen();

		drawerPreference = findPreference(KEY_PRF_PROB_DRAWER);
//		drawerPreference.isShown();
//		surfaceView = drawerPreference.getSurfaceView();

	}

	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		container.getContext().setTheme(R.style.preferenceScreen);
		Log.d(TAG, "onCreateView: " + view.toString());
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		/** Onboarding intro */
		if (ExerciseRunner.isShowIntro() &&
				(0 == (ExerciseRunner.getShownIntros() & SHOWN_02_SCHULTE))) {
			new TapTargetSequence(requireActivity())
					.targets(
//							new TapTargetViewWr(this, view, getString(R.string.hint_schulte_settings_title), getString(R.string.hint_schulte_settings_desc)).getTapTarget()
							TapTarget.forBounds(new Rect(200, 100, 200, 100), getString(R.string.hint_schulte_settings_title), getString(R.string.hint_schulte_settings_desc))
									.outerCircleAlpha(0.9f)
									.outerCircleColor(R.color.purple_700)
									.textColor(R.color.light_grey_D_yellow)
									.targetRadius(150)
									.transparentTarget(true)
									.cancelable(true)
					)
					.listener(new TapTargetSequence.Listener() {
						// This listener will tell us when interesting(tm) events happen in regards
						// to the sequence
						@Override
						public void onSequenceFinish() {
							//Toast.makeText(MainActivity.this, "onSequenceFinish", Toast.LENGTH_SHORT).show();
							ExerciseRunner.updateShownIntros(SHOWN_02_SCHULTE);
						}

						@Override
						public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
							//Toast.makeText(MainActivity.this, "onSequenceStep", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSequenceCanceled(TapTarget lastTarget) {
							//Toast.makeText(MainActivity.this, "onSequenceCanceled", Toast.LENGTH_SHORT).show();
						}
					}).start();
		}

		/* this was an attempt to update ProbDrawer in real time appearing on screen. Callback is better.
		this.getListView().setOnScrollChangeListener(new View.OnScrollChangeListener() {
			@Override
			public void onScrollChange(View view, int i, int i1, int i2, int i3) {
				if (null == surfaceView) {
					surfaceView = drawerPreference.getSurfaceView();
					if (null == surfaceView) return;
					surfaceHolder = surfaceView.getHolder();
					surfaceHolder.addCallback(new SurfaceHolder.Callback() {
						@Override
						public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
							//				drawProbabilities(shiftX10, shiftY10, w, false);
							double w = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_SURFACE))).getValue() / 10D;
							boolean probZero = ((SwitchPreference) findPreference(KEY_PRF_PROB_ZERO)).isChecked();
							int shiftX10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_X))).getValue();
							int shiftY10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_Y))).getValue();
							drawProbabilities(shiftX10, shiftY10, w, probZero);
						}

						@Override
						public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
							//				drawProbabilities(shiftX10, shiftY10, w, false);
							double w = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_SURFACE))).getValue() / 10D;
							boolean probZero = ((SwitchPreference) findPreference(KEY_PRF_PROB_ZERO)).isChecked();
							int shiftX10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_X))).getValue();
							int shiftY10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_Y))).getValue();
							drawProbabilities(shiftX10, shiftY10, w, probZero);
						}

						@Override
						public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

						}
					});
				}
			}
		});*/
//		surfaceView = drawerPreference.getSurfaceView();
//		Log.d(TAG, "onViewCreated: " + view.toString());
	}

	@Override
	public void onResume() {
		super.onResume();
		drawerPreference.getSurfaceView(this::onCallback, surfaceView);
		updatePrefScreen();
	}

	@Override
	public void onPause() {
		super.onPause();
		ExerciseRunner.savePreferences();
	}

	@Override
	public boolean onPreferenceTreeClick(@NonNull Preference preference) {
		// only for Group Check Boxes:
		if (exerciseTypeCheckBoxes.contains(preference)) {
			chosen = (androidx.preference.CheckBoxPreference) preference;
			for (Preference p : exerciseTypeCheckBoxes) {
				((androidx.preference.CheckBoxPreference) p).setChecked(false);
			}
			chosen.setChecked(true);

			updatePrefScreen();
		}

		if (KEY_PRF_RATINGS.equals(preference.getKey())) {
			enableOptions ( ! ((SwitchPreference)preference).isChecked()); 	// Action is opposite of ratings
		}

		if (KEY_PRF_PROB_ENABLED.equals(preference.getKey())) {
			enableProbability(((SwitchPreference) preference).isChecked());
		}

		if (KEY_PRF_PROB_SURFACE.equals(preference.getKey())) {
			drawProbabilities();
		}

		return super.onPreferenceTreeClick(preference);
	}

	/**
	 * @param action true for turn on probability mgmt
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void enableProbability(boolean action) {
		((Preference) findPreference(KEY_PRF_PROB_DRAWER)).setEnabled(action);
		((SwitchPreference) findPreference(KEY_PRF_PROB_ZERO)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_SURFACE)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setEnabled(action);

		if (surfaceView == null) return; // just for safety
		if (action) {
//			surfaceView.setBackgroundColor(Color.GRAY);
			drawProbabilities();

			surfaceView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {

					int startX, startY, shiftX10, shiftY10;
//					double w = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_SURFACE))).getValue()/10D;
//					int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						// absolute and relative points of center
						startX = (int) event.getX(); shiftX10 = (int) (10*(2F*startX/surfaceView.getWidth()-1));
						startY = (int) event.getY(); shiftY10 = (int) (10*(2F*startY/surfaceView.getHeight()-1));

						((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_X))).setValue(shiftX10);
						((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_Y))).setValue(shiftY10);

						drawProbabilities();
//						surfaceView.performClick();
						return true;
					}

					return false;
				}
			});
		} else {
			try {
				Canvas canvas = surfaceView.getHolder().lockCanvas();
				canvas.drawColor(Color.DKGRAY);
				surfaceView.getHolder().unlockCanvasAndPost(canvas);
			} catch (Exception e) {
				Log.e(TAG, "enableProbability: " + e.getMessage(), e);
			}
//			canvas = null;
//			surfaceView = null;
		}

	}

	private void drawProbabilities() {
		int shiftX10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_X))).getValue();
		int shiftY10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_Y))).getValue();
		double w = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_SURFACE))).getValue()/10D;
		boolean canBeZero = ((SwitchPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_ZERO))).isChecked();

		double zMin = 10;
		double zMax = -10;

		// Skip if isn't allowed to draw (prob mgmt is off)
		if (!((SwitchPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_ENABLED))).isChecked()) return;
		if (!canDraw) return; // seems the surfaceView is invisible
		if (surfaceView == null) return; // just for safety

		// Prepare the Paint
		{
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setColor(getRes().getColor(R.color.prob_color99));
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeWidth(24);

		}

		// Prepare array of row values
		for (int i = -10; i<=10; i++) {
			for (int j = -10; j<=10; j++) {
				double x,y,z;
				x = i / 10D; y = j / 10D;
				z = STable.camelSurface(x, y, .1*shiftX10, .1*shiftY10, w);
//				System.out.printf("\t'%1.3f'",z);

				zMin = Math.min(z, zMin);
				zMax = Math.max(z, zMax);

			}
//			System.out.println();
		}
//		System.out.printf("\tmin '%1.3f', ",zMin);
//		System.out.printf("\tmax '%1.3f'",zMax);
//		System.out.println();

		Canvas canvas = null;
		// Draw touch point:
/*						canvas = surfaceView.getHolder().lockCanvas();
						// clear:
//						canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
						canvas.drawColor(Color.GRAY);
						// draw:
						canvas.drawPoint(startX, startY, paint);
						surfaceView.getHolder().unlockCanvasAndPost(canvas);*/

		try {
			canvas = surfaceView.getHolder().lockCanvas();
			canvas.drawColor(Color.GRAY);
		} catch (Exception e){
			Log.e(TAG, "drawProbabilities: ", e);
			return;
		}
//						Random r = new Random();
		zMax -= zMin; // now z can be always between 0 and zMax
		for (int i = -10; i<=10; i++) {
			for (int j = -10; j<=10; j++) {
//							double x = r.nextDouble() * 2 - 1;
//							double y = r.nextDouble() * 2 - 1;
				double x = i / 10D;
				double y = j / 10D;

				double z = STable.camelSurface(x, y, .1*shiftX10, .1*shiftY10, w);

				z -= zMin;
				int value = (int) ((z-1.0E-10)/(zMax/4));

				switch (value) {
					case 0:
						paint.setColor(getRes().getColor(R.color.prob_color00));
						break;
					case 1:
						paint.setColor(getRes().getColor(R.color.prob_color25));
						break;
					case 2:
						paint.setColor(getRes().getColor(R.color.prob_color75));
						break;
					case 3:
						paint.setColor(getRes().getColor(R.color.prob_color99));
						break;
					default:
						paint.setColor(Color.BLACK);
				}

				canvas.drawPoint((int) ((x+1)*canvas.getWidth()/2), (int) ((y+1)*canvas.getHeight()/2), paint);
			}
		}

		surfaceView.getHolder().unlockCanvasAndPost(canvas);
		canvas = null;
	}

	private void enableOptions(boolean allowed) {
		//		allowed is true if ratings is off
		((PreferenceCategory) findPreference(KEY_PRF_OPTIONS)).setVisible(allowed);
		((PreferenceCategory) findPreference(KEY_PRF_OPTIONS)).setEnabled(allowed);

		((PreferenceCategory) findPreference(KEY_PRF_PROBABILITIES)).setVisible(allowed);
		((PreferenceCategory) findPreference(KEY_PRF_PROBABILITIES)).setEnabled(allowed);

		((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setEnabled(allowed);
		((SwitchPreference) findPreference(KEY_PRF_SQUARED)).setEnabled(allowed);
//		((DropDownPreference) findPreference(KEY_PRF_SYMBOLS)).setEnabled(allowed);
		((ListPreference) findPreference(KEY_PRF_SYMBOLS)).setEnabled(allowed);

//		((SeekBarPreference) findPreference(KEY_PRF_PROB_SURFACE)).setEnabled(action);
//		((SwitchPreference) findPreference(KEY_PRF_PROB_ZERO)).setEnabled(action);
//		((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setEnabled(action);
//		((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setEnabled(action);
		if (allowed) {
			// do nothing
		} else {
			// actually prob settings are get 0 in ExerciseRunner
//			((SeekBarPreference) findPreference(KEY_PRF_PROB_SURFACE)).setValue(0);
//			((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setValue(0);
//			((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setValue(0);
		}
	}

	private void updatePrefScreen(){
//		runner.loadPreference();
		EditTextPreference exType = findPreference(KEY_TYPE_OF_EXERCISE);

		// Find which checkbox of "group" is selected on the screen:
		for (Preference p: exerciseTypeCheckBoxes) {
			if (((androidx.preference.CheckBoxPreference) p).isChecked()) {
				chosen = (androidx.preference.CheckBoxPreference) p;
				break;
			}
		}
		// for safety
		if (null == chosen) {
			chosen = (androidx.preference.CheckBoxPreference) findPreference(KEY_PRF_EX_S1); //gcb_schulte_1_sequence
			chosen.setChecked(true);
		}
		exType.setText(chosen.getKey());
		runner.setExType(exType.getText().toString()); // set exType into the runner from invisible EditTextPreference field
		runner.setRatings(((SwitchPreference) findPreference(KEY_PRF_RATINGS)).isChecked());

		// set X & Y to runner
		switch (chosen.getKey()){
			case KEY_PRF_EX_S1:
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setEnabled(true);
				((SwitchPreference) findPreference(KEY_PRF_SQUARED)).setEnabled(true);
				enableOptions(true);
				// check if is Ratings
				if (runner.isRatings()) {
					enableOptions(false);
					findPreference(KEY_PRF_RATINGS).setEnabled(true);
					runner.setX((byte) 5);
					runner.setY((byte) 5);
				} else { // enable options PreferenceCategory for ease levels
					runner.setX((byte) ((androidx.preference.SeekBarPreference) findPreference(KEY_X_SIZE)).getValue());
					runner.setY((byte) ((androidx.preference.SeekBarPreference) findPreference(KEY_Y_SIZE)).getValue());
					// set hinted to runner
					runner.setHinted(((androidx.preference.SwitchPreference) findPreference(KEY_PRF_HINTED)).isChecked());
					runner.setCountDown(((androidx.preference.SwitchPreference) findPreference(KEY_PRF_COUNT_DOWN)).isChecked());
					runner.setSquared(((androidx.preference.SwitchPreference) findPreference(KEY_PRF_SQUARED)).isChecked());
//					runner.setSymbolType(((DropDownPreference) findPreference(KEY_PRF_SYMBOLS)).getValue());
					runner.setSymbolType(((ListPreference) findPreference(KEY_PRF_SYMBOLS)).getValue());
				}
				break;
			case KEY_PRF_EX_S2:
				runner.setX((byte) 7);
				runner.setY((byte) 7);
				// set options and disable PreferenceCategory for hard levels
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setChecked(true);
				((SwitchPreference) findPreference(KEY_PRF_SQUARED)).setChecked(true);
				((SwitchPreference) findPreference(KEY_PRF_HINTED)).setChecked(false);
				((SwitchPreference) findPreference(KEY_PRF_COUNT_DOWN)).setChecked(false);
				enableOptions(false);
				break;
			case KEY_PRF_EX_S3:
				runner.setX((byte) 10);
				runner.setY((byte) 10);
				// set options and disable PreferenceCategory for hard levels
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setChecked(true);
				((SwitchPreference) findPreference(KEY_PRF_SQUARED)).setChecked(true);
				((SwitchPreference) findPreference(KEY_PRF_HINTED)).setChecked(false);
				((SwitchPreference) findPreference(KEY_PRF_COUNT_DOWN)).setChecked(false);
				enableOptions(false);
				break;
			case KEY_PRF_EX_S4:
				// set options and disable PreferenceCategory for hard levels
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setChecked(true);
				((SwitchPreference) findPreference(KEY_PRF_HINTED)).setChecked(false);
				enableOptions(false);
				break;
			default:
				runner.setX((byte) 5);
				runner.setY((byte) 5);
				// enable options PreferenceCategory for ease levels
				enableOptions(true);
		}
	}

	/**
	 * method provides functionality "Group of Checkboxes" directly in _preferences.xml -- layout
	 */
	private void initiateExerciseTypes(){
		ArrayList<Preference> list = getPreferenceList(getPreferenceScreen(), new ArrayList<Preference>());
		exerciseTypeCheckBoxes.clear();
		for (Preference p : list) {
			String pKey = p.getKey();
			// prefix "gcb_" of Preference means Group Check Box
			if (p instanceof androidx.preference.CheckBoxPreference && pKey.startsWith("gcb_")) {
				p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						chosen = (androidx.preference.CheckBoxPreference) preference;
						return false;
					}
				});
				exerciseTypeCheckBoxes.add(p);
			}
			// Setting badge
			ExType exType = ExerciseRunner.getExTypes().get(pKey);
			if (exType != null && exType.getStatus() == ExType.FUNC_STATUS_PLANNED) {
				Drawable icon = p.getIcon();
				p.setIcon(overlayBadgedIcon(icon, getRes().getDrawable(R.drawable.ic_bagde_inprogress, null)));
				p.setEnabled(false);
			}
		}
	}

	/**
	 * Recursive filling preferences,
	 * @param p starting from root (i.e. Pref.Screen)
	 * @param list
	 * @return
	 */
	private ArrayList<Preference> getPreferenceList(Preference p, ArrayList<Preference> list) {
		if( p instanceof PreferenceCategory || p instanceof PreferenceScreen) {
			PreferenceGroup pGroup = (PreferenceGroup) p;
			int pCount = pGroup.getPreferenceCount();
			for(int i = 0; i < pCount; i++) {
				getPreferenceList(pGroup.getPreference(i), list); // recursive call
			}
		} else {
			list.add(p);
		}
		return list;
	}

	/** For the customised DrawerPreference -- surfaceView*/
	@Override
	public void onCallback(@Nullable SurfaceView sView) {
		if (null != sView) {
			surfaceView = sView;
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(this);

		}
	}

	@Override
	public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
		canDraw = true;
		drawProbabilities();
	}

	@Override
	public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
		;
	}

	@Override
	public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
		canDraw = false;
	}
}
