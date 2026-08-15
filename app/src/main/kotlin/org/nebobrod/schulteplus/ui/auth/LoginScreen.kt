package org.nebobrod.schulteplus.ui.auth

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
 * Вход на Compose (B2): email/пароль, Google (GoogleSignInClient 16.0.0),
 * демо-лок support@attplus.in, переход в Signup.
 */
@Composable
fun LoginScreen(
    initialEmail: String,
    initialName: String,
    initialPassword: String,
    onGoToSignup: (email: String, name: String, password: String) -> Unit,
    onMain: (UserHelper?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var password by rememberSaveable { mutableStateOf(initialPassword) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var demoLocked by remember { mutableStateOf(false) }

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
                        { onMain(it) }, {})
                } else {
                    Toast.makeText(context, R.string.msg_user_login_failed, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun submit() {
        if (demoLocked || busy) return
        // демо: настоящий аккаунт support@attplus.in — лок полей и вход (паритет lockForDemo)
        if ("support@attplus.in".equals(email, ignoreCase = true)) demoLocked = true
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, R.string.msg_username_wrong, Toast.LENGTH_LONG).show()
            return
        }
        if (!Const.PASSWORD_REG_EXP.toRegex().matches(password)) {
            Toast.makeText(context, R.string.msg_password_rules, Toast.LENGTH_LONG).show()
            return
        }
        scope.launch {
            busy = true
            val ok = FirebaseAuthService.signInEmail(email, password)
            busy = false
            if (ok) {
                val fbUser = FirebaseAuth.getInstance().currentUser
                if (fbUser != null) {
                    AuthSession.loginWithUpdate(context as Activity, fbUser, password, "new",
                        { onMain(it) }, {})
                }
            } else {
                Toast.makeText(context, R.string.msg_user_login_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(context.getString(R.string.title_login), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            enabled = !demoLocked && !busy,
            label = { Text(context.getString(R.string.hint_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            enabled = !demoLocked && !busy,
            label = { Text(context.getString(R.string.hint_pass)) },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(context.getString(R.string.lbl_show_password))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = { submit() }, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
            Text(context.getString(R.string.lbl_go_on))
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(context.getString(R.string.lbl_google_log_in))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            context.getString(R.string.str_login_go_off),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable { onGoToSignup(email, initialName, password) }
        )
        if (busy) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
