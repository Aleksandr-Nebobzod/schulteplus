package org.nebobrod.schulteplus.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nebobrod.schulteplus.Utils
import org.nebobrod.schulteplus.analytics.Analytics
import org.nebobrod.schulteplus.auth.AuthSession
import org.nebobrod.schulteplus.auth.FirebaseAuthService
import org.nebobrod.schulteplus.data.DataOrmRepo
import org.nebobrod.schulteplus.data.UserHelper
import org.nebobrod.schulteplus.ui.auth.LoginScreen
import org.nebobrod.schulteplus.ui.auth.OnboardingPrefs
import org.nebobrod.schulteplus.ui.auth.OnboardingScreen
import org.nebobrod.schulteplus.ui.auth.SignupScreen
import org.nebobrod.schulteplus.ui.theme.SchultePlusTheme

/**
 * Единая Compose-активность авторизации (B2): Splash → Login ↔ Signup → MainActivity.
 * Префилл email/name/password из intent-extras сохраняет контракт prf_user_delete.
 * Snackbar-хост (D-23): тосты SystemUI на Android 12+ могут не отрисоваться
 * (SystemUIToast → getBadgedIcon IOException после обновления APK).
 */
class AuthActivity : ComponentActivity() {

    private enum class Screen { ONBOARDING, LOGIN, SIGNUP }

    override fun onCreate(savedInstanceState: Bundle?) {
        // D-29: системная заставка (core-splashscreen) удерживается, пока статус-экран
        // не завершит проверки (сеть/аккаунт) — onStatusReady
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val splashReady = mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !splashReady.value }
        val prefillEmail = intent.getStringExtra("email").orEmpty()
        val prefillName = intent.getStringExtra("name").orEmpty()
        val prefillPassword = intent.getStringExtra("password").orEmpty()

        setContent {
            SchultePlusTheme {
                // SP03-07: кастомный сплэш-экран убран — системная заставка (core-splashscreen)
                // держится (setKeepOnScreenCondition), пока не завершится проверка сессии
                var screen by rememberSaveable { mutableStateOf(Screen.LOGIN.name) }
                var email by rememberSaveable { mutableStateOf(prefillEmail) }
                var name by rememberSaveable { mutableStateOf(prefillName) }
                var password by rememberSaveable { mutableStateOf(prefillPassword) }

                LaunchedEffect(Unit) {
                    Analytics.authSplashShown(this@AuthActivity)
                    val user = withContext(Dispatchers.IO) { checkUserSession() }
                    if (user != null) {
                        AuthSession.runMainActivity(this@AuthActivity, user)
                    } else {
                        // SP03-18: снек-бар MainActivity «Зарегистрироваться?» → сразу экран SignUp
                        screen = when {
                            intent.getStringExtra("start_screen") == "signup" -> Screen.SIGNUP.name
                            OnboardingPrefs.isShown(this@AuthActivity) -> Screen.LOGIN.name
                            else -> Screen.ONBOARDING.name
                        }
                    }
                    splashReady.value = true
                }

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val showText: (String) -> Unit = { text ->
                    scope.launch { snackbarHostState.showSnackbar(text) }
                }
                // B2.1 (Inc 4): snackbar с действием (resend верификации). suspend — вызывающий
                // экран может дождаться выбора пользователя до навигации.
                val showTextAction: suspend (String, String, () -> Unit) -> Unit = { text, actionLabel, onAction ->
                    val result = snackbarHostState.showSnackbar(
                        message = text,
                        actionLabel = actionLabel,
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) onAction()
                }

                val goMain: (UserHelper?) -> Unit = { user -> AuthSession.runMainActivity(this, user) }

                Box(Modifier.fillMaxSize()) {
                    when (Screen.valueOf(screen)) {
                        Screen.ONBOARDING -> OnboardingScreen(
                            onSignup = {
                                OnboardingPrefs.markShown(this@AuthActivity)
                                Analytics.onboardingDone(this@AuthActivity, "signup")
                                screen = Screen.SIGNUP.name
                            },
                            // D-30: автостарт выбранной тренировки после слайда 3 (аноним оплатил)
                            onStartExercise = { anonUid, exTypeId ->
                                OnboardingPrefs.markShown(this@AuthActivity)
                                Analytics.onboardingDone(this@AuthActivity, "exercise")
                                Analytics.demoEntered(this@AuthActivity)
                                val anon = UserHelper(anonUid, "",
                                    OnboardingPrefs.anonName(this@AuthActivity), "",
                                    Utils.getDevId(), Utils.generateUak(), false)
                                // SP03-11: setUserHelper перезаписывает psycoins из UserHelper (0) —
                                // передаём текущий баланс анонима, чтобы кошелёк не обнулялся
                                anon.setPsyCoins(OnboardingPrefs.anonBalance(this@AuthActivity))
                                AuthSession.runMainActivity(this@AuthActivity, anon, startExercise = exTypeId)
                            }
                        )
                        Screen.LOGIN -> LoginScreen(
                            initialEmail = email,
                            initialName = name,
                            initialPassword = password,
                            onGoToSignup = { e, n, p ->
                                email = e; name = n; password = p
                                screen = Screen.SIGNUP.name
                            },
                            onMain = goMain,
                            onMessage = showText,
                            onMessageAction = showTextAction
                        )
                        Screen.SIGNUP -> SignupScreen(
                            initialEmail = email,
                            initialName = name,
                            initialPassword = password,
                            onGoToLogin = { e, n, p ->
                                email = e; name = n; password = p
                                screen = Screen.LOGIN.name
                            },
                            // SP03-02: «Продолжить без регистрации» → онбординг (демо-выбор),
                            // а не префилл служебной учётки в Login
                            onGoToOnboarding = { screen = Screen.ONBOARDING.name },
                            onMain = goMain,
                            onMessage = showText
                        )
                    }
                    SnackbarHost(
                        snackbarHostState,
                        Modifier.align(Alignment.BottomCenter).imePadding().navigationBarsPadding()
                    )
                }
            }
        }
    }
}

/** Проверка сессии: Firebase-пользователь + запись UserHelper из ORM (паритет checkUser). */
private suspend fun checkUserSession(): UserHelper? {
    val user = FirebaseAuth.getInstance().currentUser ?: return null
    return FirebaseAuthService.awaitResult(
        DataOrmRepo<UserHelper>(UserHelper::class.java).read("" + Utils.intStringHash(user.uid))
    )
}
