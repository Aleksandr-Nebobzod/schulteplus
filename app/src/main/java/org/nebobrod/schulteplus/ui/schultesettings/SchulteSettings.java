package org.nebobrod.schulteplus.ui.schultesettings;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Const.*;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.R;

import java.util.ArrayList;

public class SchulteSettings extends PreferenceFragmentCompat {
	// TODO: 28.09.2023 this and BasicSettings class are mostly same (think how to unify them)
	private static final String TAG = "SchulteSettings";
	ArrayList<Preference> exerciseTypes = new ArrayList<>();
	androidx.preference.CheckBoxPreference chosen;
	private ExerciseRunner runner = ExerciseRunner.getInstance(getContext());
	private String[] exTypes;

	private DrawerPreference drawerPreference;
	private SurfaceView surfaceView;
	private Paint paint;


	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
		setPreferencesFromResource(R.xml.preferences_schulte, rootKey);
		PreferenceScreen screen = this.getPreferenceScreen();
		exTypes = getRes().getStringArray(R.array.ex_type);

		initiateExerciseTypes();

		drawerPreference = findPreference(KEY_PRF_PROB_DRAWER);

	}




	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

/*		if (null != view) {
			SurfaceView surfaceView = (SurfaceView) view.findViewById(android.R.id.list);
			if (null != surfaceView)
				surfaceView.setPadding(0, 0, 0, 0);
		}*/

		surfaceView = drawerPreference.getSurfaceView();


		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	private void updatePrefScreen(){
		runner.loadPreference(getContext());
		EditTextPreference exType = findPreference("prf_ex_type");

		// Find which checkbox of "group" is selected on the screen:
		for (Preference p: exerciseTypes) {
			if (((androidx.preference.CheckBoxPreference) p).isChecked()) {
				chosen = (androidx.preference.CheckBoxPreference) p;
				break;
			}
		}
		if (null == chosen) {
			chosen = (androidx.preference.CheckBoxPreference) findPreference("gcb_adv_schulte_1_sequence");
			chosen.setChecked(true);
		}
		exType.setText(chosen.getKey());
		runner.setExType(exType.getText().toString()); // set exType into the runner from invisible pref et
		switch (chosen.getKey()){	// disable options PreferenceCategory for hard levels
			case "gcb_schulte_1_sequence":
				findPreference("prf_cat_options").setEnabled(true);
				break;
			default:
				findPreference("prf_cat_options").setEnabled(false);
		}
		// set X & Y to runner
		switch (chosen.getKey()){
			case "gcb_schulte_1_sequence":
				runner.setX((byte) ((androidx.preference.SeekBarPreference) findPreference("prf_x_size")).getValue());
				runner.setY((byte) ((androidx.preference.SeekBarPreference) findPreference("prf_y_size")).getValue());
				break;
			case "gcb_schulte_2_sequences":
				runner.setX((byte) 7);
				runner.setY((byte) 7);
				break;
			case "gcb_schulte_3_sequences":
				runner.setX((byte) 10);
				runner.setY((byte) 10);
				break;
			default:
				runner.setX((byte) 5);
				runner.setY((byte) 5);
		}
		// set hinted to runner
		runner.setHinted(((androidx.preference.SwitchPreference) findPreference(KEY_HINTED)).isChecked());

	}

	@Override
	public void onResume() {
		super.onResume();
		updatePrefScreen();
	}

	@Override
	public void onPause() {
		super.onPause();
		updatePrefScreen();
		ExerciseRunner.savePreferences(null);
	}

	@Override
	public boolean onPreferenceTreeClick(@NonNull Preference preference) {
		// only for Group Check Boxes:
		if (exerciseTypes.contains(preference)) {
			chosen = (androidx.preference.CheckBoxPreference) preference;
			for (Preference p : exerciseTypes) {
				((androidx.preference.CheckBoxPreference) p).setChecked(false);
			}
			chosen.setChecked(true);

			updatePrefScreen();
		}

		if (KEY_PRF_RATINGS.equals(preference.getKey())) {
			enableOptions(false);
		} else {
			enableOptions(true);
		}

		if (KEY_PRF_PROB_ENABLED.equals(preference.getKey())) {
			if (null == surfaceView) {
				surfaceView = drawerPreference.getSurfaceView();

				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setDither(true);
				paint.setColor(getRes().getColor(R.color.prob_color99));
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeJoin(Paint.Join.ROUND);
				paint.setStrokeCap(Paint.Cap.ROUND);
				paint.setStrokeWidth(24);

			}
			enableProbability(((SwitchPreference) preference).isChecked());
		}


		return super.onPreferenceTreeClick(preference);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void enableProbability(boolean action) {
		((Preference) findPreference(KEY_PRF_PROB_DRAWER)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_SURFACE)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setEnabled(action);

//		if (surfaceView == null) return; // just for safety

		if (action) {
//			surfaceView.setBackgroundColor(Color.GRAY);
			surfaceView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					int sizeX = surfaceView.getWidth();
					int sizeY = surfaceView.getHeight();
					int startX, startY, endX, endY, dX, dY;
					startX = startY = endX = endY = dX = dY = 0;
					int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						startX = (int) event.getX();
						startY = (int) event.getY();

						((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setValue((int) (10*(2F*startX/sizeX-1)));
						((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setValue((int) (10*(2F*startY/sizeY-1)));

						Canvas canvas = surfaceView.getHolder().lockCanvas();
//						canvas.drawPaint(paint);
						Log.d(TAG, "onTouch: " + startX + " and: " +startY);
						// clear:
//						canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
						canvas.drawColor(Color.GRAY);
						// draw:
						canvas.drawPoint(startX, startY, paint);
						surfaceView.getHolder().unlockCanvasAndPost(canvas);
						canvas = null;

//						surfaceView.performClick();

						return true;
					}

/*					if (event.getAction() == MotionEvent.ACTION_UP) {
						endX = (int) event.getX();
						endY = (int) event.getY();
						dX = Math.abs(endX - startX);
						dY = Math.abs(endY - startY);


						if (Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)) <= touchSlop) {
							// Your on click logic here...
							return true;
						}
					}*/
					return false;
				}
			});


		} else {
			Canvas canvas = surfaceView.getHolder().lockCanvas();
			canvas.drawColor(Color.DKGRAY);
			surfaceView.getHolder().unlockCanvasAndPost(canvas);
			canvas = null;
			surfaceView = null;
		}

	}

	private void enableOptions(boolean action)
	{
//		((PreferenceCategory) findPreference(KEY_PRF_OPTIONS)).setVisible(false);

		((SwitchPreference) findPreference(KEY_HINTED)).setChecked(action);
		((SwitchPreference) findPreference(KEY_PRF_SHUFFLE)).setChecked(action);
		if (action) {
			((SeekBarPreference) findPreference(KEY_X_SIZE)).setValue(5);
			((SeekBarPreference) findPreference(KEY_Y_SIZE)).setValue(5);
			((DropDownPreference) findPreference(KEY_PRF_SYMBOLS)).setValue("N");
		}

		((PreferenceCategory) findPreference(KEY_PRF_PROBABILITIES)).setVisible(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_SURFACE)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setEnabled(action);
		if (action) {
			// do nothing
		} else {
			((SeekBarPreference) findPreference(KEY_PRF_PROB_SURFACE)).setValue(0);
			((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setValue(0);
			((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setValue(0);
		}

	}


	/**
	 * method provides fnc Group of Checkboxes directly in _preferences.xml -- layout
	 */
	private void initiateExerciseTypes(){
		ArrayList<Preference> list = getPreferenceList(getPreferenceScreen(), new ArrayList<Preference>());
		exerciseTypes.clear();
		for (Preference p : list) {
			// prefix "gcb_" of Preference means Group Check Box
			if (p instanceof androidx.preference.CheckBoxPreference && p.getKey().startsWith("gcb_")) {
				p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(@NonNull Preference preference) {
						chosen = (androidx.preference.CheckBoxPreference) preference;
						return false;
					}
				});
				exerciseTypes.add(p);
			}
		}
	}

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
}
