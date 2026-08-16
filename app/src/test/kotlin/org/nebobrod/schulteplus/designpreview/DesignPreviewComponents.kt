package org.nebobrod.schulteplus.designpreview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nebobrod.schulteplus.R

/**
 * Компоненты макетов редизайна стартовых экранов (SP-03, design.md §3.2).
 * Только для превью (test source set); рабочие AuthField/AuthButton Ф3-реализации
 * будут жить в app-коде.
 *
 * Фон экранов — R.drawable.bg_login_03; контролы (поля, карточки, чекбокс-блок,
 * бейджи) — на полупрозрачной подложке colorScheme.surfaceContainer; кнопки — без
 * прозрачности.
 */

/** Пиктограмма приложения из res (вверху экранов): светлая тема — цветная, тёмная — ч/б. */
@Composable
fun AppIcon(dark: Boolean, size: Dp = 56.dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(if (dark) R.drawable.ic_logo_100_bw else R.drawable.ic_logo_100_color),
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
        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
        modifier = modifier.fillMaxSize()
    )
}

/** Логотип-сетка 4x4 (таблица Шульте) — только для Splash (на брендовом фоне). */
@Composable
fun SchulteLogo(boxSize: Dp, background: Color, grid: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(boxSize).background(background, RoundedCornerShape(boxSize / 5)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxWidth().padding(boxSize / 7)) {
            // здесь size — это DrawScope.size (Size); параметр boxSize: Dp в Canvas не виден
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

/** Поле ввода Auth (design.md §3.2): label, isError + supportingText, trailing (show/hide).
 *  Подложка — полупрозрачная (surfaceContainer), чтобы фон-картинка просвечивала. */
@Composable
fun AuthField(
    label: String,
    value: String,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val overlay = MaterialTheme.colorScheme.surfaceContainer
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = overlay,
            unfocusedContainerColor = overlay,
            errorContainerColor = overlay
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/** Показ/скрытие пароля (show/hide). */
@Composable
fun PasswordTrailing(shown: Boolean, onToggle: () -> Unit) {
    TextButton(onClick = onToggle) { Text(if (shown) "Hide" else "Show", fontSize = 12.sp) }
}

/** Основная кнопка со встроенным спиннером при загрузке (без прозрачности). */
@Composable
fun AuthButton(
    text: String,
    busy: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spinnerColor = MaterialTheme.colorScheme.onPrimary
    Button(onClick = onClick, enabled = !busy, modifier = modifier.fillMaxWidth()) {
        if (busy) {
            Box(Modifier.size(16.dp)) {
                Canvas(Modifier.fillMaxWidth()) {
                    drawArc(color = spinnerColor, startAngle = 0f, sweepAngle = 260f,
                        useCenter = false, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
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
fun GoogleButton(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        GoogleLogo(20.dp)
        Spacer(Modifier.width(10.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Точки-индикаторы слайдов онбординга. */
@Composable
fun OnboardingDots(count: Int, selected: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { i ->
            val active = i == selected
            Box(
                Modifier
                    .size(if (active) 9.dp else 7.dp)
                    .background(
                        if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                        CircleShape
                    )
            )
        }
    }
}

/** Бейдж состояния доступа на карточке упражнения (цены псикойнов отложены — D-19, design.md §7). */
@Composable
fun AccessBadge(text: String, locked: Boolean = false, modifier: Modifier = Modifier) {
    val bg = if (locked) MaterialTheme.colorScheme.errorContainer
    else MaterialTheme.colorScheme.surfaceContainerHigh
    val fg = if (locked) MaterialTheme.colorScheme.onErrorContainer
    else MaterialTheme.colorScheme.onSurface
    Box(
        modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Карточка упражнения для слайда 2 онбординга. Полупрозрачная подложка
 * (surfaceContainer); locked=true — «серенькая» заблокированная (сниженная
 * контрастность) с пометкой «Зарегистрируйтесь»; options — строки-варианты
 * внутри карточки (Schulte), одна выбрана.
 */
@Composable
fun ExerciseCard(
    title: String,
    description: String,
    locked: Boolean = false,
    selected: Boolean = false,
    options: List<String> = emptyList(),
    selectedOption: Int = 0,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    val bg = when {
        locked -> MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.55f)
        selected -> MaterialTheme.colorScheme.surfaceContainerHigh
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    val borderColor = when {
        locked -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val borderWidth = if (selected) 2.dp else 1.dp
    val titleColor = if (locked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    else MaterialTheme.colorScheme.onSurface
    val descColor = if (locked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
    else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier
            .fillMaxWidth()
            .background(bg, shape)
            .border(borderWidth, borderColor, shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = titleColor)
            Spacer(Modifier.height(2.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = descColor)
            if (options.isNotEmpty() && !locked) {
                Spacer(Modifier.height(8.dp))
                options.forEachIndexed { i, option ->
                    val on = i == selectedOption
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                        Box(
                            Modifier.size(14.dp).border(
                                1.5.dp,
                                if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                CircleShape
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (on) {
                                Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            AccessBadge(if (locked) "Зарегистрируйтесь" else "Доступно", locked = locked)
        }
        if (!locked) {
            Box(
                Modifier.size(20.dp).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(Modifier.size(12.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                }
            }
        }
    }
}

/** Горизонтальный разделитель «or». */
@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.weight(1f).height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Text(
            "or",
            Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Нижняя текстовая ссылка-переход (login ↔ signup). */
@Composable
fun BottomLink(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}
