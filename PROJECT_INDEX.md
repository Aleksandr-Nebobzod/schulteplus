# Project Index: Schulte Plus

*Generated: 2026-07-26* | *84 source files · ~58K tokens → 3K index (95% reduction)*

## Project Structure

```
schulteplus/
├── app/                                  # Main Android module
│   ├── build.gradle                      # App-level build config (dependencies, SDK versions)
│   ├── proguard-rules.pro                # ProGuard rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml       # App manifest (activities, permissions, themes)
│       │   ├── assets/                   # HTML help pages, RSS, sitemap
│       │   ├── res/                      # Resources (layouts, drawables, values, navigation, raw)
│       │   └── java/org/nebobrod/schulteplus/
│       │       ├── Utils.java            # Application class + static utility hub (1543 lines)
│       │       ├── common/               # Core exercise engine
│       │       ├── data/                 # Data layer (ORMLite + Firebase)
│       │       └── ui/                   # Activities, fragments, view models, adapters
│       ├── test/                         # Unit tests (JVM)
│       └── androidTest/                  # Instrumentation tests (device/emulator)
├── gradle/                               # Gradle wrapper
├── lib/                                  # Library module (build config only)
├── build.gradle                          # Root build config (plugins, repos)
├── settings.gradle                       # Project settings (rootProject.name = "Schulte Plus")
├── gradle.properties                     # JVM args, AndroidX, R8 settings
└── CLAUDE.md                             # Dev guidance for Claude Code
```

## Entry Points

| Entry | Path | Purpose |
|---|---|---|
| App class | `Utils.java` (extends `Application`) | Global context, static utilities, crash logging |
| Launcher | `SplashActivity` | Auth check → auto-login or `LoginActivity`/`SignupActivity` |
| Main hub | `MainActivity` | Bottom nav (Dashboard/Home/Schulte/More) + FAB exercise launcher |
| Exercise activities | `SchulteActivity`, `BasicsActivity`, `SssrActivity` | Fullscreen exercise sessions |
| Settings | `PrefsPopupFragment`, `PrefsSettingsFragment`, `PrefsChoiceFragment` | User preferences |
| Invest/Unlock | `InvestActivity` | Quiz/purchase to unlock exercises with psycoins |

## Core Modules

### Common (`common/`) — Exercise Engine

| File | Lines | Role |
|---|---|---|
| `Const.java` | ~165 | Interface: ALL preference keys, exercise IDs, achievement flags, bitmask intros |
| `ExerciseRunner.java` | ~670 | Singleton: user state, preferences CRUD, exercise lifecycle (`start`/`complete`/`clear`), stat accumulation |
| `STable.java` | ~530 | Schulte Table: grid of `SCell`, probability distribution via `camelSurface()`, shuffle, turn journal, result calc |
| `Exercise.java` | ~90 | Abstract base for all exercise types, parameterized on `ExResult` |
| `SCell.java` | ~60 | Single cell: value, text, color, coordinates |
| `GridAdapter.java` | ~100 | Adapter: renders `STable` cells into a `GridView` with squared/rectangular modes |
| `Turn.java` | ~80 | Data class: one turn record (timestamp, position, correctness, time delta) |
| `AppExecutors.java` | ~40 | Thread pool executors for network and disk I/O |
| `NetworkConnectivity.java` | ~40 | Internet connectivity checker |
| `Log.java` | ~20 | Log wrapper around `android.util.Log` |
| `Tile*Paving.java` (4 files) | ~120 | Tile arrangement algorithms (branch, fill, pick, squash) |

### Data (`data/`) — Dual Local+Cloud Persistence

| File | Lines | Role |
|---|---|---|
| `DataRepository.java` | ~115 | Interface: CRUD + `WhereCond` enum (EQ/GE/LE) for query conditions |
| `DataRepos.java` | ~390 | Facade: writes to ORMLite then Firestore; reads resolve conflicts by timestamp |
| `DataOrmRepo.java` | ~300 | Local SQLite via ORMLite (create, read, update, delete, batch load, list queries) |
| `DataFirestoreRepo.java` | ~312 | Cloud Firestore (same CRUD interface, `getListByField` with compound conditions) |
| `DatabaseHelper.java` | ~260 | ORMLite helper: creates/upgrades local DB tables (Achievement, ExResult, Turn, UserHelper, AdminNote) |
| `ExResult.java` | ~505 | Core data class: id, uak, uid, name, timestamp, exType, numValue, turns, RMSD, psycoins, emotion/energy levels |
| `ExResultSchulte.java` | ~25 | Extends ExResult with Schulte-specific fields (turns, turnsMissed, average, rmsd) |
| `ExResultBasics.java` | ~25 | Extends ExResult with Basics-specific fields |
| `ExResultSssr.java` | ~25 | Extends ExResult with SSSR-specific fields (lng01-lng03, flo01-flo03) |
| `ExType.java` | ~303 | Exercise type metadata loaded from `res/raw/ex_types.json`; tracks unlock requirements |
| `Achievement.java` | ~80 | Achievement records (ex type, record value, date) |
| `UserHelper.java` | ~100 | User profile: uid, uak, name, email, psycoins, seconds, hours, level |
| `Turn.java` | ~80 | Turn journal entry |
| `AdminNote.java` | ~40 | Admin messages from server |
| `Identifiable.java` | ~15 | Interface for entities with `getEntityKey()` and `getTimeStamp()` |
| `fbservices/` (7 files) | ~600 | Firebase helpers: Achievements sync, UserDbPreferences, ConditionEntry, FirestoreRepo |

### UI (`ui/`) — Activities, Fragments, View Models

| File | Lines | Role |
|---|---|---|
| `MainActivity.java` | ~390 | Central hub: nav setup, FAB exercise dispatch, in-app update check, onboarding |
| `SplashActivity.java` | ~60 | Launch screen: check auth → route to Login or Main |
| `LoginActivity.java` | ~200 | Email/password + Google sign-in |
| `SignupActivity.java` | ~120 | User registration |
| `SchulteActivity.java` | ~345 | Fullscreen Schulte table exercise with toolbar, grid, feedback dialog |
| `BasicsActivity.java` | ~200 | Basics visualization exercises |
| `SssrActivity.java` | ~300 | SSSR exercises |
| `InvestActivity.java` | ~200 | Unlock exercises (quiz/purchase) |
| `DashboardFragment.java` | ~100 | ViewPager container: State → Achievements → Results tabs |
| `DashboardFragment00State.java` | ~80 | User stats summary tab |
| `DashboardFragment01Achievements.java` | ~100 | Achievements list tab |
| `DashboardFragment02ExResult.java` | ~120 | Exercise results history tab |
| `DashboardPagerAdapter.java` | ~50 | ViewPager adapter for dashboard |
| `DashboardViewModel.java` | ~60 | Dashboard data holder |
| `HomeFragment.java` | ~80 | News/notifications feed |
| `HomeViewModel.java` | ~40 | News data holder |
| `NotificationsFragment.java` | ~60 | System notifications |
| `SchulteSettings.java` | ~200 | Schulte exercise configuration |
| `BasicSettings.java` | ~150 | Basics exercise configuration |
| `SssrSettings.java` | ~150 | SSSR exercise configuration |
| `SssrViewModel.java` | ~80 | SSSR data holder |
| `PrefsPopupFragment.java` | ~60 | Quick settings popup |
| `PrefsSettingsFragment.java` | ~100 | Full preferences screen |
| `PrefsChoiceFragment.java` | ~120 | Exercise space selection |
| `PrefsAboutFragment.java` | ~60 | About screen |
| `ExResultArrayAdapter.java` | ~200 | List adapter for exercise results + feedback dialog |
| `ExResultCardViewAdapter.java` | ~150 | CardView adapter for results |
| `RichEditorDialogFragment.java` | ~80 | Rich text note editor |
| `SpCalendarView.java` | ~100 | Custom calendar heatmap using MPAndroidChart |
| `TapTargetViewWr.java` | ~30 | Wrapper for onboarding tap targets |

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
| Unit tests | `app/src/test/` | 3 files | JUnit 4.13 |
| Instrumentation | `app/src/androidTest/` | 7 files | AndroidJUnitRunner, Espresso 3.5, Mockito 4.0 |

Key test targets:
- `DataOrmRepoTest` — local database CRUD operations
- `DataFirestoreRepositoryTest` — Firestore operations (requires emulator/device)
- `AchievementsFbDataTest` — Firebase achievements sync
- `LoginActivityTest` — Login UI flow
- `TilePavingTest` — Tile arrangement algorithm unit test

## Configuration Files

| File | Purpose |
|---|---|
| `build.gradle` (root) | Plugins: Firebase Crashlytics 2.9.9, Google Services 4.4.0, Kotlin 1.9.0, AGP 7.4.2 |
| `app/build.gradle` | SDK versions, 30+ dependencies, build types (debug/release), Firestore root path per variant |
| `gradle.properties` | JVM 2048m, parallel builds, AndroidX, R8 full mode disabled, config cache |
| `settings.gradle` | Repo config (Google, Maven Central, JitPack), module includes |
| `app/proguard-rules.pro` | Release obfuscation rules |
| `ormlite_config.txt` | Pre-generated ORMLite config for faster startup |

## Key Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| Firebase Firestore | 25.0.0 | Cloud database |
| Firebase Auth | 16.0.3 | Authentication |
| Firebase Crashlytics | 19.0.3 | Crash reporting |
| Firebase Analytics | 22.0.2 | Usage analytics |
| Firebase UI Auth | 7.2.0 | Auth UI components |
| ORMLite Android | 6.1 | Local SQLite ORM |
| Google Play Services Auth | 16.0.0 | Google sign-in |
| Navigation Component | 2.7.7 | Fragment navigation |
| MPAndroidChart | 3.1.0 | Charts (calendar heatmap) |
| Calendar View (kizitonwose) | 2.0.0 | Calendar UI |
| Glide | 4.11.0 | Image loading (GIF in Basics) |
| TapTargetView | 1.13.3 | Onboarding tutorial overlays |
| RichEditor | 2.0.0 | Rich text editing |
| Play App Update | 2.1.0 | In-app update prompts |
| Gson | 2.10.1 | JSON parsing |

## Build Types

| Variant | Firestore Root | Debuggable | Crashlytics |
|---|---|---|---|
| `debug` | `spdbs/dev/` | true | disabled |
| `release` | `spdbs/test/` | false | enabled |

Output APK: `Schulte Plus_{versionCode}_{versionName}.apk` (e.g., `Schulte Plus_116_Entada.apk`)

## Exercise Spaces by ID Prefix

| Prefix | Space | Activity | Settings |
|---|---|---|---|
| `gcb_sch` | Schulte tables | `SchulteActivity` | `SchulteSettings` |
| `gcb_bas` | Basics (visual) | `BasicsActivity` | `BasicSettings` |
| `gcb_sss` | SSSR | `SssrActivity` | `SssrSettings` |
| `gcb_space_schulte_parents` | Schulte Parents | (via SchulteActivity) | `Settings` (schulteparents) |

Exercise type metadata is loaded from `res/raw/ex_types.json` into `Map<String, ExType>` at startup. Each `ExType` tracks unlock requirements (achievements, psycoin purchase, certification quiz).
