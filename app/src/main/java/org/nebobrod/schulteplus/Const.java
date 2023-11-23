package org.nebobrod.schulteplus;

import androidx.appcompat.graphics.drawable.DrawableContainerCompat;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public interface Const {
	// set of exercise related constants
	public static final String NAME_REG_EXP = "^[a-z][[a-z]![0-9]]{3,14}$";

	// set of exercise related constants
	public static final byte SEQ1_SINGLE = 1;
	public static final byte SEQ2_DOUBLE = 2;
	public static final byte 	SEQ2_RED = 21;
	public static final byte 	SEQ2_BLUE = 22;
	public static final byte SEQ4_QUARTER = 4;
	public static final byte 	SEQ4_RED = 41;
	public static final byte 	SEQ4_BLUE = 42;
	public static final byte 	SEQ4_YELLOW = 43;
	public static final byte 	SEQ4_GREEN = 44;

	// set of prf related constants
	public static final String KEY_USER_NAME = "prf_user_name";
	public static final String KEY_USER_EMAIL = "prf_user_email";
	public static final String KEY_POINTS = "prf_points"; // number of points earned (seconds + hits + prises)
	public static final String KEY_HOURS = "prf_hours"; // number of hours practicing
	public static final String KEY_PRF_LEVEL = "prf_level";
	public static final String KEY_PRF_CURRENT_LEVEL = "prf_current_level";
	public static final String KEY_TYPE_OF_EXERCISE = "prf_ex_type";
	public static final String KEY_TS_UPDATED = "prf_ts_updated";

	public static final String KEY_PRF_RATINGS = "prf_sw_ratings";
	public static final String KEY_PRF_OPTIONS = "prf_cat_options";
	public static final String KEY_HINTED = "prf_sw_hints";
	public static final String KEY_PRF_SHUFFLE = "prf_sw_shuffle";
	public static final String KEY_X_SIZE = "prf_x_size";
	public static final String KEY_Y_SIZE = "prf_y_size";
	public static final String KEY_PRF_SYMBOLS = "prf_symbol_type";
	public static final String KEY_PRF_FONT_SCALE = "prf_font_scale";
	public static final String KEY_HAPTIC = "prf_vibration";
	public static final String KEY_SOUND = "prf_sound";

	public static final String KEY_PRF_PROBABILITIES = "prf_cat_prob";
	public static final String KEY_PRF_PROB_ENABLED = "prf_prob_enabled";
	public static final String KEY_PRF_PROB_DRAWER = "prf_prob_drawer";
	public static final String KEY_PRF_PROB_ZERO = "prf_prob_zero"; // can make "white spaces"
	public static final String KEY_PRF_PROB_X = "prf_prob_x"; // value -10:10 should be divided by 10
	public static final String KEY_PRF_PROB_Y = "prf_prob_y"; // value -10;10 should be divided by 10
	public static final String KEY_PRF_PROB_SURFACE = "prf_prob_surface"; // value 4:10 should be divided by 10

	public static final String KEY_PRF_EX_S1 = "gcb_schulte_1_sequence";
	public static final String KEY_PRF_EX_S2 = "gcb_schulte_2_sequences";
	public static final String KEY_PRF_EX_S3 = "gcb_schulte_3_sequences";
	public static final String KEY_PRF_EX_S4 = "gcb_schulte_4_mishmash";


}
