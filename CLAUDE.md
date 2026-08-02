# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests (JVM tests in app/src/test/)
./gradlew test

# Run a single unit test class
./gradlew test --tests "org.nebobrod.schulteplus.TilePavingTest"

# Run instrumentation tests (requires device/emulator, in app/src/androidTest/)
./gradlew connectedAndroidTest

# Lint check
./gradlew lint

# Clean
./gradlew clean
```

## Project Overview

**Schulte Plus** is an Android cognitive training app (package `org.nebobrod.schulteplus`). It offers multiple "exercise spaces" — Schulte tables, Basics (visual illusions), and SSSR — each with configurable difficulty, symbol types, and probabilistic cell distribution. The app tracks user stats, awards "psycoins," and syncs data between a local SQLite database and Firebase Firestore.

- Target: minSdk 26, compileSdk 34, targetSdk 35
- Language: Java (Kotlin is a build dependency only, not used in app code)
- AGP 7.4.2, Gradle 7.x, Kotlin 1.8.0
- `Utils` extends `Application` — serves as both the Application class and a static utility hub (context, time formatting, UUID generation, animations, dialogs)

## Architecture: Exercise Lifecycle

1. **`ExerciseRunner`** (singleton) holds all runtime state: current user, exercise config, and accumulated stats (psycoins, hours, level). It loads/saves state from `SharedPreferences` keyed by Firebase UID.
2. **`Exercise<T extends ExResult>`** is the abstract base. `STable` (Schulte Table) extends it — the grid of `SCell` objects with shuffle logic, turn journaling, and result calculation.
3. An exercise activity (`SchulteActivity`, `BasicsActivity`, `SssrActivity`) creates the `Exercise`, displays it via a `GridAdapter`, and records turns.
4. On completion, `ExerciseRunner.complete()` validates the result, updates stats, saves to both repos, and checks for achievements.

## Data Layer: Dual-Repository Pattern

Every data write goes to **both** local and cloud storage:

- **`DataRepos<TEntity>`** — facade; calls `DataOrmRepo` (ORMLite/SQLite local) then `DataFirestoreRepo` (Firestore cloud), resolving conflicts by timestamp.
- **`DataRepository<TEntity, TKey>`** — interface with CRUD (`create`, `read`, `update`, `delete`, `exists`).
- **`DatabaseHelper`** — ORMLite `OrmLiteSqliteOpenHelper`. Tables: `exresult`, `achievement`, `turn`, `userhelper`, `adminnote`. Uses `ormlite_config` raw resource for optimized startup. Schema upgrades rename tables → recreate → copy data.
- **`DataFirestoreRepo`** — wraps Firestore collection operations. DB root path varies by build type (`spdbs/dev/` debug, `spdbs/test/` release), read from `R.string.firestore_root`.
- Entity classes use `@DatabaseTable`/`@DatabaseField` (ORMLite) annotations and implement `Identifiable<String>` for Firestore document keys.

## Key Classes

| Class | Role |
|---|---|
| `Const` | Interface with ALL preference keys (`KEY_PRF_*`), exercise-type IDs (`gcb_sch_*`, `gcb_bas_*`, `gcb_sss_*`), achievement flags, intro bitflags |
| `ExerciseRunner` | Singleton: user state, preferences, exercise lifecycle (`start`/`complete`/`clear`), stat accumulation |
| `STable` | Schulte table: grid of `SCell` objects, probability distribution (`camelSurface`), shuffle, turn journal, result calculation |
| `ExResult` | Core data class stored in `exresult` table; polymorphic fields cover Schulte (turns, average, RMSD), Basics, and SSSR results |
| `ExType` | Exercise type metadata loaded from `res/raw/ex_types.json` — tracks unlock requirements (achievements/purchase/certification) |
| `DataRepos` | Write-through cache: saves to ORMLite then Firestore; reads resolve conflicts by timestamp |
| `Utils` | Application class + static utility methods (time formatting, UUID generation, animations, dialogs, screen metrics) |
| `MainActivity` | App hub: bottom nav (Dashboard/Home/Schulte settings/More), FAB launches exercises based on `ExerciseRunner.getExTypeId()`, in-app update check |
| `UserHelper` | User profile stored locally and in Firestore (uid, uak, name, email, stats) |

## Navigation & UI Flow

- **`SplashActivity`** → auto-login or **`LoginActivity`**/**`SignupActivity`** → **`MainActivity`**
- `MainActivity` uses Android Navigation Component with bottom nav:
  - **Dashboard** (`DashboardFragment`) — ViewPager of: user state, achievements, exercise results
  - **Home** (`HomeFragment`) — news/admin notes
  - **Schulte Settings** (`SchulteSettings`) — exercise configuration
  - **Plus** nested nav — choice fragment → settings for Basics, SSSR, Schulte Parents
- Exercise activities are fullscreen, separate from the nav graph
- `InvestActivity` handles unlocking exercises (quiz/purchase with psycoins)

## Exercise Spaces (by Const prefix)

- `gcb_sch*` — Schulte tables: single sequence, double red/blue, 4-color mishmash
- `gcb_bas*` — Basics: Necker illusions, dot exercises, color circles, dancing silhouettes
- `gcb_sss*` — SSSR: main exercise + intercept variants
- `gcb_space_schulte_parents` — parent-oriented exercises

Each space has its own `Settings` fragment and `Activity`. The "Play" FAB in `MainActivity` dispatches to the correct Activity based on the first 7 chars of `exTypeId`.

## Firebase/Auth

- Firebase Authentication (email/password + Google Sign-In via `play-services-auth` 16.0.0)
- Firestore root path set via `resValue` per build type
- Crashlytics disabled in debug builds (`ENABLE_CRASHLYTICS` build config field)
- Demo user: hardcoded default UID `AvtKMUW82OhFJnmRN97cTjmG8cs2` (signed out on activity stop)

## Preferences

User preferences are stored in `SharedPreferences` keyed by Firebase UID. `PreferenceManager.setDefaultValues` initializes defaults from `menu_preferences.xml`. Preference screens use AndroidX Preference library with XML definitions in `res/xml/preferences_*.xml`.

## Symbol Types

Schulte tables support multiple symbol types: numbers, Roman numerals, Latin/Cyrillic/Devanagari letters, and interpolated color gradients (red/blue). Defined in `Const.KEY_SYMBOL_TYPE_*` and configured in `res/values/arrays.xml`.
