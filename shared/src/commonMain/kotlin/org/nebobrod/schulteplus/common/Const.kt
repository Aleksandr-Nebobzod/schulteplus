package org.nebobrod.schulteplus.common

/**
 * Migrated from Java Const.java (B1). Keep package org.nebobrod.schulteplus.common
 * so Java code keeps `Const.*` access without import changes.
 */
interface Const {

	/** Enumerates types of Achievements*/
	enum class AchievementFlags {
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

	companion object {
		// set of user related constants
		const val NAME_REG_EXP = "^[A-Za-z][[A-Za-z]![0-9]]{3,14}$"
		const val PASSWORD_REG_EXP = "^(?=.*[A-Za-z])[A-Za-z\\d~!@#$%^*()+=]{6,15}$"

		const val ANIM_STEP_MILLIS = 200L

		// set of Showing Intro
		const val SHOWN_00_MAIN = 0b1 shl 0
		const val SHOWN_01_BASE = 0b1 shl 1
		const val SHOWN_02_SCHULTE = 0b1 shl 2
		const val SHOWN_03_STATA = 0b1 shl 3
		const val SHOWN_04_NEWS = 0b1 shl 4
		const val SHOWN_05_BASE_SPACE = 0b1 shl 5
		const val SHOWN_06_SCHULTE_SPACE = 0b1 shl 6
		const val SHOWN_ALL = 0b1101111 // no news yet

		// set of exercise related constants
		const val SEQ1_SINGLE: Byte = 1
		const val SEQ2_DOUBLE: Byte = 2
		const val SEQ2_RED: Byte = 21
		const val SEQ2_BLUE: Byte = 22
		const val SEQ4_QUARTER: Byte = 4
		const val SEQ4_RED: Byte = 41
		const val SEQ4_BLUE: Byte = 42
		const val SEQ4_YELLOW: Byte = 43
		const val SEQ4_GREEN: Byte = 44
		const val QUERY_COMMON_LIMIT = 99L
		const val AVERAGE_IDLE_LIMIT = 300L // allowed AFK in seconds

		// set of prf related constants
		const val KEY_USER_APP_KEY = "prf_user_app_key"
		const val KEY_USER_NAME = "prf_user_name"
		const val KEY_USER_EMAIL = "prf_user_email"
		const val KEY_PRF_SHARED_DATA = "prf_title_data"
		const val KEY_PSYCOINS = "prf_psycoins" // sum of psycoins earned (ExResult.getPsycoins)
		const val KEY_SECONDS = "prf_points" // number of seconds earned (<3600)
		const val KEY_HOURS = "prf_hours" // sum of hours practicing
		const val KEY_DAYS = "prf_days" // number of days with any exercise
		const val KEY_PRF_LEVEL = "prf_level"
		const val KEY_PRF_CURRENT_LEVEL = "prf_current_level"
		const val KEY_PFR_EXERCISE_SPACE = "prf_ex_space"
		const val KEY_TYPE_OF_EXERCISE = "prf_ex_type"
		const val KEY_TS_UPDATED = "prf_ts_updated"

		/** Spaces of bottom menu KEY_PFR_EXERCISE_SPACE */
		const val KEY_SPACE_01_SCHULTE = "gcb_space_schulte"
		const val KEY_SPACE_02_BASICS = "gcb_space_basics"
		const val KEY_SPACE_03_SSSR = "gcb_space_sssr"
		const val KEY_SPACE_04_SCHULTE_PARENTS = "gcb_space_schulte_parents"
		const val KEY_SPACE_05_WORD_FLOWS = "gcb_space_word_flow"

		/** PRFs for the schulte space */
		// gcb means Group Check Box
		const val KEY_PRF_EX_S0 = "gcb_sch"
		const val KEY_PRF_EX_S1 = "gcb_schulte_1_sequence"
		const val KEY_PRF_EX_S2 = "gcb_schulte_2_sequences"
		const val KEY_PRF_EX_S3 = "gcb_schulte_3_sequences"
		const val KEY_PRF_EX_S4 = "gcb_schulte_4_mishmash"

		const val KEY_PRF_RATINGS = "prf_sw_ratings"
		const val KEY_PRF_OPTIONS = "prf_cat_options"
		const val KEY_PRF_HINTED = "prf_sw_hints"
		const val KEY_PRF_COUNT_DOWN = "prf_sw_count_down"
		const val KEY_PRF_SHUFFLE = "prf_sw_shuffle"
		const val KEY_X_SIZE = "prf_x_size"
		const val KEY_Y_SIZE = "prf_y_size"
		const val KEY_PRF_SQUARED = "prf_squared"
		const val KEY_PRF_SYMBOLS = "prf_symbol_type"
		const val KEY_SYMBOL_TYPE_NUMBER = "number"
		const val KEY_SYMBOL_TYPE_NUMBER_ROME = "number_rome"
		const val KEY_SYMBOL_TYPE_LETTER_LATIN = "letter_latin"
		const val KEY_SYMBOL_TYPE_LETTER_CYRILLIC = "letter_cyrillic"
		const val KEY_SYMBOL_TYPE_LETTER_DEVANAGARI = "letter_devanagari"
		const val KEY_SYMBOL_TYPE_COLOR_RED = "color_red"
		const val KEY_SYMBOL_TYPE_COLOR_BLUE = "color_blue"
		const val KEY_PRF_FONT_SCALE = "prf_font_scale"
		const val KEY_PRF_HAPTIC = "prf_vibration"
		const val KEY_PRF_SOUND = "prf_sound"
		const val KEY_PRF_ONLINE = "prf_online"
		const val KEY_PRF_SHOW_INTRO = "prf_show_intro"
		const val KEY_PRF_SHOWN_INTROS = "prf_shown_intros"

		const val KEY_PRF_PROBABILITIES = "prf_cat_prob"
		const val KEY_PRF_PROB_ENABLED = "prf_prob_enabled"
		const val KEY_PRF_PROB_DRAWER = "prf_prob_drawer"
		const val KEY_PRF_PROB_ZERO = "prf_prob_zero" // can make "white spaces"
		const val KEY_PRF_PROB_X = "prf_prob_x" // value -10:10 should be divided by 10
		const val KEY_PRF_PROB_Y = "prf_prob_y" // value -10;10 should be divided by 10
		const val KEY_PRF_PROB_SURFACE = "prf_prob_surface" // value 4:10 should be divided by 10

		/** PRFs for the basic space */
		const val KEY_PRF_EX_B0 = "gcb_bas"

		const val KEY_PRF_EX_B9 = "gcb_bas_necker_cube"
		const val KEY_PRF_EX_BA = "gcb_bas_necker_cylinder"
		const val KEY_PRF_EX_BB = "gcb_bas_necker_ball"
		const val KEY_PRF_EX_BC = "gcb_bas_penrose_triangle"

		const val KEY_PRF_EX_B1 = "gcb_bas_dot"
		const val KEY_PRF_EX_B2 = "gcb_bas_dbl_dot"
		const val KEY_PRF_EX_B3 = "gcb_bas_pyramidot"
		const val KEY_PRF_EX_B4 = "gcb_bas_circles_rb"
		const val KEY_PRF_EX_B5 = "gcb_bas_circles_rb_lined"
		const val KEY_PRF_EX_B6 = "gcb_bas_khao_manee"
		const val KEY_PRF_EX_B7 = "gcb_bas_circles_rb_crossed"
		const val KEY_PRF_EX_B8 = "gcb_bas_squares_colored_crossed"

		const val KEY_PRF_EX_BD = "gcb_bas_dancing_girl"
		const val KEY_PRF_EX_BE = "gcb_bas_dancing_cat"

		/** PRFs for the sssr space */
		const val KEY_PRF_EX_R0 = "gcb_sss"
		const val KEY_PRF_EX_R1 = "gcb_sssr_main"
		const val KEY_PRF_EX_R2 = "gcb_sssr_intercept01"
		const val KEY_PRF_EX_R3 = "gcb_sssr_intercept02"
		const val KEY_PRF_EX_R4 = "gcb_sssr_intercept03"

		const val KEY_PRF_EX_R0_JOB = "prf_sssr_job"
		const val KEY_PRF_EX_R0_PHYSICAL = "prf_sssr_physical"
		const val KEY_PRF_EX_R0_LEISURE = "prf_sssr_leisure"
		const val KEY_PRF_EX_R0_FAMILY = "prf_sssr_family"
		const val KEY_PRF_EX_R0_FRIENDS = "prf_sssr_friends"
		const val KEY_PRF_EX_R0_CHORES = "prf_sssr_chores"
		const val KEY_PRF_EX_R0_SLEEP = "prf_sssr_sleep"
		const val KEY_PRF_EX_R0_SSSR = "prf_sssr_sssr"

		/**
		 * Shared constants for data classes [org.nebobrod.schulteplus.data.ExResult]
		 */
		const val LAYOUT_HEADER_FLAG = "H"
		const val LAYOUT_GROUP_FLAG = "G"
		/** The last update of data */
		const val TIMESTAMP_FIELD_NAME = "timeStamp"
	}
}
