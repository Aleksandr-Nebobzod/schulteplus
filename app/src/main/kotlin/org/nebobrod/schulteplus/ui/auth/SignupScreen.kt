package org.nebobrod.schulteplus.ui.auth

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.Utils
import org.nebobrod.schulteplus.auth.AuthSession
import org.nebobrod.schulteplus.auth.FirebaseAuthService
import org.nebobrod.schulteplus.common.Const
import org.nebobrod.schulteplus.data.UserHelper

/**
 * Регистрация на Compose (B2): имя/email/пароль + согласие, Google
 * (GoogleSignInClient вместо firebase-ui-auth), переход в Login,
 * continue-unregistered → демо-префилл.
 */
@Composable
fun SignupScreen(
    initialEmail: String,
    initialName: String,
    initialPassword: String,
    onGoToLogin: (email: String, name: String, password: String) -> Unit,
    onMain: (UserHelper?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf(initialName) }
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var password by rememberSaveable { mutableStateOf(initialPassword) }
    var agreed by rememberSaveable { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }

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
                    val fallbackName = fbUser.displayName ?: "new"
                    AuthSession.loginWithUpdate(context as Activity, fbUser, "google_sign_in", fallbackName,
                        { onMain(it) }, {})
                } else {
                    Toast.makeText(context, R.string.msg_user_signed_up_failed, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun submit() {
        if (busy) return
        if (!agreed) {
            Toast.makeText(context, R.string.hint_signup_agreed_title, Toast.LENGTH_LONG).show()
            Toast.makeText(context, R.string.hint_signup_agreed_desc, Toast.LENGTH_LONG).show()
            return
        }
        if (!Const.NAME_REG_EXP.toRegex().matches(name)) {
            Toast.makeText(context, R.string.msg_username_wrong, Toast.LENGTH_LONG).show()
            return
        }
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
            val ok = FirebaseAuthService.signUpEmail(email, password, name)
            busy = false
            if (ok) {
                val fbUser = FirebaseAuth.getInstance().currentUser
                if (fbUser != null) {
                    val userHelper = AuthSession.createUserHelper(context as Activity, fbUser, name, email, password)
                    onMain(userHelper)
                }
            } else {
                Toast.makeText(context, R.string.msg_user_signed_up_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(context.getString(R.string.title_signup), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            enabled = !busy,
            label = { Text(context.getString(R.string.hint_login)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            enabled = !busy,
            label = { Text(context.getString(R.string.hint_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            enabled = !busy,
            label = { Text(context.getString(R.string.hint_pass)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreed, onCheckedChange = { agreed = it }, enabled = !busy)
            Text(
                context.getString(R.string.hint_signup_agreed_desc),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable {
                    Utils.displayHtmlAlertDialog(context, R.string.str_about_user_agreement_html_source)
                }
            )
        }
        Spacer(Modifier.height(8.dp))
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
            context.getString(R.string.str_signup_go_off),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable { onGoToLogin(email, name, password) }
        )
        Spacer(Modifier.height(8.dp))
        Text(
            context.getString(R.string.lbl_continue_unregistered),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable { onGoToLogin("support@attplus.in", "", "support") }
        )
        if (busy) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
