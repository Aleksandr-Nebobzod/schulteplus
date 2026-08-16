package org.nebobrod.schulteplus.designpreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.nebobrod.schulteplus.R

/**
 * Онбординг — 3 слайда (D-19, design.md §2.5). Показ — первый запуск приложения,
 * до входа (глобальный битфлаг ONBOARDING_SHOWN). Внизу — точки-индикаторы и
 * основная кнопка.
 *
 * Слайд 2 (правка 4.2) — 5 карточек с ценами: варианты Schulte «Числа 3×3» /
 * «Буквы 3×3» / «Биты 4×4» по 4 псикойна, Basics — 50, Spheres — 100. Карточки
 * нейтральные (серые полупрозрачные); цвет доступности — у бейджа цены: зелёный
 * (цена ≤ кредита) или красный (дороже). Под заголовком — бейдж кредита
 * с монетой. Слайд 3 — выгоды регистрации: все упражнения доступны, псикойны копятся.
 */
data class PreviewExerciseSpace(
    val id: String,
    val name: String,
    val description: String,
    val price: Int
)

/** Кредит нового пользователя на слайде 2; карточка доступна при price <= CREDIT. */
private const val CREDIT = 10

private val previewSpaces = listOf(
    PreviewExerciseSpace(
        id = "gcb_sch_num",
        name = "Числа 3×3",
        description = "Number grids for focus and peripheral vision",
        price = 4
    ),
    PreviewExerciseSpace(
        id = "gcb_sch_letters",
        name = "Буквы 3×3",
        description = "Number grids for focus and peripheral vision",
        price = 4
    ),
    PreviewExerciseSpace(
        id = "gcb_sch_bits",
        name = "Биты 4×4",
        description = "Number grids for focus and peripheral vision",
        price = 4
    ),
    PreviewExerciseSpace(
        id = "gcb_space_basics",
        name = "Basics",
        description = "Illusions and perception training",
        price = 50
    ),
    PreviewExerciseSpace(
        id = "gcb_space_sssr",
        name = "Spheres",
        description = "Quick-reaction selection tasks",
        price = 100
    )
)

@Composable
fun OnboardingDesign(slide: Int, dark: Boolean) {
    PreviewTheme(dark) {
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
                Spacer(Modifier.height(24.dp))
                AppIcon(dark, size = 56.dp)
                Spacer(Modifier.height(14.dp))
                when (slide) {
                    1 -> Slide1()
                    2 -> Slide2(dark)
                    else -> Slide3()
                }
            }
        }
    }
}

/** Слайд 1 «Что это»: пиктограмма приложения, название, 1–2 строки о тренировке. */
@Composable
private fun Slide1() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            "Schulte Plus",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Train your attention, peripheral vision and reaction speed.\nA few minutes a day keep your mind sharp.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(40.dp))
        AuthButton(text = "Next")
        Spacer(Modifier.height(16.dp))
        OnboardingDots(count = 3, selected = 0)
    }
}

/** Слайд 2 «Выбор упражнения»: бейдж кредита, 5 карточек с бейджами цен + «Начать» (правка 4.2). */
@Composable
private fun Slide2(dark: Boolean) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            "Choose your first exercise",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        // правка 4.1: бейдж кредита — сразу после строки заголовка, слева
        CreditBadge()
        Spacer(Modifier.height(8.dp))
        Text(
            "Every space unlocks more exercises as you train.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
        previewSpaces.forEachIndexed { i, space ->
            ExerciseCard(
                title = space.name,
                description = space.description,
                price = space.price,
                affordable = space.price <= CREDIT,
                dark = dark,
                selected = i == 0
            )
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(14.dp))
        AuthButton(text = "Start")
        Spacer(Modifier.height(16.dp))
        OnboardingDots(count = 3, selected = 1)
    }
}

/** Слайд 3 «Регистрация»: выгоды аккаунта + «Зарегистрироваться» / «Без регистрации». */
@Composable
private fun Slide3() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            "Keep your progress",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "Create an account to sync your stats across devices\nand see your achievements.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                .padding(vertical = 14.dp, horizontal = 16.dp)
        ) {
            Column {
                Text(
                    "•  All exercises are available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "•  Psycoins accumulate with every session",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(28.dp))
        AuthButton(text = "Sign up")
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Continue without registration")
        }
        Spacer(Modifier.height(16.dp))
        OnboardingDots(count = 3, selected = 2)
    }
}
