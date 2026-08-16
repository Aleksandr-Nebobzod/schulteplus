package org.nebobrod.schulteplus.designpreview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Локальные токены превью редизайна стартовых экранов (SP-03, design.md §3.1).
 * Значения — из app-палитры (res/values/colors.xml, themes.xml): primary #1E397E,
 * primaryContainer #7681E8, secondary #40294C, background #DDDDDD, error #880000.
 *
 * НЕ трогаем рабочий Theme.kt: при Ф3-реализации токены переносятся в app-код,
 * превью-файлы этого пакета удаляются.
 */
object PreviewPalette {
    // Светлая схема (из XML-палитры)
    val primaryLight = Color(0xFF1E397E)          // light_grey_2_blue
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFF7681E8) // light_grey_D_blue_special
    val onPrimaryContainerLight = Color(0xFF11112B)
    val secondaryLight = Color(0xFF40294C)        // light_grey_2_purple
    val onSecondaryLight = Color(0xFF89C8FF)      // light_grey_D_blue
    val backgroundLight = Color(0xFFDDDDDD)       // light_grey_D
    val onSurfaceLight = Color(0xFF111111)
    val surfaceContainerLight = Color(0xB3FFFFFF)  // светло-серый полупрозрачный (подложка контролов на bg_login_03)
    val surfaceContainerHighLight = Color(0xD9FFFFFF) // более плотная подложка (выбранная карточка)
    val surfaceVariantLight = Color(0xFFC9DEF1)   // light_grey_F_blue
    val onSurfaceVariantLight = Color(0xFF444444) // light_grey_4
    val outlineLight = Color(0xFF666666)          // light_grey_6
    val outlineVariantLight = Color(0xFF9AA3AE)
    val errorLight = Color(0xFF880000)            // light_grey_8_red
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0x66ED7575)   // light_grey_D_red, полупрозрачный
    val onErrorContainerLight = Color(0xFF111111)

    // Тёмная схема (согласована с палитрой, не дефолтная)
    val primaryDark = Color(0xFF89C8FF)           // light_grey_D_blue
    val onPrimaryDark = Color(0xFF11112B)
    val primaryContainerDark = Color(0xFF1E397E)  // light_grey_2_blue
    val onPrimaryContainerDark = Color(0xFFD3E4FF)
    val secondaryDark = Color(0xFFA181CD)         // light_grey_A_purple
    val onSecondaryDark = Color(0xFF40294C)
    val backgroundDark = Color(0xFF1A1A1A)
    val onSurfaceDark = Color(0xFFE6E6E6)         // заголовки/текст — светлые
    val surfaceContainerDark = Color(0x8C111111)  // тёмно-серый полупрозрачный (подложка контролов на bg_login_03)
    val surfaceContainerHighDark = Color(0xA6111111) // более плотная подложка (выбранная карточка)
    val surfaceVariantDark = Color(0xFF2A2A3E)
    val onSurfaceVariantDark = Color(0xFFAAAAAA)  // light_grey_A
    val outlineDark = Color(0xFF888888)           // light_grey_8
    val outlineVariantDark = Color(0xFF444444)    // light_grey_4
    val errorDark = Color(0xFFED7575)             // light_grey_D_red
    val onErrorDark = Color(0xFF111111)
    val errorContainerDark = Color(0x66623232)    // light_grey_2_red, полупрозрачный
    val onErrorContainerDark = Color(0xFFED7575)
}

/** Тема превью: светлая и тёмная схемы для всех макетов. */
@Composable
fun PreviewTheme(dark: Boolean, content: @Composable () -> Unit) {
    val scheme = if (dark) darkColorScheme(
        primary = PreviewPalette.primaryDark,
        onPrimary = PreviewPalette.onPrimaryDark,
        primaryContainer = PreviewPalette.primaryContainerDark,
        onPrimaryContainer = PreviewPalette.onPrimaryContainerDark,
        secondary = PreviewPalette.secondaryDark,
        onSecondary = PreviewPalette.onSecondaryDark,
        background = PreviewPalette.backgroundDark,
        onBackground = PreviewPalette.onSurfaceDark,
        surface = PreviewPalette.backgroundDark,
        onSurface = PreviewPalette.onSurfaceDark,
        surfaceContainer = PreviewPalette.surfaceContainerDark,
        surfaceContainerHigh = PreviewPalette.surfaceContainerHighDark,
        surfaceVariant = PreviewPalette.surfaceVariantDark,
        onSurfaceVariant = PreviewPalette.onSurfaceVariantDark,
        outline = PreviewPalette.outlineDark,
        outlineVariant = PreviewPalette.outlineVariantDark,
        error = PreviewPalette.errorDark,
        onError = PreviewPalette.onErrorDark,
        errorContainer = PreviewPalette.errorContainerDark,
        onErrorContainer = PreviewPalette.onErrorContainerDark,
    ) else lightColorScheme(
        primary = PreviewPalette.primaryLight,
        onPrimary = PreviewPalette.onPrimaryLight,
        primaryContainer = PreviewPalette.primaryContainerLight,
        onPrimaryContainer = PreviewPalette.onPrimaryContainerLight,
        secondary = PreviewPalette.secondaryLight,
        onSecondary = PreviewPalette.onSecondaryLight,
        background = PreviewPalette.backgroundLight,
        onBackground = PreviewPalette.onSurfaceLight,
        surface = PreviewPalette.backgroundLight,
        onSurface = PreviewPalette.onSurfaceLight,
        surfaceContainer = PreviewPalette.surfaceContainerLight,
        surfaceContainerHigh = PreviewPalette.surfaceContainerHighLight,
        surfaceVariant = PreviewPalette.surfaceVariantLight,
        onSurfaceVariant = PreviewPalette.onSurfaceVariantLight,
        outline = PreviewPalette.outlineLight,
        outlineVariant = PreviewPalette.outlineVariantLight,
        error = PreviewPalette.errorLight,
        onError = PreviewPalette.onErrorLight,
        errorContainer = PreviewPalette.errorContainerLight,
        onErrorContainer = PreviewPalette.onErrorContainerLight,
    )
    MaterialTheme(colorScheme = scheme, content = content)
}
