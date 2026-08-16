package org.nebobrod.schulteplus.designpreview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
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
 *  Фон поля — ПРОЗРАЧНЫЙ (правка 3.1): подложку даёт карточка FieldsCard, а не поле. */
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
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Карточка полей ввода (правка 3.1): ВСЕ поля одного экрана — на одной
 * полупрозрачной подложке (surfaceContainer: светлая — светло-серая, тёмная —
 * тёмно-серая). Сами поля прозрачные; кнопки и чекбокс-блок — вне карточки.
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

/** Монета-пиктограмма (правка 4.1): простой круг с золотым радиальным градиентом. */
@Composable
fun CoinIcon(size: Dp, modifier: Modifier = Modifier) {
    val brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFFF3C4), Color(0xFFFFC107), Color(0xFFB8860B))
    )
    Box(
        modifier
            .size(size)
            .background(brush, CircleShape)
            .border(1.dp, Color(0xFF8B6914), CircleShape)
    )
}

/** Бейдж кредита на слайде 2 онбординга (правка 4.1): монета + текст (дословно от заказчика). */
@Composable
fun CreditBadge(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CoinIcon(16.dp)
            Text(
                "10 псимонет — ваш кредит",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
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

/**
 * Бейдж цены на карточке упражнения слайда 2 (правка 4.2): монета + цена.
 * Зелёный — цена ≤ кредита, красный (errorContainer) — цена > кредита.
 * Текст дословно: «4 монеты», «50 монет», «100 монет» (не «psycoin»).
 */
@Composable
fun PriceBadge(price: Int, affordable: Boolean, dark: Boolean, modifier: Modifier = Modifier) {
    val bg = if (affordable) {
        if (dark) PreviewPalette.affordableBadgeBgDark else PreviewPalette.affordableBadgeBgLight
    } else MaterialTheme.colorScheme.errorContainer
    val fg = if (affordable) {
        if (dark) PreviewPalette.affordableBadgeFgDark else PreviewPalette.affordableBadgeFgLight
    } else MaterialTheme.colorScheme.onErrorContainer
    Box(
        modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CoinIcon(14.dp)
            Text(
                priceText(price),
                style = MaterialTheme.typography.labelMedium,
                color = fg,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/** Русская форма слова «монета» по числу: 1 монета, 4 монеты, 50 монет. */
private fun priceText(price: Int): String {
    val mod10 = price % 10
    val mod100 = price % 100
    val form = when {
        mod10 == 1 && mod100 != 11 -> "монета"
        mod10 in 2..4 && mod100 !in 12..14 -> "монеты"
        else -> "монет"
    }
    return "$price $form"
}

/**
 * Карточка упражнения для слайда 2 онбординга (правка 4.2). Карточка нейтральная
 * (полупрозрачная подложка surfaceContainer; selected — плотнее + рамка primary);
 * цвет доступности — у бейджа цены: зелёный (цена ≤ кредита) / красный (дороже).
 * Радиокнопка выбора — только у доступных.
 */
@Composable
fun ExerciseCard(
    title: String,
    description: String,
    price: Int,
    affordable: Boolean,
    dark: Boolean,
    selected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    val bg = if (selected) MaterialTheme.colorScheme.surfaceContainerHigh
    else MaterialTheme.colorScheme.surfaceContainer
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val borderWidth = if (selected) 2.dp else 1.dp

    Row(
        modifier
            .fillMaxWidth()
            .background(bg, shape)
            .border(borderWidth, borderColor, shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(2.dp))
            Text(description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            PriceBadge(price = price, affordable = affordable, dark = dark)
        }
        if (affordable) {
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
