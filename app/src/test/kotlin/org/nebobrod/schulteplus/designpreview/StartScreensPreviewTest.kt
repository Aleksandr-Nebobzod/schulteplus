package org.nebobrod.schulteplus.designpreview

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Рендер PNG-макетов редизайна стартовых экранов (SP-03) на JVM без устройства.
 *
 * Запуск:  ./gradlew recordRoborazziDebug
 * Результат: PNG в <root>/temp/design-preview/ (путь строится от user.dir теста =
 * каталог модуля app/, parentFile = корень репозитория).
 *
 * Каждый экран — светлая и тёмная схемы. targetSdk 36: Robolectric 4.16.1
 * поддерживает SDK 36, но рендер идёт на @Config(sdk = 34) — стабильный канал
 * Robolectric (android-all-instrumented-14).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = RobolectricDeviceQualifiers.Pixel5)
class StartScreensPreviewTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val previewDir: File
        get() {
            val moduleDir = File(System.getProperty("user.dir") ?: ".").absoluteFile
            val rootDir = moduleDir.parentFile ?: moduleDir
            return rootDir.resolve("temp/design-preview")
        }

    private fun capture(name: String, content: @Composable () -> Unit) {
        previewDir.mkdirs()
        composeRule.setContent { content() }
        composeRule.onRoot().captureRoboImage(previewDir.resolve(name).absolutePath)
    }

    // Splash (вариант B, D-17) — без статус-бара
    @Test fun splash_light() = capture("splash_light.png") { SplashDesign(dark = false) }
    @Test fun splash_dark() = capture("splash_dark.png") { SplashDesign(dark = true) }

    // Login — с инлайн-ошибкой на email (isError + supportingText)
    @Test fun login_light() = capture("login_light.png") { LoginDesign(dark = false) }
    @Test fun login_dark() = capture("login_dark.png") { LoginDesign(dark = true) }

    // Signup — show/hide пароль, чекбокс согласия, демо-ссылка
    @Test fun signup_light() = capture("signup_light.png") { SignupDesign(dark = false) }
    @Test fun signup_dark() = capture("signup_dark.png") { SignupDesign(dark = true) }

    // Онбординг — 3 слайда (D-19)
    @Test fun onboarding_slide1_light() = capture("onboarding_slide1_light.png") { OnboardingDesign(slide = 1, dark = false) }
    @Test fun onboarding_slide1_dark() = capture("onboarding_slide1_dark.png") { OnboardingDesign(slide = 1, dark = true) }
    @Test fun onboarding_slide2_light() = capture("onboarding_slide2_light.png") { OnboardingDesign(slide = 2, dark = false) }
    @Test fun onboarding_slide2_dark() = capture("onboarding_slide2_dark.png") { OnboardingDesign(slide = 2, dark = true) }
    @Test fun onboarding_slide3_light() = capture("onboarding_slide3_light.png") { OnboardingDesign(slide = 3, dark = false) }
    @Test fun onboarding_slide3_dark() = capture("onboarding_slide3_dark.png") { OnboardingDesign(slide = 3, dark = true) }
}
