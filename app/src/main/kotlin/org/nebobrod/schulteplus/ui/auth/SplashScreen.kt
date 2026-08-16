package org.nebobrod.schulteplus.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.Utils
import org.nebobrod.schulteplus.auth.FirebaseAuthService
import org.nebobrod.schulteplus.data.DataOrmRepo
import org.nebobrod.schulteplus.data.UserHelper

/**
 * Сплэш — вариант B (D-17): чисто брендовая заставка ~600 мс с параллельной проверкой
 * Firebase-сессии (порт USER-блока SplashViewModel). Проверки версии/сети — фоном
 * в Main (StartupChecks). null в onSession → Login, иначе Main.
 */
@Composable
fun SplashScreen(onSession: (UserHelper?) -> Unit) {
    val context = LocalContext.current
    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val minDelay = async { delay(600) }
        val user = async(Dispatchers.IO) { checkUserSession() }
        minDelay.await()
        if (!navigated) {
            navigated = true
            onSession(user.await())
        }
    }

    // палитра макета (SplashDesign); после порта темы (Inc 2) — из colorScheme
    val light = listOf(Color(0xFF1E397E), Color(0xFF7681E8), Color(0xFF40294C))
    val dark = listOf(Color(0xFF0D1B3E), Color(0xFF7681E8), Color(0xFF40294C))
    val gradient = if (isSystemInDarkTheme()) dark else light

    Box(
        Modifier.fillMaxSize().background(Brush.linearGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SchulteLogo(108.dp, Color(0x33FFFFFF), Color.White)
            Spacer(Modifier.height(28.dp))
            Text(
                context.getString(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))
            Text(
                context.getString(R.string.app_version_full),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.82f)
            )
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
