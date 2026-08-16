package org.nebobrod.schulteplus.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.nebobrod.schulteplus.R

/**
 * Компоненты онбординга (SP-03, слайд 2/3): монета, бейдж кредита, бейдж цены,
 * карточка упражнения, точки-индикаторы. Цены и кредит — отображаемые константы
 * макета (механика цен/кредита — SP-06, TODO при реализации).
 */

/** Монета-пиктограмма (правка 4.1): круг с золотым радиальным градиентом. */
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

/** Бейдж кредита на слайде 2: монета + «%d псимонет — ваш кредит» (строка). */
@Composable
fun CreditBadge(credit: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
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
                context.getString(R.string.ob_credit_badge, credit),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** Зелёные тона бейджа «доступно» (цена ≤ кредита) — из макета (правка 4.2). */
private val affordableBadgeBgLight = Color(0x994CAF50)
private val affordableBadgeFgLight = Color(0xFF1B5E20)
private val affordableBadgeBgDark = Color(0x8C2E7D32)
private val affordableBadgeFgDark = Color(0xFFA5D6A7)

/**
 * Бейдж цены на карточке упражнения: монета + цена (plurals «монета/монеты/монет»).
 * Зелёный — цена ≤ кредита, красный (errorContainer) — цена > кредита.
 */
@Composable
fun PriceBadge(price: Int, affordable: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dark = isSystemInDarkTheme()
    val bg = if (affordable) {
        if (dark) affordableBadgeBgDark else affordableBadgeBgLight
    } else MaterialTheme.colorScheme.errorContainer
    val fg = if (affordable) {
        if (dark) affordableBadgeFgDark else affordableBadgeFgLight
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
                context.resources.getQuantityString(R.plurals.ob_price_coin, price, price),
                style = MaterialTheme.typography.labelMedium,
                color = fg,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Карточка упражнения (слайд 2). Карточка нейтральная (полупрозрачная подложка;
 * selected — плотнее + рамка primary); цвет доступности — у бейджа цены.
 * Радиокнопка выбора — только у доступных.
 */
@Composable
fun ExerciseCard(
    title: String,
    description: String,
    price: Int,
    affordable: Boolean,
    selected: Boolean = false,
    onClick: () -> Unit = {},
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
            .clickable(enabled = affordable, onClick = onClick)
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
            PriceBadge(price = price, affordable = affordable)
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
