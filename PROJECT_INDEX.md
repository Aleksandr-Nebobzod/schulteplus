# Project Index: Schulte Plus

*Generated: 2026-08-23* | *98 source files: 78 Java (app), 14 Kotlin (app), 6 Kotlin (shared KMP)*

## Project Structure

```
schulteplus/
├── app/                                  # Main Android module (Java + Kotlin)
│   ├── build.gradle                      # App-level build config (dependencies, SDK versions)
│   ├── proguard-rules.pro                # ProGuard rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml       # App manifest (activities, permissions, themes)
│       │   ├── assets/                   # HTML help pages, RSS, sitemap
│       │   ├── res/                      # Resources (layouts, drawables, values, navigation, raw)
│       │   ├── java/org/nebobrod/schulteplus/   # Java: core engine, data layer, View-based UI
│       │   └── kotlin/org/nebobrod/schulteplus/ # Kotlin: auth (Compose), analytics
│       ├── test/                         # Unit tests (JVM, JUnit 4)
│       └── androidTest/                  # Instrumentation tests (device/emulator)
├── shared/                               # Kotlin Multiplatform module (commonMain)
│   └── src/commonMain/kotlin/org/nebobrod/schulteplus/  # Const, AppContext, AuthService, ...
├── docs/                                 # ТЗ и планы (см. раздел Documentation)
├── lib/                                  # Library module (build config only)
├── gradle/                               # Gradle wrapper
├── build.gradle                          # Root build config (Kotlin 2.2.21, MPP/Compose plugins)
├── settings.gradle                       # Project settings (rootProject.name = "Schulte Plus")
├── gradle.properties                     # JVM args, AndroidX, R8 settings
├── CLAUDE.md                             # Dev guidance for Claude Code
├── README.md / README_RU.md              # Project overview (EN/RU)
└── PROJECT_INDEX.md                      # This file
```

## Migration Status (Java → Kotlin)

Миграция активна (ветка `feature/kotlin2`): auth/analytics уже на Kotlin+Compose, ядро упражнений (`common/`, `data/`) пока Java, общие константы/модели переезжают в `shared/commonMain` (KMP). Новый доменный код пишется сразу на Kotlin в `shared` (см. `docs/TZ_Mishmash.md`).

## Entry Points

| Entry | Path | Purpose |
|---|---|---|
| App class | `Utils.java` (extends `Application`) | Global context, static utilities, crash logging |
| Launcher | `ui/AuthActivity.kt` (+ `ui/auth/` Compose) | Splash/Login/Signup/Onboarding на Compose (заменили `SplashActivity`/`LoginActivity`/`SignupActivity`) |
| Main hub | `MainActivity.java` | Bottom nav (Dashboard/Home/Schulte/More) + FAB exercise launcher |
| Exercise activities | `ui/schulte/SchulteActivity`, `ui/basics/BasicsActivity`, `ui/sssr/SssrActivity` | Fullscreen exercise sessions |
| Settings | `PrefsPopupFragment`, `PrefsSettingsFragment`, `PrefsChoiceFragment` | User preferences |
| Invest/Unlock | `InvestActivity` | Quiz/purchase to unlock exercises with psycoins |

## Core Modules

### Shared KMP (`shared/commonMain`) — общие модели и сервисы (Kotlin)

| File | Role |
|---|---|
| `Const.kt` | ALL preference keys, exercise IDs (`KEY_PRF_*`), achievement flags, intro bitflags — единый источник (Java-версия удалена) |
| `AppContext.kt` | Абстракция контекста приложения для KMP |
| `AuthService.kt` | Сервис авторизации (contract для KMP, реализация в app: `FirebaseAuthService`) |
| `ExerciseStats.kt` | Статистика упражнения (числа для ExResult) |
| `Validatable.kt` | Интерфейс валидации результатов |
| `Shared.kt` | Точка входа модуля |

### Common (`common/`) — Exercise Engine (Java)

| File | Role |
|---|---|
| `Exercise.java` | Abstract base for all exercise types, parameterized on `ExResult` |
| `ExerciseRunner.java` | Singleton: user state, preferences CRUD, exercise lifecycle (`start`/`complete`/`clear`), stat accumulation |
| `ExerciseServices.java` | Сервисный мост домена (шаблоны символов, saver/writer) |
| `STable.java` | Schulte Table: grid of `SCell`, probability distribution via `camelSurface()`, shuffle, turn journal, result calc |
| `SCell.java` | Single cell: value, text, color, coordinates |
| `GridAdapter.java` | Adapter: renders `STable` cells into a `GridView` (кандидат на paved-режим «Мешанины») |
| `SymbolTemplate.java`, `ResourceSymbolTemplate.java` | Шаблоны символов (числа/буквы/цвета) |
| `ResultSaver.java`, `TurnWriter.java` | Персистенция результата и ходов (имплементации в `data/`) |
| `Tile*Paving.java` (4) | Tile arrangement algorithms (branch, fill, pick, squash) — «Мешанина»: рефакторинг squash в `shared/TilePaving.kt` |
| `AppExecutors.java`, `NetworkConnectivity.java`, `Log.java`, `SnackBarManager.java` | Утилиты (потоки, сеть, лог, snackbar) |

### Data (`data/`) — Dual Local+Cloud Persistence (Java)

| File | Role |
|---|---|
| `DataRepository.java` | Interface: CRUD + `WhereCond` enum (EQ/GE/LE) for query conditions |
| `DataRepos.java` | Facade: writes to ORMLite then Firestore; reads resolve conflicts by timestamp |
| `DataOrmRepo.java` | Local SQLite via ORMLite |
| `DataFirestoreRepo.java` | Cloud Firestore (в `fbservices/`) |
| `DatabaseHelper.java`, `DatabaseConfigUtil.java` | ORMLite helper: create/upgrade local DB (Achievement, ExResult, Turn, UserHelper, AdminNote) |
| `ExResult.java` (+ `ExResultSchulte/Basics/Sssr`) | Core result hierarchy |
| `ExType.java` | Exercise type metadata from `res/raw/ex_types.json`; unlock requirements |
| `DefaultResultSaver.java`, `DefaultTurnWriter.java` | Реализации saver/writer для ORMLite+Firestore |
| `Achievement.java`, `AchievementArrayAdapter.java`, `UserHelper.java`, `Turn.java`, `AdminNote.java`, `Identifiable.java` | Сущности и адаптеры |
| `z_DataRepository.java` | (z_-файл — не используется в основном потоке) |

### UI (`ui/`) — Activities, Fragments, View Models

Java (View-система):
- `MainActivity.java` — центральный хаб (nav, FAB, in-app update)
- `ui/schulte/SchulteActivity`, `ui/basics/BasicsActivity`, `ui/sssr/SssrActivity` — игровые экраны
- `ui/schulte/SchulteSettings`, `ui/basics/BasicSettings`, `ui/sssr/SssrSettings` — настройки упражнений
- Подпапки: `dashboard/`, `home/`, `notifications/`, `schulteparents/`, `basics/`, `sssr/`
- `InvestActivity`, `Prefs*Fragment`, `ExResult*Adapter`, `SpCalendarView`, `RichEditorDialogFragment`, `KeyValueView`, `z_AlertDialogFragment`

Kotlin (Compose):
- `ui/AuthActivity.kt` — хост auth-экранов
- `ui/auth/`: `SplashScreen.kt`, `LoginScreen.kt`, `SignupScreen.kt`, `OnboardingScreen.kt`, `AuthComponents.kt`, `OnboardingPrefs.kt`
- `ui/theme/Theme.kt` — Material-тема
- `auth/AuthSession.kt`, `auth/FirebaseAuthService.kt` — сессия и реализация AuthService
- `analytics/Analytics.kt`, `common/AppNetwork.kt`, `common/StartupChecks.kt`

## Documentation (docs/)

| File | Purpose |
|---|---|
| `TZ_Mishmash.md` | ТЗ упражнения «Мешанина» (S4): замощение 10×10 плитками, Kotlin в shared, Java UI |
| `TZ_StartScreens_Redesign.md` | ТЗ редизайна стартовых экранов (SP-03, Compose) |
| `API36_upgrade_plan.md` | План обновления до Android 16 (API 36) |
| `ARCHITECTURE.md` | Архитектура приложения (C4/mermaid) |
| `BACKLOG.md` | Бэклог задач |
| `Decisions.md` | Реестр архитектурных решений |
| `MODULAR_TRANSITION.md` | План модульного перехода (KMP) |
| `Research_Addon_Platforms.md` (+ `-.md`) | Исследование аддон-платформ |
| `pdca/` | Циклы PDCA |

## Resource Highlights

```
res/
├── layout/                # 40+ XML layout files
├── drawable/              # 80+ drawables (icons, backgrounds, SVG vectors)
├── navigation/            # mobile_navigation.xml (bottom nav graph)
├── menu/                  # Bottom nav menu + overflow menus
├── values/                # strings, colors, styles, themes, dimens, arrays
├── values-ru/             # Russian translations
├── values-night/          # Dark theme overrides
├── xml/                   # 7 PreferenceScreen XMLs + backup rules
├── raw/                   # ex_types.json (exercise definitions), ormlite_config.txt
└── anim/                  # 3 animation XMLs
```

## Test Coverage

| Test type | Location | Files | Framework |
|---|---|---|---|
| Unit tests | `app/src/test/` | 3 (AnyTest, ExampleUnitTest, TilePavingTest) | JUnit 4.13 |
| Instrumentation | `app/src/androidTest/` | `data/`, `ui/`, ExampleInstrumentedTest | AndroidJUnitRunner, Espresso, Mockito |

Key test targets:
- `TilePavingTest` — сейчас гоняет `TileSquashPaving.main()`; по `docs/TZ_Mishmash.md` заменяется на `shared/commonTest/TilePavingTest.kt` (полнота замощения, детерминизм, инварианты PavingMap)
- `DataOrmRepoTest`, `DataFirestoreRepositoryTest` — persistence (второй требует устройство/эмулятор)

## Configuration Files

| File | Purpose |
|---|---|
| `build.gradle` (root) | Kotlin 2.2.21 (android + multiplatform + compose plugins), Firebase/GMS classpath |
| `shared/build.gradle` | KMP-плагин + `com.android.kotlin.multiplatform.library`; coroutines 1.10.2 |
| `app/build.gradle` | SDK versions, dependencies, build types, Firestore root path per variant |
| `gradle.properties` | JVM args, parallel builds, AndroidX, R8, config cache |
| `settings.gradle` | Repo config (Google, Maven Central, JitPack), module includes |
| `app/proguard-rules.pro` | Release obfuscation rules |

## Key Dependencies

> Версии обновлялись в ходе миграции (фикс `a115930` — upgrade play-services); актуальный список — `app/build.gradle`.

| Dependency | Version (на 26.07) | Purpose |
|---|---|---|
| Kotlin | 2.2.21 (актуально) | Язык: app + shared (MPP, Compose) |
| Compose / kotlinx-coroutines | 1.10.2 (shared) | UI-редизайн (SP-03), конкурентность |
| Firebase Firestore | 25.0.0 | Cloud database |
| Firebase Auth | 16.0.3 | Authentication |
| Firebase Crashlytics | 19.0.3 | Crash reporting |
| Firebase Analytics | 22.0.2 | Usage analytics |
| ORMLite Android | 6.1 | Local SQLite ORM |
| Google Play Services Auth | 16.0.0 → поднята | Google sign-in |
| Navigation Component | 2.7.7 | Fragment navigation |
| MPAndroidChart | 3.1.0 | Charts (calendar heatmap) |
| Glide | 4.11.0 | Image loading (GIF in Basics) |
| Gson | 2.10.1 | JSON parsing |

## Build Types

| Variant | Firestore Root | Debuggable | Crashlytics |
|---|---|---|---|
| `debug` | `spdbs/dev/` | true | disabled |
| `release` | `spdbs/test/` | false | enabled |

## Exercise Spaces by ID Prefix

| Prefix | Space | Activity | Settings |
|---|---|---|---|
| `gcb_sch` | Schulte tables | `SchulteActivity` | `SchulteSettings` |
| `gcb_bas` | Basics (visual) | `BasicsActivity` | `BasicSettings` |
| `gcb_sss` | SSSR | `SssrActivity` | `SssrSettings` |
| `gcb_space_schulte_parents` | Schulte Parents | (via SchulteActivity) | `Settings` (schulteparents) |

Exercise type metadata is loaded from `res/raw/ex_types.json` into `Map<String, ExType>` at startup. Each `ExType` tracks unlock requirements (achievements, psycoin purchase, certification quiz).
