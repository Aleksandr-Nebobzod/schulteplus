package org.nebobrod.schulteplus.ui.auth

import android.app.Activity
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.Utils
import org.nebobrod.schulteplus.analytics.Analytics
import org.nebobrod.schulteplus.auth.AuthSession
import org.nebobrod.schulteplus.auth.FirebaseAuthService
import org.nebobrod.schulteplus.common.AppNetwork
import org.nebobrod.schulteplus.common.Const
import org.nebobrod.schulteplus.data.UserHelper

/**
 * Регистрация (B2 + SP-03 Inc 2): имя/email/пароль в одной карточке (FieldsCard),
 * инлайн-ошибки, чекбокс согласия с одним пояснением и ссылками (политика/соглашение),
 * Google, переход в Login, continue-unregistered → демо-префилл. Фон — bg_login_03.
 */
@Composable
fun SignupScreen(
    initialEmail: String,
    initialName: String,
    initialPassword: String,
    onGoToLogin: (email: String, name: String, password: String) -> Unit,
    onMain: (UserHelper?) -> Unit,
    onMessage: (text: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf(initialName) }
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var password by rememberSaveable { mutableStateOf(initialPassword) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var agreed by rememberSaveable { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val googleSignInClient = remember {
        val clientId = context.getString(
            context.resources.getIdentifier("default_web_client_id", "string", context.packageName))
        GoogleSignIn.getClient(context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build())
    }
    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val account = try {
            GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)
        } catch (e: ApiException) {
            null
        }
        val idToken = account?.idToken
        if (idToken != null) {
            // Inc 6: без сети вход невозможен — сообщение, форма остаётся доступной
            if (!AppNetwork.isConnected(context)) {
                onMessage(context.getString(R.string.msg_user_network_failed))
                return@rememberLauncherForActivityResult
            }
            Analytics.authSignupStarted(context, "google")
            scope.launch {
                busy = true
                val ok = FirebaseAuthService.signInWithGoogleIdToken(idToken)
                val fbUser = FirebaseAuth.getInstance().currentUser
                busy = false
                if (ok && fbUser != null) {
                    Analytics.authSignupSuccess(context, "google")
                    val fallbackName = fbUser.displayName ?: "new"
                    AuthSession.loginWithUpdate(context as Activity, fbUser, "google_sign_in", fallbackName,
                        { onMain(it) }, {}, onMessage = onMessage)
                } else {
                    Analytics.authSignupFailure(context, "google")
                    onMessage(context.getString(R.string.msg_user_signed_up_failed))
                }
            }
        }
    }

    fun submit() {
        if (busy) return
        if (!agreed) {
            onMessage(context.getString(R.string.hint_signup_agreed_title))
            onMessage(context.getString(R.string.hint_signup_agreed_desc))
            return
        }
        nameError = !Const.NAME_REG_EXP.toRegex().matches(name)
        emailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        passwordError = !Const.PASSWORD_REG_EXP.toRegex().matches(password)
        if (nameError || emailError || passwordError) return
        // Inc 6: без сети регистрация невозможна — сообщение, форма остаётся доступной
        if (!AppNetwork.isConnected(context)) {
            onMessage(context.getString(R.string.msg_user_network_failed))
            return
        }
        Analytics.authSignupStarted(context, "email")
        scope.launch {
            busy = true
            val ok = FirebaseAuthService.signUpEmail(email, password, name)
            busy = false
            if (ok) {
                val fbUser = FirebaseAuth.getInstance().currentUser
                if (fbUser != null) {
                    Analytics.authSignupSuccess(context, "email")
                    val userHelper = AuthSession.createUserHelper(
                        context as Activity, fbUser, name, email, password, onMessage = onMessage)
                    onMain(userHelper)
                }
            } else {
                Analytics.authSignupFailure(context, "email")
                onMessage(context.getString(R.string.msg_user_signed_up_failed))
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ScreenBackground()
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            AppIcon(size = 56.dp)
            Spacer(Modifier.height(14.dp))
            Text(
                context.getString(R.string.title_signup),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(20.dp))
            // правка 3.1: все поля — в одной карточке с полупрозрачной подложкой
            FieldsCard {
                AuthField(
                    label = context.getString(R.string.hint_login),
                    value = name,
                    onValueChange = { name = it },
                    enabled = !busy,
                    isError = nameError,
                    supportingText = if (nameError) context.getString(R.string.msg_username_wrong) else null,
                )
                AuthField(
                    label = context.getString(R.string.hint_email),
                    value = email,
                    onValueChange = { email = it },
                    enabled = !busy,
                    isError = emailError,
                    supportingText = if (emailError) context.getString(R.string.msg_email_pattern) else null,
                    keyboardType = KeyboardType.Email,
                )
                AuthField(
                    label = context.getString(R.string.hint_pass),
                    value = password,
                    onValueChange = { password = it },
                    enabled = !busy,
                    isError = passwordError,
                    supportingText = if (passwordError) context.getString(R.string.msg_password_rules) else null,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailing = {
                        PasswordTrailing(shown = showPassword,
                            show = context.getString(R.string.lbl_show),
                            hide = context.getString(R.string.lbl_hide)) { showPassword = !showPassword }
                    }
                )
            }
            Spacer(Modifier.height(12.dp))
            // согласие: один чекбокс + одно пояснение со ссылками (политика/соглашение)
            val linkColor = MaterialTheme.colorScheme.primary
            val consentText = buildAnnotatedString {
                append(context.getString(R.string.signup_consent_prefix))
                withStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Medium)) {
                    append(context.getString(R.string.signup_consent_privacy))
                }
                append(context.getString(R.string.signup_consent_and))
                withStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Medium)) {
                    append(context.getString(R.string.signup_consent_terms))
                }
                append(context.getString(R.string.signup_consent_suffix))
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Checkbox(checked = agreed, onCheckedChange = { agreed = it }, enabled = !busy)
                    Column(Modifier.padding(top = 10.dp)) {
                        Text(
                            consentText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.clickable {
                                Utils.displayHtmlAlertDialog(context, R.string.str_about_user_agreement_html_source)
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            AuthButton(text = context.getString(R.string.lbl_go_on), busy = busy) { submit() }
            Spacer(Modifier.height(14.dp))
            GoogleButton(
                text = context.getString(R.string.lbl_google_log_in),
                onClick = { googleLauncher.launch(googleSignInClient.signInIntent) }
            )
            Spacer(Modifier.height(20.dp))
            BottomLink(context.getString(R.string.str_signup_go_off)) { onGoToLogin(email, name, password) }
            Spacer(Modifier.height(10.dp))
            Text(
                context.getString(R.string.lbl_continue_unregistered),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onGoToLogin("support@attplus.in", "", "support") }
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}
