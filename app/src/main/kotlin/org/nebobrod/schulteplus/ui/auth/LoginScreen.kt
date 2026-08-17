package org.nebobrod.schulteplus.ui.auth

import android.app.Activity
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.auth.AuthSession
import org.nebobrod.schulteplus.auth.FirebaseAuthService
import org.nebobrod.schulteplus.common.Const
import org.nebobrod.schulteplus.data.UserHelper

/**
 * Вход (B2 + SP-03 Inc 2/4): email/пароль в одной карточке (FieldsCard), инлайн-ошибки,
 * show/hide, «Забыли пароль?» → диалог сброса (B2.1), snackbar «подтвердите почту»
 * с resend для непроверенных аккаунтов, Google, демо-лок support@attplus.in, переход в Signup.
 */
@Composable
fun LoginScreen(
    initialEmail: String,
    initialName: String,
    initialPassword: String,
    onGoToSignup: (email: String, name: String, password: String) -> Unit,
    onMain: (UserHelper?) -> Unit,
    onMessage: (text: String) -> Unit,
    onMessageAction: suspend (text: String, actionLabel: String, onAction: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var password by rememberSaveable { mutableStateOf(initialPassword) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var demoLocked by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    // B2.1: диалог сброса пароля
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var resetEmail by rememberSaveable { mutableStateOf("") }
    var resetError by remember { mutableStateOf(false) }
    var resetBusy by remember { mutableStateOf(false) }

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
            scope.launch {
                busy = true
                val ok = FirebaseAuthService.signInWithGoogleIdToken(idToken)
                val fbUser = FirebaseAuth.getInstance().currentUser
                busy = false
                if (ok && fbUser != null) {
                    AuthSession.loginWithUpdate(context as Activity, fbUser, "google_sign_in", "new",
                        { onMain(it) }, {}, onMessage = onMessage)
                } else {
                    onMessage(context.getString(R.string.msg_user_login_failed))
                }
            }
        }
    }

    fun submit() {
        if (demoLocked || busy) return
        // демо: настоящий аккаунт support@attplus.in — лок полей и вход (паритет lockForDemo)
        if ("support@attplus.in".equals(email, ignoreCase = true)) demoLocked = true
        emailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        passwordError = !Const.PASSWORD_REG_EXP.toRegex().matches(password)
        if (emailError || passwordError) return
        scope.launch {
            busy = true
            val ok = FirebaseAuthService.signInEmail(email, password)
            busy = false
            if (ok) {
                val fbUser = FirebaseAuth.getInstance().currentUser
                if (fbUser != null) {
                    // B2.1: почта не подтверждена — snackbar с resend; ждём выбора до входа
                    if (!fbUser.isEmailVerified) {
                        onMessageAction(
                            context.getString(R.string.msg_user_unverified),
                            context.getString(R.string.lbl_resend_verification_email)
                        ) {
                            scope.launch {
                                val resent = FirebaseAuthService.resendVerificationEmail()
                                onMessage(context.getString(if (resent) R.string.msg_user_verif_sent
                                    else R.string.msg_user_verif_not_sent))
                            }
                        }
                    }
                    AuthSession.loginWithUpdate(context as Activity, fbUser, password, "new",
                        { onMain(it) }, {}, onMessage = onMessage)
                }
            } else {
                onMessage(context.getString(R.string.msg_user_login_failed))
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
            Spacer(Modifier.height(36.dp))
            AppIcon(size = 64.dp)
            Spacer(Modifier.height(18.dp))
            Text(
                context.getString(R.string.title_login),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(20.dp))
            // правка 3.1: все поля — в одной карточке с полупрозрачной подложкой
            FieldsCard {
                AuthField(
                    label = context.getString(R.string.hint_email),
                    value = email,
                    onValueChange = { email = it },
                    enabled = !demoLocked && !busy,
                    isError = emailError,
                    supportingText = if (emailError) context.getString(R.string.msg_email_pattern) else null,
                    keyboardType = KeyboardType.Email,
                )
                AuthField(
                    label = context.getString(R.string.hint_pass),
                    value = password,
                    onValueChange = { password = it },
                    enabled = !demoLocked && !busy,
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
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = {
                        resetEmail = email
                        showResetDialog = true
                    },
                    enabled = !busy
                ) {
                    Text(context.getString(R.string.lbl_forgot_password))
                }
            }
            Spacer(Modifier.height(4.dp))
            AuthButton(text = context.getString(R.string.lbl_go_on), busy = busy) { submit() }
            Spacer(Modifier.height(16.dp))
            OrDivider(context.getString(R.string.lbl_or))
            Spacer(Modifier.height(16.dp))
            GoogleButton(
                text = context.getString(R.string.lbl_google_log_in),
                onClick = { googleLauncher.launch(googleSignInClient.signInIntent) }
            )
            Spacer(Modifier.height(24.dp))
            BottomLink(context.getString(R.string.str_login_go_off)) { onGoToSignup(email, initialName, password) }
            Spacer(Modifier.height(28.dp))
        }
    }

    // B2.1: диалог сброса пароля — email → sendPasswordResetEmail → snackbar результата
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { if (!resetBusy) showResetDialog = false },
            title = { Text(context.getString(R.string.lbl_forgot_password)) },
            text = {
                Column {
                    Text(
                        context.getString(R.string.msg_reset_password_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text(context.getString(R.string.hint_email)) },
                        enabled = !resetBusy,
                        isError = resetError,
                        supportingText = if (resetError) {
                            { Text(context.getString(R.string.msg_email_pattern)) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !resetBusy,
                    onClick = {
                        resetError = !Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()
                        if (resetError) return@TextButton
                        scope.launch {
                            resetBusy = true
                            val ok = FirebaseAuthService.sendPasswordResetEmail(resetEmail)
                            resetBusy = false
                            showResetDialog = false
                            onMessage(context.getString(if (ok) R.string.msg_password_reset_email_sent
                                else R.string.msg_password_reset_email_failed))
                        }
                    }
                ) { Text(context.getString(R.string.lbl_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }, enabled = !resetBusy) {
                    Text(context.getString(R.string.lbl_cancel))
                }
            }
        )
    }
}
