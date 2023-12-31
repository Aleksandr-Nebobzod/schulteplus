package org.nebobrod.schulteplus.ui.schultesettings;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Const.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
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
import org.nebobrod.schulteplus.STable;

import java.util.ArrayList;
import java.util.Objects;

public class SchulteSettings extends PreferenceFragmentCompat {
	// TODO: 28.09.2023 this and BasicSettings class are mostly same (think how to unify them)
	private static final String TAG = "SchulteSettings";
	ArrayList<Preference> exerciseTypeCheckBoxes = new ArrayList<>();
	androidx.preference.CheckBoxPreference chosen;
	private ExerciseRunner runner = ExerciseRunner.getInstance();
	private String[] exTypes;

	private DrawerPreference drawerPreference;
	private SurfaceView surfaceView;
	private Paint paint;




	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		getPreferenceManager().setSharedPreferencesName(ExerciseRunner.uid);
		getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

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

		surfaceView = drawerPreference.getSurfaceView();


		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		updatePrefScreen();
	}

	@Override
	public void onPause() {
		super.onPause();
//		updatePrefScreen();
		ExerciseRunner.savePreferences(null);
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
			enableOptions ( ! ((SwitchPreference)preference).isChecked()); //		Action is opposite of ratings
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

		if (KEY_PRF_PROB_SURFACE.equals(preference.getKey())) {
			double w = ((SeekBarPreference) preference).getValue() / 10D;
			boolean probZero = ((SwitchPreference) findPreference(KEY_PRF_PROB_ZERO)).isChecked();
			int shiftX10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_X))).getValue();
			int shiftY10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_Y))).getValue();
			drawProbabilities(shiftX10, shiftY10, w, probZero);

//			Toast.makeText(getContext(), "Surface" + w, Toast.LENGTH_SHORT).show();
		}

		return super.onPreferenceTreeClick(preference);
	}



	@SuppressLint("ClickableViewAccessibility")
	private void enableProbability(boolean action) {
		((Preference) findPreference(KEY_PRF_PROB_DRAWER)).setEnabled(action);
		((SwitchPreference) findPreference(KEY_PRF_PROB_ZERO)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_SURFACE)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setEnabled(action);
		((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setEnabled(action);

//		if (surfaceView == null) return; // just for safety

		if (action) {
//			surfaceView.setBackgroundColor(Color.GRAY);

			int shiftX10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_X))).getValue();
			int shiftY10 = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_Y))).getValue();
			boolean canBeZero = ((SwitchPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_ZERO))).isChecked();
			final double w = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_SURFACE))).getValue()/10D;
			drawProbabilities(shiftX10, shiftY10, w, canBeZero);

			surfaceView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {

					int startX, startY, shiftX10, shiftY10;
					double w = ((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_SURFACE))).getValue()/10D;
//					int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						// absolute and relative points of center
						startX = (int) event.getX(); shiftX10 = (int) (10*(2F*startX/surfaceView.getWidth()-1));
						startY = (int) event.getY(); shiftY10 = (int) (10*(2F*startY/surfaceView.getHeight()-1));

						((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_X))).setValue(shiftX10);
						((SeekBarPreference) Objects.requireNonNull(findPreference(KEY_PRF_PROB_Y))).setValue(shiftY10);


						drawProbabilities(shiftX10, shiftY10, w, false);


//						surfaceView.performClick();

						return true;
					}

					return false;
				}
			});


		} else {
			Canvas canvas = surfaceView.getHolder().lockCanvas();
			canvas.drawColor(Color.DKGRAY);
			surfaceView.getHolder().unlockCanvasAndPost(canvas);
			canvas = null;
//			surfaceView = null;
		}

	}

	private void drawProbabilities(int shiftX10, int shiftY10, double w, boolean canBeZero) {
		double zMin = 10;
		double zMax = -10;


		// Prepare array of row values
		for (int i = -10; i<=10; i++) {
			for (int j = -10; j<=10; j++) {
				double x,y,z;
				x = i / 10D; y = j / 10D;
				z = STable.camelSurface(x, y, .1*shiftX10, .1*shiftY10, w);
				System.out.printf("\t'%1.3f'",z);

				zMin = Math.min(z, zMin);
				zMax = Math.max(z, zMax);

			}
			System.out.println();
		}
		System.out.printf("\tmin '%1.3f', ",zMin);
		System.out.printf("\tmax '%1.3f'",zMax);
		System.out.println();

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
			Log.e(TAG, "drawProbabilities: " + e.getMessage());
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



	private void enableOptions(boolean action)
	{ //		Action is true if ratings off
		((PreferenceCategory) findPreference(KEY_PRF_OPTIONS)).setVisible(action);
		((PreferenceCategory) findPreference(KEY_PRF_OPTIONS)).setEnabled(action);

		// Set off if Disabled
/*		if (action) {

		} else {
			((SeekBarPreference) findPreference(KEY_X_SIZE)).setValue(5);
			((SeekBarPreference) findPreference(KEY_Y_SIZE)).setValue(5);
			((DropDownPreference) findPreference(KEY_PRF_SYMBOLS)).setValue("number");
		}*/

		((PreferenceCategory) findPreference(KEY_PRF_PROBABILITIES)).setVisible(action);
		((PreferenceCategory) findPreference(KEY_PRF_PROBABILITIES)).setEnabled(action);

//		((SeekBarPreference) findPreference(KEY_PRF_PROB_SURFACE)).setEnabled(action);
//		((SwitchPreference) findPreference(KEY_PRF_PROB_ZERO)).setEnabled(action);
//		((SeekBarPreference) findPreference(KEY_PRF_PROB_X)).setEnabled(action);
//		((SeekBarPreference) findPreference(KEY_PRF_PROB_Y)).setEnabled(action);
		if (action) {
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
		EditTextPreference exType = findPreference("prf_ex_type");

		// Find which checkbox of "group" is selected on the screen:
		for (Preference p: exerciseTypeCheckBoxes) {
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
		runner.setRatings(((SwitchPreference) findPreference(KEY_PRF_RATINGS)).isChecked());

		// set X & Y to runner
		switch (chosen.getKey()){
			case KEY_PRF_EX_S1:

				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setEnabled(true);
//				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setChecked(true);
				// check if is Ratings
				if (runner.isRatings()) {
					enableOptions(false);
					runner.setX((byte) 5);
					runner.setY((byte) 5);
				} else { // enable options PreferenceCategory for ease levels
					enableOptions(true);
					runner.setX((byte) ((androidx.preference.SeekBarPreference) findPreference(KEY_X_SIZE)).getValue());
					runner.setY((byte) ((androidx.preference.SeekBarPreference) findPreference(KEY_Y_SIZE)).getValue());
				}
				break;
			case KEY_PRF_EX_S2:
				runner.setX((byte) 7);
				runner.setY((byte) 7);
				// disable options PreferenceCategory for hard levels
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setChecked(true);
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setEnabled(false);
				enableOptions(false);
				break;
			case KEY_PRF_EX_S3:
				runner.setX((byte) 10);
				runner.setY((byte) 10);
				// disable options PreferenceCategory for hard levels
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setChecked(true);
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setEnabled(false);
				enableOptions(false);
				break;
			case KEY_PRF_EX_S4:
//				runner.setX((byte) 10);
//				runner.setY((byte) 10);
				// disable options PreferenceCategory for hard levels
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setChecked(true);
				((SwitchPreference) findPreference(KEY_PRF_RATINGS)).setEnabled(false);
				enableOptions(false);
				break;
			default:
				runner.setX((byte) 5);
				runner.setY((byte) 5);
				// enable options PreferenceCategory for ease levels
				enableOptions(true);
		}
		// set hinted to runner
//		runner.setHinted(((androidx.preference.SwitchPreference) findPreference(KEY_HINTED)).isChecked());

	}


	/**
	 * method provides functionality "Group of Checkboxes" directly in _preferences.xml -- layout
	 */
	private void initiateExerciseTypes(){
		ArrayList<Preference> list = getPreferenceList(getPreferenceScreen(), new ArrayList<Preference>());
		exerciseTypeCheckBoxes.clear();
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
				exerciseTypeCheckBoxes.add(p);
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
