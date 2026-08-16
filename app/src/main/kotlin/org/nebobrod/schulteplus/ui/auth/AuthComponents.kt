package org.nebobrod.schulteplus.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nebobrod.schulteplus.R

/**
 * Компоненты стартовых экранов (SP-03): порт утверждённых макетов
 * (designpreview → app-код). Фон экранов — bg_login_03; контролы — на
 * полупрозрачной подложке colorScheme.surfaceContainer; кнопки — без прозрачности.
 */

/** Пиктограмма приложения (вверху экранов): светлая тема — цветная, тёмная — ч/б. */
@Composable
fun AppIcon(size: Dp = 56.dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(if (isSystemInDarkTheme()) R.drawable.ic_logo_100_bw else R.drawable.ic_logo_100_color),
        contentDescription = null,
        modifier = modifier.size(size)
    )
}

/** Фон экрана: bg_login_03 на всю площадь (слой под контентом). */
@Composable
fun ScreenBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.bg_login_03),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize()
    )
}

/** Логотип-сетка 4×4 (таблица Шульте) — для Splash (на брендовом фоне). */
@Composable
fun SchulteLogo(boxSize: Dp, background: Color, grid: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(boxSize).background(background, RoundedCornerShape(boxSize / 5)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxWidth().padding(boxSize / 7)) {
            val n = 4
            val stroke = size.width / 42
            for (i in 1 until n) {
                val f = i.toFloat() / n
                drawLine(grid, androidx.compose.ui.geometry.Offset(0f, size.height * f),
                    androidx.compose.ui.geometry.Offset(size.width, size.height * f), strokeWidth = stroke)
                drawLine(grid, androidx.compose.ui.geometry.Offset(size.width * f, 0f),
                    androidx.compose.ui.geometry.Offset(size.width * f, size.height), strokeWidth = stroke)
            }
        }
    }
}

/**
 * Поле ввода Auth: label, инлайн-ошибка (isError + supportingText), trailing
 * (show/hide), autofill-подсказки. Фон поля — ПРОЗРАЧНЫЙ (правка 3.1):
 * подложку даёт карточка FieldsCard.
 */
@Composable
fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // autofillHints недоступны в String-оверлоаде material3 1.5-alpha (BOM 2026.06.01)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Карточка полей ввода (правка 3.1): ВСЕ поля экрана — на одной полупрозрачной
 * подложке (surfaceContainer); сами поля прозрачные.
 */
@Composable
fun FieldsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) { content() }
}

/** Показ/скрытие пароля (show/hide); тексты передаются из экрана (локализация). */
@Composable
fun PasswordTrailing(shown: Boolean, show: String, hide: String, onToggle: () -> Unit) {
    TextButton(onClick = onToggle) { Text(if (shown) hide else show, fontSize = 12.sp) }
}

/** Основная кнопка со встроенным спиннером при загрузке (без прозрачности). */
@Composable
fun AuthButton(
    text: String,
    busy: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val spinnerColor = MaterialTheme.colorScheme.onPrimary
    Button(onClick = onClick, enabled = !busy, modifier = modifier.fillMaxWidth()) {
        if (busy) {
            Box(Modifier.size(16.dp)) {
                Canvas(Modifier.fillMaxWidth()) {
                    drawArc(color = spinnerColor, startAngle = 0f, sweepAngle = 260f,
                        useCenter = false, style = Stroke(width = 2.dp.toPx()))
                }
            }
            Spacer(Modifier.width(8.dp))
        }
        Text(text)
    }
}

/** Google-логотип: цветная «G» на белой плашке. */
@Composable
fun GoogleLogo(size: Dp = 22.dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(size).background(Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "G",
            color = Color(0xFF4285F4),
            fontSize = (size.value * 0.62f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Кнопка «Log in with Google»: белая плашка + брендированный логотип (без прозрачности). */
@Composable
fun GoogleButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    OutlinedButton(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        GoogleLogo(20.dp)
        Spacer(Modifier.width(10.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Горизонтальный разделитель «or»; текст передаётся из экрана (локализация). */
@Composable
fun OrDivider(text: String = "or", modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.weight(1f).height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Text(
            text,
            Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Нижняя текстовая ссылка-переход (login ↔ signup). */
@Composable
fun BottomLink(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}
