package org.nebobrod.schulteplus.designpreview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.nebobrod.schulteplus.R

/**
 * Signup (design.md §2.4): имя/email/пароль (+show/hide), чекбокс согласия с одним
 * пояснением и ссылками политика/соглашение, «Go on», Google, переход в Login,
 * «Continue unregistered».
 * Фон — bg_login_03; контролы — на полупрозрачной подложке; вверху — пиктограмма.
 */
@Composable
fun SignupDesign(dark: Boolean) {
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
                Spacer(Modifier.height(32.dp))
                AppIcon(dark, size = 56.dp)
                Spacer(Modifier.height(14.dp))
                Text(
                    "Sign Up",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(20.dp))
                // правка 3.1: все поля — в одной карточке с полупрозрачной подложкой
                FieldsCard {
                    AuthField(
                        label = "Username (pseudonym)",
                        value = "Vasya"
                    )
                    AuthField(
                        label = "Email address",
                        value = "vasya@example.com",
                        keyboardType = KeyboardType.Email
                    )
                    AuthField(
                        label = "Schulte-plus password",
                        value = "secret123",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (passwordShown) androidx.compose.ui.text.input.VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailing = { PasswordTrailing(passwordShown) { passwordShown = !passwordShown } }
                    )
                }
                Spacer(Modifier.height(12.dp))
                // согласие: один чекбокс + одно пояснение со ссылками, на полупрозрачной подложке
                val linkColor = MaterialTheme.colorScheme.primary
                val consentText = buildAnnotatedString {
                    append("I agree to the ")
                    withStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Medium)) { append("Privacy Policy") }
                    append(" and ")
                    withStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Medium)) { append("Terms of Service") }
                    append(".")
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Checkbox(checked = true, onCheckedChange = {})
                        Column(Modifier.padding(top = 10.dp)) {
                            Text(
                                consentText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                AuthButton(text = "Go on")
                Spacer(Modifier.height(14.dp))
                GoogleButton(text = "Log in with Google")
                Spacer(Modifier.height(20.dp))
                BottomLink("Already have an account?  Go to login!")
                Spacer(Modifier.height(10.dp))
                Text(
                    "Continue unregistered",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable(onClick = {})
                )
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
