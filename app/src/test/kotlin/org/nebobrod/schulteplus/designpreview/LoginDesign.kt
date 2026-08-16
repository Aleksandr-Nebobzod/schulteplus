package org.nebobrod.schulteplus.designpreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.nebobrod.schulteplus.R

/**
 * Login (design.md §2.3): email + пароль (show/hide), инлайн-ошибка (isError +
 * supportingText на email), «Забыли пароль?», «Go on», «Log in with Google»
 * (брендированная), переход в Signup.
 * Фон — bg_login_03 на всю площадь; контролы — на полупрозрачной подложке;
 * вверху — пиктограмма приложения.
 */
@Composable
fun LoginDesign(dark: Boolean) {
    PreviewTheme(dark) {
        var passwordShown by remember { mutableStateOf(false) }
        Box(Modifier.fillMaxSize()) {
            ScreenBackground()
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(36.dp))
                AppIcon(dark, size = 64.dp)
                Spacer(Modifier.height(18.dp))
                Text(
                    "Log in",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(20.dp))
                // правка 3.1: все поля — в одной карточке с полупрозрачной подложкой
                FieldsCard {
                    AuthField(
                        label = "Email address",
                        value = "user@example.com",
                        isError = true,
                        supportingText = "Invalid email address",
                        keyboardType = KeyboardType.Email
                    )
                    AuthField(
                        label = "Password",
                        value = "secret123",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (passwordShown) androidx.compose.ui.text.input.VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailing = { PasswordTrailing(passwordShown) { passwordShown = !passwordShown } }
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {}) { Text("Forgot password?") }
                }
                Spacer(Modifier.height(4.dp))
                AuthButton(text = "Go on")
                Spacer(Modifier.height(16.dp))
                OrDivider()
                Spacer(Modifier.height(16.dp))
                GoogleButton(text = "Log in with Google")
                Spacer(Modifier.height(24.dp))
                BottomLink("Don't have an account?  Go to sign up!")
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
