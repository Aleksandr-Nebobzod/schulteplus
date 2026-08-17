package org.nebobrod.schulteplus.ui.auth

import android.content.Context
import org.nebobrod.schulteplus.common.Const

/**
 * Глобальный флаг онбординга (D-19): читается ДО входа, когда uid ещё неизвестен,
 * поэтому живёт в отдельном файле prf_global, а не в per-user prefs (ExerciseRunner).
 */
object OnboardingPrefs {

    fun isShown(context: Context): Boolean =
        context.getSharedPreferences(Const.GLOBAL_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(Const.KEY_ONBOARDING_SHOWN, false)

    fun markShown(context: Context) {
        context.getSharedPreferences(Const.GLOBAL_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(Const.KEY_ONBOARDING_SHOWN, true).apply()
    }
}
