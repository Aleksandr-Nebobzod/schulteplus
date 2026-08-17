package org.nebobrod.schulteplus.ui.auth

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.analytics.Analytics

/**
 * Онбординг — 3 слайда (D-19, design.md §2.5): показ — первый запуск приложения
 * до входа (глобальный битфлаг ONBOARDING_SHOWN, OnboardingPrefs).
 * Слайд 2 — 5 карточек с ценами и кредитом (правки 4.1/4.2); слайд 3 —
 * регистрация или демо. Цены/кредит — константы макета (механика — SP-06).
 */
@Composable
fun OnboardingScreen(
    onSignup: () -> Unit,
    onContinueWithoutRegistration: () -> Unit
) {
    val context = LocalContext.current
    var slide by rememberSaveable { mutableStateOf(0) }
    var selectedExercise by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { Analytics.onboardingShown(context) }

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
            AppIcon(size = 56.dp)
            Spacer(Modifier.height(14.dp))
            when (slide) {
                0 -> Slide1(onNext = { slide = 1 })
                1 -> Slide2(
                    selected = selectedExercise,
                    onSelect = {
                        selectedExercise = it
                        Analytics.onboardingExerciseSelected(context, it)
                    },
                    onStart = { slide = 2 }
                )
                else -> Slide3(
                    onSignup = onSignup,
                    onContinue = onContinueWithoutRegistration
                )
            }
        }
    }
}

/** Кредит нового пользователя (слайд 2); карточка доступна при price <= CREDIT. TODO SP-06. */
private const val CREDIT = 10

private data class ExerciseOption(val id: String, val titleRes: Int, val descRes: Int, val price: Int)

private val exerciseOptions = listOf(
    ExerciseOption("gcb_sch_num", R.string.ob_card_num_title, R.string.ob_card_num_desc, 4),
    ExerciseOption("gcb_sch_letters", R.string.ob_card_letters_title, R.string.ob_card_letters_desc, 4),
    ExerciseOption("gcb_sch_bits", R.string.ob_card_bits_title, R.string.ob_card_bits_desc, 4),
    ExerciseOption("gcb_space_basics", R.string.ob_card_basics_title, R.string.ob_card_basics_desc, 50),
    ExerciseOption("gcb_space_sssr", R.string.ob_card_spheres_title, R.string.ob_card_spheres_desc, 100)
)

/** Слайд 1 «Что это»: название, 1–2 строки о тренировке. */
@Composable
private fun Slide1(onNext: () -> Unit) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            context.getString(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))
        Text(
            context.getString(R.string.ob_slide1_body),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(40.dp))
        AuthButton(text = context.getString(R.string.ob_next), onClick = onNext)
        Spacer(Modifier.height(16.dp))
        OnboardingDots(count = 3, selected = 0)
    }
}

/** Слайд 2 «Выбор упражнения»: бейдж кредита, 5 карточек с бейджами цен + «Начать». */
@Composable
private fun Slide2(
    selected: String,
    onSelect: (String) -> Unit,
    onStart: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth()) {
        Text(
            context.getString(R.string.ob_slide2_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        // правка 4.1: бейдж кредита — сразу после строки заголовка, слева
        CreditBadge(credit = CREDIT)
        Spacer(Modifier.height(8.dp))
        Text(
            context.getString(R.string.ob_slide2_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
        exerciseOptions.forEach { option ->
            ExerciseCard(
                title = context.getString(option.titleRes),
                description = context.getString(option.descRes),
                price = option.price,
                affordable = option.price <= CREDIT,
                selected = selected == option.id,
                onClick = { onSelect(option.id) }
            )
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(14.dp))
        AuthButton(text = context.getString(R.string.ob_start), onClick = onStart)
        Spacer(Modifier.height(16.dp))
        OnboardingDots(count = 3, selected = 1)
    }
}

/** Слайд 3 «Регистрация»: выгоды аккаунта + «Зарегистрироваться» / «Без регистрации». */
@Composable
private fun Slide3(onSignup: () -> Unit, onContinue: () -> Unit) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            context.getString(R.string.ob_slide3_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(14.dp))
        Text(
            context.getString(R.string.ob_slide3_body),
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
                    "•  " + context.getString(R.string.ob_benefit_1),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "•  " + context.getString(R.string.ob_benefit_2),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(28.dp))
        AuthButton(text = context.getString(R.string.ob_signup), onClick = onSignup)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text(context.getString(R.string.ob_continue_without_registration))
        }
        Spacer(Modifier.height(16.dp))
        OnboardingDots(count = 3, selected = 2)
    }
}
