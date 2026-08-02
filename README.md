# Schulte Plus — Project Overview

**Version:** 116 "Entada" · **Platform:** Android (minSdk 26, targetSdk 35) · **Language:** Java
**Package:** `org.nebobrod.schulteplus` · **License:** Apache 2.0 · **Author:** Smart Rovers

---

## What is this?

A mobile app for cognitive training. Three exercise spaces — Schulte tables, visual illusions (Basics), and SSSR — with configurable difficulty, an achievement system, and in-game currency (psycoins). Data is synchronized between a local SQLite database and Firebase cloud storage.

## Exercise Spaces

| Space | ID Prefix | Description |
|---|---|---|
| **Schulte** | `gcb_sch*` | Schulte tables: 1–4 sequences, numbers/letters/colors, configurable grid 5×5…10×10 |
| **Basics** | `gcb_bas*` | Visual exercises: Necker cube, Penrose triangle, dot exercises, color circles, dancing silhouettes |
| **SSSR** | `gcb_sss*` | Time distribution training across life domains (work, rest, family, etc.) |
| **Schulte Parents** | `gcb_space_schulte_parents` | Exercises for parents with children |

## Code Organization

```
84 Java source files, ~40 XML layouts, 80+ resource drawables
```

### Three Architecture Layers:

**1. Exercise Engine (`common/`)** — 11 files
- `ExerciseRunner` — singleton dispatcher: holds user state, settings, starts and completes exercises, tracks statistics
- `STable` — Schulte table: grid of `SCell` objects, probabilistic distribution, shuffle, turn journal, result calculation (mean, RMSD)
- `Const` — interface with all preference keys, exercise IDs, achievement flags

**2. Data Layer (`data/`)** — 20 files
- Dual-repository pattern: `DataRepos` → `DataOrmRepo` (SQLite/ORMLite) + `DataFirestoreRepo` (Firestore). Write to both; resolve conflicts by timestamp on read
- 6 tables: `exresult`, `achievement`, `turn`, `userhelper`, `adminnote`
- `ExType` — exercise metadata from `res/raw/ex_types.json` (unlock conditions, cost, certification)

**3. UI (`ui/`)** — 30+ files
- `MainActivity` — app hub: bottom navigation (Dashboard / News / Settings / More), FAB exercise launcher, onboarding hints
- Three exercise activities: `SchulteActivity`, `BasicsActivity`, `SssrActivity` — fullscreen, with grid, toolbar, and result dialog
- Dashboard via ViewPager: state → achievements → result history
- Navigation via Android Navigation Component (`mobile_navigation.xml`)

### Authentication Flow:
`SplashActivity` → auto-login or `LoginActivity`/`SignupActivity` → `MainActivity`

## Technology Stack

| Component | Solution |
|---|---|
| Local DB | ORMLite 6.1 (SQLite) |
| Cloud DB | Firebase Firestore 25.0 |
| Authentication | Firebase Auth + Google Sign-In |
| Crash Reporting | Firebase Crashlytics |
| Charts | MPAndroidChart 3.1 |
| Calendar | Calendar View (kizitonwose) 2.0 |
| Images | Glide 4.11 (GIF animations in Basics) |
| Onboarding | TapTargetView 1.13 |
| Updates | Play App Update 2.1 |
| Navigation | Android Navigation 2.7.7 |
| UI Architecture | MVVM (ViewModel + LiveData) |
| Build | Gradle 7.x, AGP 7.4.2, viewBinding |

## Build

```bash
./gradlew assembleDebug        # debug build (Firestore: spdbs/dev/)
./gradlew assembleRelease      # release build (Firestore: spdbs/test/)
./gradlew test                 # unit tests (3 classes, JUnit 4)
./gradlew connectedAndroidTest # instrumentation tests (7 classes, Espresso + Mockito)
```

## Progression System

- **Psycoins** — in-game currency: earned from exercises (time × difficulty), spent to unlock new exercises
- **Level** — grows with accumulated practice hours: `level = √(totalHours)`
- **Achievements** — badges for records, hours, levels, consecutive training days
- Exercises may require: passing a quiz (`certRequired`), purchase with psycoins (`price`), or meeting specific conditions

## Key Features

- **Probabilistic distribution** of Schulte table cells — the `camelSurface()` function biases user attention toward a target area of the grid
- **Symbol types**: numbers, Roman numerals, Latin, Cyrillic, Devanagari, color gradients
- **Demo mode** — built-in user `AvtKMUW82...` for testing without registration
- **Russian localization** — full UI translation (`values-ru/`)
- **Dark theme** — `values-night/` with alternative palette
