package org.nebobrod.schulteplus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Схемы редизайна стартовых экранов (SP-03, D-18/D-20): светлая — из XML-палитры
 * (themes.xml/colors.xml), тёмная — согласованная брендовая (порт PreviewPalette
 * из утверждённых макетов). surfaceContainer — полупрозрачная подложка контролов
 * на bg_login_03 (светлая — светло-серая, тёмная — тёмно-серая).
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF1E397E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF7681E8),
    onPrimaryContainer = Color(0xFF11112B),
    secondary = Color(0xFF40294C),
    onSecondary = Color(0xFF89C8FF),
    background = Color(0xFFDDDDDD),
    onBackground = Color(0xFF111111),
    surface = Color(0xFFDDDDDD),
    onSurface = Color(0xFF111111),
    surfaceContainer = Color(0xB3FFFFFF),
    surfaceContainerHigh = Color(0xD9FFFFFF),
    surfaceVariant = Color(0xFFC9DEF1),
    onSurfaceVariant = Color(0xFF444444),
    outline = Color(0xFF666666),
    outlineVariant = Color(0xFF9AA3AE),
    error = Color(0xFF880000),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0x66ED7575),
    onErrorContainer = Color(0xFF111111)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF89C8FF),
    onPrimary = Color(0xFF11112B),
    primaryContainer = Color(0xFF1E397E),
    onPrimaryContainer = Color(0xFFD3E4FF),
    secondary = Color(0xFFA181CD),
    onSecondary = Color(0xFF40294C),
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFE6E6E6),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFE6E6E6),
    surfaceContainer = Color(0x8C111111),
    surfaceContainerHigh = Color(0xA6111111),
    surfaceVariant = Color(0xFF2A2A3E),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF888888),
    outlineVariant = Color(0xFF444444),
    error = Color(0xFFED7575),
    onError = Color(0xFF111111),
    errorContainer = Color(0x66623232),
    onErrorContainer = Color(0xFFED7575)
)

@Composable
fun SchultePlusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
