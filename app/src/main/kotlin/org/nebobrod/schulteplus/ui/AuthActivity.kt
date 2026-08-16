package org.nebobrod.schulteplus.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.launch
import org.nebobrod.schulteplus.data.UserHelper
import org.nebobrod.schulteplus.ui.auth.LoginScreen
import org.nebobrod.schulteplus.ui.auth.SignupScreen
import org.nebobrod.schulteplus.ui.auth.SplashScreen
import org.nebobrod.schulteplus.ui.theme.SchultePlusTheme
import org.nebobrod.schulteplus.auth.AuthSession

/**
 * Единая Compose-активность авторизации (B2): Splash → Login ↔ Signup → MainActivity.
 * Префилл email/name/password из intent-extras сохраняет контракт prf_user_delete.
 * Snackbar-хост (D-23): тосты SystemUI на Android 12+ могут не отрисоваться
 * (SystemUIToast → getBadgedIcon IOException после обновления APK).
 */
class AuthActivity : ComponentActivity() {

    private enum class Screen { SPLASH, LOGIN, SIGNUP }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefillEmail = intent.getStringExtra("email").orEmpty()
        val prefillName = intent.getStringExtra("name").orEmpty()
        val prefillPassword = intent.getStringExtra("password").orEmpty()

        setContent {
            SchultePlusTheme {
                var screen by rememberSaveable { mutableStateOf(Screen.SPLASH.name) }
                var email by rememberSaveable { mutableStateOf(prefillEmail) }
                var name by rememberSaveable { mutableStateOf(prefillName) }
                var password by rememberSaveable { mutableStateOf(prefillPassword) }

                // D-20: fullscreen — только сплэш; Login/Signup — со статус-баром
                DisposableEffect(screen) {
                    val controller = WindowInsetsControllerCompat(window, window.decorView)
                    if (screen == Screen.SPLASH.name) {
                        controller.hide(WindowInsetsCompat.Type.systemBars())
                        controller.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    } else {
                        controller.show(WindowInsetsCompat.Type.systemBars())
                    }
                    onDispose {}
                }

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val showText: (String) -> Unit = { text ->
                    scope.launch { snackbarHostState.showSnackbar(text) }
                }

                val goMain: (UserHelper?) -> Unit = { user -> AuthSession.runMainActivity(this, user) }

                Box(Modifier.fillMaxSize()) {
                    when (Screen.valueOf(screen)) {
                        Screen.SPLASH -> SplashScreen(
                            onSession = { user ->
                                if (user != null) goMain(user)
                                else screen = Screen.LOGIN.name
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
                            onMessage = showText
                        )
                        Screen.SIGNUP -> SignupScreen(
                            initialEmail = email,
                            initialName = name,
                            initialPassword = password,
                            onGoToLogin = { e, n, p ->
                                email = e; name = n; password = p
                                screen = Screen.LOGIN.name
                            },
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
