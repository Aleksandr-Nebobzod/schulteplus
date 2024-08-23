/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

public interface Const {
	// set of user related constants
	String NAME_REG_EXP = "^[A-Za-z][[A-Za-z]![0-9]]{3,14}$";
	String PASSWORD_REG_EXP = "^(?=.*[A-Za-z])[A-Za-z\\d~!@#$%^*()+=]{6,15}$";

	long ANIM_STEP_MILLIS = 200;

	// set of Showing Intro
	int SHOWN_00_MAIN = 	0b1<<0;
	int SHOWN_01_BASE = 	0b1<<1;
	int SHOWN_02_SCHULTE = 	0b1<<2;
	int SHOWN_03_STATA = 	0b1<<3;
	int SHOWN_04_NEWS =		0b1<<4;
	int SHOWN_05_BASE_SPACE =		0b1<<5;
	int SHOWN_06_SCHULTE_SPACE =	0b1<<6;
	int SHOWN_ALL =			0b1101111;			// no news yet

	// set of exercise related constants
	byte 	SEQ1_SINGLE = 1;
	byte 	SEQ2_DOUBLE = 2;
	byte 	SEQ2_RED = 21;
	byte 	SEQ2_BLUE = 22;
	byte 	SEQ4_QUARTER = 4;
	byte 	SEQ4_RED = 41;
	byte 	SEQ4_BLUE = 42;
	byte 	SEQ4_YELLOW = 43;
	byte 	SEQ4_GREEN = 44;
	long 	QUERY_COMMON_LIMIT = 99;
	long 	AVERAGE_IDLE_LIMIT = 300; 					// allowed  AFK in seconds
//1	2	3	4	5	6	7	8	9	10	11	12	13	14	15
	// set of prf related constants
	String KEY_USER_APP_KEY = "prf_user_app_key";
	String KEY_USER_NAME = "prf_user_name";
	String KEY_USER_EMAIL = "prf_user_email";
	String KEY_PRF_SHARED_DATA = "prf_title_data";
	String KEY_PSYCOINS = "prf_psycoins"; 				// sum of psycoins earned (ExResult.getPsycoins)
	String KEY_SECONDS = "prf_points"; 					// number of seconds earned (<3600)
	String KEY_HOURS = "prf_hours"; 					// sum of hours practicing
	String KEY_DAYS = "prf_days"; 						// number of days with any exercise
	String KEY_PRF_LEVEL = "prf_level";
	String KEY_PRF_CURRENT_LEVEL = "prf_current_level";
	String KEY_PFR_EXERCISE_SPACE = "prf_ex_space";
	String KEY_TYPE_OF_EXERCISE = "prf_ex_type";
	String KEY_TS_UPDATED = "prf_ts_updated";

	/** Spaces of bottom menu KEY_PFR_EXERCISE_SPACE */
	String KEY_SPACE_01_SCHULTE = "gcb_space_schulte";
	String KEY_SPACE_02_BASICS = "gcb_space_basics";
	String KEY_SPACE_03_SSSR = "gcb_space_sssr";
	String KEY_SPACE_04_SCHULTE_PARENTS = "gcb_space_schulte_parents";
	String KEY_SPACE_05_WORD_FLOWS = "gcb_space_word_flow";

	/** PRFs for the schulte space */
	// gcb means Group Check Box
	String KEY_PRF_EX_S0 = "gcb_sch";
	String KEY_PRF_EX_S1 = "gcb_schulte_1_sequence";
	String KEY_PRF_EX_S2 = "gcb_schulte_2_sequences";
	String KEY_PRF_EX_S3 = "gcb_schulte_3_sequences";
	String KEY_PRF_EX_S4 = "gcb_schulte_4_mishmash";

	String KEY_PRF_RATINGS = "prf_sw_ratings";
	String KEY_PRF_OPTIONS = "prf_cat_options";
	String KEY_PRF_HINTED = "prf_sw_hints";
	String KEY_PRF_COUNT_DOWN = "prf_sw_count_down";
	String KEY_PRF_SHUFFLE = "prf_sw_shuffle";
	String KEY_X_SIZE = "prf_x_size";
	String KEY_Y_SIZE = "prf_y_size";
	String KEY_PRF_SQUARED = "prf_squared";
	String KEY_PRF_SYMBOLS = "prf_symbol_type";
	String KEY_SYMBOL_TYPE_NUMBER = "number";
	String KEY_SYMBOL_TYPE_NUMBER_ROME = "number_rome";
	String KEY_SYMBOL_TYPE_LETTER_LATIN = "letter_latin";
	String KEY_SYMBOL_TYPE_LETTER_CYRILLIC = "letter_cyrillic";
	String KEY_SYMBOL_TYPE_LETTER_DEVANAGARI = "letter_devanagari";
	String KEY_SYMBOL_TYPE_COLOR_RED = "color_red";
	String KEY_SYMBOL_TYPE_COLOR_BLUE = "color_blue";
	String KEY_PRF_FONT_SCALE = "prf_font_scale";
	String KEY_PRF_HAPTIC = "prf_vibration";
	String KEY_PRF_SOUND = "prf_sound";
	String KEY_PRF_ONLINE = "prf_online";
	String KEY_PRF_SHOW_INTRO = "prf_show_intro";
	String KEY_PRF_SHOWN_INTROS = "prf_shown_intros";

	String KEY_PRF_PROBABILITIES = "prf_cat_prob";
	String KEY_PRF_PROB_ENABLED = "prf_prob_enabled";
	String KEY_PRF_PROB_DRAWER = "prf_prob_drawer";
	String KEY_PRF_PROB_ZERO = "prf_prob_zero"; 		// can make "white spaces"
	String KEY_PRF_PROB_X = "prf_prob_x"; 				// value -10:10 should be divided by 10
	String KEY_PRF_PROB_Y = "prf_prob_y"; 				// value -10;10 should be divided by 10
	String KEY_PRF_PROB_SURFACE = "prf_prob_surface"; 	// value 4:10 should be divided by 10


	/** PRFs for the basic space */
	String KEY_PRF_EX_B0 = "gcb_bas";
	String KEY_PRF_EX_B1 = "gcb_bas_dot";
	String KEY_PRF_EX_B2 = "gcb_bas_dbl_dot";
	String KEY_PRF_EX_B3 = "gcb_bas_pyramidot";
	String KEY_PRF_EX_B4 = "gcb_bas_circles_rb";
	String KEY_PRF_EX_B5 = "gcb_bas_circles_rb_lined";
	String KEY_PRF_EX_B6 = "gcb_bas_khao_manee";
	String KEY_PRF_EX_B7 = "gcb_bas_circles_rb_crossed";
	String KEY_PRF_EX_B8 = "gcb_bas_squares_colored_crossed";
	String KEY_PRF_EX_B9 = "gcb_bas_necker_cube";
	String KEY_PRF_EX_BA = "gcb_bas_necker_cylinder";
	String KEY_PRF_EX_BB = "gcb_bas_necker_ball";
	String KEY_PRF_EX_BC = "gcb_bas_penrose_triangle";
	String KEY_PRF_EX_BD = "gcb_bas_dancing_girl";
	String KEY_PRF_EX_BE = "gcb_bas_dancing_cat";

	/** PRFs for the sssr space */
	String KEY_PRF_EX_R0 = "gcb_sssr_main";
	String KEY_PRF_EX_R1 = "gcb_sssr_intercept01";
	String KEY_PRF_EX_R2 = "gcb_sssr_intercept02";
	String KEY_PRF_EX_R3 = "gcb_sssr_intercept03";

	/** Enumerates types of Achievements*/
	enum AchievementFlags {
		/** any of success exercise added to log */
		EXERCISE,
		/** an Hour of practicing added */
		HOURS,
		/** a Level added */
		LEVEL,
		/** a Day of continuous practicing added */
		SUSTAIN,
		/** added if time is less than the least */
		RECORD_PRIVATE, // this two are for the speed-type exercises
		/** added if time is less than the least world-wide */
		RECORD_PUBLIC,
		/** added when an aim is reached */
		AIM
	}

	/**
	 * Shared constants for data classes {@link org.nebobrod.schulteplus.data.ExResult}
	 */
	String LAYOUT_HEADER_FLAG = "H";
	String LAYOUT_GROUP_FLAG = "G";
	/** The last update of data */
	public static final String TIMESTAMP_FIELD_NAME = "timeStamp";

}
