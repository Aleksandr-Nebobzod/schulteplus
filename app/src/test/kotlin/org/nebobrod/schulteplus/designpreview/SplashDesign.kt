package org.nebobrod.schulteplus.designpreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Splash — вариант B (D-17, design.md §2.2): чисто брендовая заставка ~0.6–0.9 c,
 * без точек-индикаторов и без статус-бара (полный экран). Логотип-блок + имя +
 * версия на брендовом градиенте. Проверка Firebase-сессии — параллельно, в Main —
 * фон.
 */
@Composable
fun SplashDesign(dark: Boolean) {
    PreviewTheme(dark) {
        val gradient = if (dark) {
            // тёмный вариант: глубокий синий → фирменный синий → фиолетовый
            listOf(Color(0xFF0D1B3E), PreviewPalette.primaryContainerDark, Color(0xFF40294C))
        } else {
            listOf(Color(0xFF1E397E), PreviewPalette.primaryContainerLight, Color(0xFF40294C))
        }
        Box(
            Modifier.fillMaxSize().background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SchulteLogo(
                    boxSize = 108.dp,
                    background = Color(0x33FFFFFF),
                    grid = Color.White
                )
                Spacer(Modifier.height(28.dp))
                Text(
                    "Schulte Plus",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "v120 · Entada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.82f)
                )
            }
        }
    }
}
