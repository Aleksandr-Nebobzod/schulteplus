package org.nebobrod.schulteplus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Светлая схема из XML-палитры приложения (themes.xml/colors.xml). */
private val LightColors = lightColorScheme(
    primary = Color(0xFF1E397E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF7681E8),
    secondary = Color(0xFF40294C),
    onSecondary = Color(0xFF89C8FF),
    background = Color(0xFFDDDDDD),
    surface = Color(0xFFDDDDDD),
    error = Color(0xFF880000)
)

@Composable
fun SchultePlusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else LightColors,
        content = content
    )
}
