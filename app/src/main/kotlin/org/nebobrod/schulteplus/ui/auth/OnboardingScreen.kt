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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.analytics.Analytics
import org.nebobrod.schulteplus.common.Const

/**
 * Онбординг — 3 слайда (D-19, design.md §2.5): показ — первый запуск приложения
 * до входа (глобальный битфлаг ONBOARDING_SHOWN, OnboardingPrefs).
 * Слайд 2 — 5 карточек с ценами и кошельком анонима (SP03-06: списание при выборе,
 * «Начать» активна только при выбранном); слайд 3 — имя анонима, регистрация
 * (возврат цены + сброс выбора) и автостарт выбранной тренировки по отсчёту 5 сек
 * (D-30). Цены — константы макета (механика — SP-06).
 */
@Composable
fun OnboardingScreen(
    onSignup: () -> Unit,
    onStartExercise: (uid: String, exTypeId: String) -> Unit
) {
    val context = LocalContext.current
    var slide by rememberSaveable { mutableStateOf(0) }
    var selectedExercise by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        OnboardingPrefs.ensureAnon(context)
        Analytics.onboardingShown(context)
    }

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
                    balance = OnboardingPrefs.anonBalance(context),
                    onSelect = {
                        selectedExercise = it
                        Analytics.onboardingExerciseSelected(context, it)
                    },
                    onStart = {
                        // SP03-11: повторный выбор купленного упражнения — бесплатно
                        if (!OnboardingPrefs.isPurchased(context, selectedExercise)) {
                            val price = exerciseOptions.first { it.id == selectedExercise }.price
                            OnboardingPrefs.spend(context, price)
                            OnboardingPrefs.markPurchased(context, selectedExercise)
                        }
                        slide = 2
                    }
                )
                else -> Slide3(
                    anonName = OnboardingPrefs.anonName(context),
                    onSignup = {
                        // SP03-11: прерывание регистрацией — полный откат покупки
                        val opt = exerciseOptions.first { it.id == selectedExercise }
                        OnboardingPrefs.refund(context, opt.price)
                        OnboardingPrefs.unpurchase(context, selectedExercise)
                        selectedExercise = ""
                        // SP03-14: при возврате «Продолжить без регистрации» онбординг начинается
                        // с первого слайда (иначе slide=2 остаётся в saveable и автостарт уходит с пустым выбором)
                        slide = 0
                        onSignup()
                    },
                    onStart = {
                        // SP03-14: автостарт только при реально выбранном упражнении;
                        // запуск — с реальным exTypeId (настройки применит setAnonExType в prefs анонима)
                        val option = exerciseOptions.firstOrNull { it.id == selectedExercise }
                        if (option != null) {
                            OnboardingPrefs.setAnonExType(context, option.id)
                            onStartExercise(OnboardingPrefs.anonUid(context), option.exTypeId)
                        } else {
                            slide = 1
                        }
                    }
                )
            }
        }
    }
}

/**
 * id — внутренний (выбор/покупка на слайде 2), exTypeId — реальный ID упражнения
 * (Const/ex_types.json), настройки карточки применяет OnboardingPrefs.setAnonExType.
 */
private data class ExerciseOption(
    val id: String,
    val exTypeId: String,
    val titleRes: Int,
    val descRes: Int,
    val price: Int
)

// SP03-14: реальные ID из Const — выдуманные «gcb_sch_num» и т.п. не существуют в ex_types.json:
// NPE в ExerciseRunner.loadPreference (размер остаётся 5×5) + default-ветка GridAdapter (пустые ячейки).
// Числа/Буквы/Биты — один и тот же S1 (gcb_schulte_1_sequence), различие — настройками (размер/символы).
private val exerciseOptions = listOf(
    ExerciseOption("sch_num", Const.KEY_PRF_EX_S1, R.string.ob_card_num_title, R.string.ob_card_num_desc, 4),
    ExerciseOption("sch_letters", Const.KEY_PRF_EX_S1, R.string.ob_card_letters_title, R.string.ob_card_letters_desc, 4),
    ExerciseOption("sch_bits", Const.KEY_PRF_EX_S1, R.string.ob_card_bits_title, R.string.ob_card_bits_desc, 4),
    ExerciseOption("basics", Const.KEY_PRF_EX_B1, R.string.ob_card_basics_title, R.string.ob_card_basics_desc, 50),
    ExerciseOption("sssr", Const.KEY_PRF_EX_R1, R.string.ob_card_spheres_title, R.string.ob_card_spheres_desc, 100)
)

/** Слайд 1 «Что это»: название (вне плашки), описание и кнопка — на общей подложке (SP03-08). */
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
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
    }
}

/** Слайд 2 «Выбор упражнения»: заголовок вне, кошелёк, карточки и кнопка — на общей подложке (SP03-08). */
@Composable
private fun Slide2(
    selected: String,
    balance: Int,
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
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                // SP03-06: кошелёк анонима (стартовый запас — OnboardingPrefs.ANON_CREDIT)
                CreditBadge(credit = balance)
                Spacer(Modifier.height(8.dp))
                Text(
                    context.getString(R.string.ob_slide2_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(20.dp))
                exerciseOptions.forEach { option ->
                    // SP03-11: купленное ранее упражнение — «0 псимонет», доступно всегда
                    val purchased = OnboardingPrefs.isPurchased(context, option.id)
                    ExerciseCard(
                        title = context.getString(option.titleRes),
                        description = context.getString(option.descRes),
                        price = if (purchased) 0 else option.price,
                        // SP03-16: доступность/цвет бейджа — по факту баланса кошелька (не константе CREDIT)
                        affordable = purchased || option.price <= balance,
                        selected = selected == option.id,
                        onClick = { onSelect(option.id) }
                    )
                    Spacer(Modifier.height(10.dp))
                }
                Spacer(Modifier.height(14.dp))
                // SP03-06: «Начать» доступна только после выбора упражнения (списание — в onStart)
                AuthButton(text = context.getString(R.string.ob_start), onClick = onStart,
                    enabled = selected.isNotEmpty())
                Spacer(Modifier.height(16.dp))
                OnboardingDots(count = 3, selected = 1)
            }
        }
    }
}

/** Слайд 3 «Регистрация»: заголовок вне, выгоды, имя и кнопки — на общей подложке (SP03-08). */
@Composable
private fun Slide3(anonName: String, onSignup: () -> Unit, onStart: () -> Unit) {
    val context = LocalContext.current
    var countdown by remember { mutableStateOf(5) }
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        onStart()
    }
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
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    context.getString(R.string.ob_slide3_body),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(20.dp))
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
                Spacer(Modifier.height(12.dp))
                // D-30/SP03-06: аноним получает случайное имя при первом показе (ensureAnon);
                // SP03-17: имя выделено жирным
                val nameLabel = context.getString(R.string.ob_slide3_anon_name, anonName)
                val nameIndex = nameLabel.indexOf(anonName)
                Text(
                    buildAnnotatedString {
                        if (nameIndex >= 0) {
                            append(nameLabel.substring(0, nameIndex))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(anonName) }
                            append(nameLabel.substring(nameIndex + anonName.length))
                        } else {
                            append(nameLabel)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(20.dp))
                AuthButton(text = context.getString(R.string.ob_signup), onClick = onSignup)
                Spacer(Modifier.height(12.dp))
                // D-30: «Старт… N» — неактивная кнопка с обратным отсчётом; при 1→0 стартует тренировка
                AuthButton(
                    text = context.getString(R.string.ob_start_countdown, countdown),
                    onClick = {},
                    enabled = false
                )
                Spacer(Modifier.height(16.dp))
                OnboardingDots(count = 3, selected = 2)
            }
        }
    }
}
