package org.nebobrod.schulteplus.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import org.nebobrod.schulteplus.data.UserHelper
import org.nebobrod.schulteplus.ui.auth.LoginScreen
import org.nebobrod.schulteplus.ui.auth.SignupScreen
import org.nebobrod.schulteplus.ui.auth.SplashScreen
import org.nebobrod.schulteplus.ui.theme.SchultePlusTheme
import org.nebobrod.schulteplus.auth.AuthSession

/**
 * Единая Compose-активность авторизации (B2): Splash → Login ↔ Signup → MainActivity.
 * Префилл email/name/password из intent-extras сохраняет контракт prf_user_delete.
 */
class AuthActivity : ComponentActivity() {

    private enum class Screen { SPLASH, LOGIN, SIGNUP }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefillEmail = intent.getStringExtra("email").orEmpty()
        val prefillName = intent.getStringExtra("name").orEmpty()
        val prefillPassword = intent.getStringExtra("password").orEmpty()

        setContent {
            SchultePlusTheme {
                var screen by rememberSaveable { mutableStateOf(Screen.SPLASH.name) }
                var email by rememberSaveable { mutableStateOf(prefillEmail) }
                var name by rememberSaveable { mutableStateOf(prefillName) }
                var password by rememberSaveable { mutableStateOf(prefillPassword) }

                val goMain: (UserHelper?) -> Unit = { user -> AuthSession.runMainActivity(this, user) }

                when (Screen.valueOf(screen)) {
                    Screen.SPLASH -> SplashScreen(
                        onLogin = { screen = Screen.LOGIN.name },
                        onMain = goMain
                    )
                    Screen.LOGIN -> LoginScreen(
                        initialEmail = email,
                        initialName = name,
                        initialPassword = password,
                        onGoToSignup = { e, n, p ->
                            email = e; name = n; password = p
                            screen = Screen.SIGNUP.name
                        },
                        onMain = goMain
                    )
                    Screen.SIGNUP -> SignupScreen(
                        initialEmail = email,
                        initialName = name,
                        initialPassword = password,
                        onGoToLogin = { e, n, p ->
                            email = e; name = n; password = p
                            screen = Screen.LOGIN.name
                        },
                        onMain = goMain
                    )
                }
            }
        }
    }
}
