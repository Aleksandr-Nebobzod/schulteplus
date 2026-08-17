package org.nebobrod.schulteplus.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Обёртка Firebase Analytics — воронка стартового флоу (SP-03 Inc 3/6):
 * Splash → Onboarding → Login/Signup → Main. События заданы константами.
 */
object Analytics {

    const val AUTH_SPLASH_SHOWN = "auth_splash_shown"
    const val ONBOARDING_SHOWN = "onboarding_shown"
    const val ONBOARDING_EXERCISE_SELECTED = "onboarding_exercise_selected"
    const val ONBOARDING_DONE = "onboarding_done"
    const val DEMO_ENTERED = "demo_entered"

    fun log(context: Context, event: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle()
        params.forEach { (k, v) -> bundle.putString(k, v) }
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle)
    }

    fun onboardingShown(context: Context) = log(context, ONBOARDING_SHOWN)

    fun onboardingExerciseSelected(context: Context, exTypeId: String) =
        log(context, ONBOARDING_EXERCISE_SELECTED, mapOf("ex_type" to exTypeId))

    fun onboardingDone(context: Context, method: String) =
        log(context, ONBOARDING_DONE, mapOf("method" to method))

    fun demoEntered(context: Context) = log(context, DEMO_ENTERED)
}
