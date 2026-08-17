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
    const val AUTH_LOGIN_STARTED = "auth_login_started"
    const val AUTH_LOGIN_SUCCESS = "auth_login_success"
    const val AUTH_LOGIN_FAILURE = "auth_login_failure"
    const val AUTH_SIGNUP_STARTED = "auth_signup_started"
    const val AUTH_SIGNUP_SUCCESS = "auth_signup_success"
    const val AUTH_SIGNUP_FAILURE = "auth_signup_failure"
    const val AUTH_RESET_REQUESTED = "auth_reset_requested"
    const val AUTH_RESET_SUCCESS = "auth_reset_success"
    const val AUTH_RESET_FAILURE = "auth_reset_failure"
    const val AUTH_RESEND_REQUESTED = "auth_resend_requested"
    const val ONBOARDING_SHOWN = "onboarding_shown"
    const val ONBOARDING_EXERCISE_SELECTED = "onboarding_exercise_selected"
    const val ONBOARDING_DONE = "onboarding_done"
    const val DEMO_ENTERED = "demo_entered"

    fun log(context: Context, event: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle()
        params.forEach { (k, v) -> bundle.putString(k, v) }
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle)
    }

    fun authSplashShown(context: Context) = log(context, AUTH_SPLASH_SHOWN)

    fun authLoginStarted(context: Context, method: String) =
        log(context, AUTH_LOGIN_STARTED, mapOf("method" to method))

    fun authLoginSuccess(context: Context, method: String) =
        log(context, AUTH_LOGIN_SUCCESS, mapOf("method" to method))

    fun authLoginFailure(context: Context, method: String) =
        log(context, AUTH_LOGIN_FAILURE, mapOf("method" to method))

    fun authSignupStarted(context: Context, method: String) =
        log(context, AUTH_SIGNUP_STARTED, mapOf("method" to method))

    fun authSignupSuccess(context: Context, method: String) =
        log(context, AUTH_SIGNUP_SUCCESS, mapOf("method" to method))

    fun authSignupFailure(context: Context, method: String) =
        log(context, AUTH_SIGNUP_FAILURE, mapOf("method" to method))

    fun authResetRequested(context: Context) = log(context, AUTH_RESET_REQUESTED)

    fun authResetSuccess(context: Context) = log(context, AUTH_RESET_SUCCESS)

    fun authResetFailure(context: Context) = log(context, AUTH_RESET_FAILURE)

    fun authResendRequested(context: Context) = log(context, AUTH_RESEND_REQUESTED)

    fun onboardingShown(context: Context) = log(context, ONBOARDING_SHOWN)

    fun onboardingExerciseSelected(context: Context, exTypeId: String) =
        log(context, ONBOARDING_EXERCISE_SELECTED, mapOf("ex_type" to exTypeId))

    fun onboardingDone(context: Context, method: String) =
        log(context, ONBOARDING_DONE, mapOf("method" to method))

    fun demoEntered(context: Context) = log(context, DEMO_ENTERED)
}
