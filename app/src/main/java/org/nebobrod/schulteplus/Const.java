package org.nebobrod.schulteplus;

import androidx.appcompat.graphics.drawable.DrawableContainerCompat;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public interface Const {
	// set of exercise related constants
	public static final byte SEQ1_SINGLE = 1;
	public static final byte SEQ2_DOUBLE = 2;
	public static final byte SEQ2_RED = 21;
	public static final byte SEQ2_BLUE = 22;
	public static final byte SEQ4_QUARTER = 4;
	public static final byte SEQ4_RED = 41;
	public static final byte SEQ4_BLUE = 42;
	public static final byte SEQ4_YELLOW = 43;
	public static final byte SEQ4_GREEN = 44;

	// set of prf related constants
	public static final String KEY_USER_NAME = "prf_user_name";
	public static final String KEY_USER_EMAIL = "prf_user_email";
	public static final String KEY_POINTS = "prf_points"; // number of points earned (seconds + hits + prises)
	public static final String KEY_HOURS = "prf_hours"; // number of hours practicing
	public static final String KEY_PRF_LEVEL = "prf_level";
	public static final String KEY_PRF_CURRENT_LEVEL = "prf_current_level";
	public static final String KEY_TYPE_OF_EXERCISE = "prf_ex_type";
	public static final String KEY_X_SIZE = "prf_x_size";
	public static final String KEY_Y_SIZE = "prf_y_size";
	public static final String KEY_TS_UPDATED = "prf_ts_updated";
	public static final String KEY_HINTED = "prf_sw_hints";


}
