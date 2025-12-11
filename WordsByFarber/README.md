# Words by Farber

Android app that downloads and stores word dictionaries for multiple languages.

## Build and Run

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Run tests
./gradlew test

# Clean build
./gradlew clean assembleDebug
```

## Tech Stack

- **UI**: Jetpack Compose + Material 3
- **Navigation**: AndroidX Navigation 3
- **DI**: Koin
- **Network**: Ktor
- **Database**: Room (per-language db files)
- **Async**: Kotlin Coroutines + Flow
- **Preferences**: DataStore

## Architecture

### Startup Flow

```
Android System
      |
      v
WordsByFarberApplication     <-- Koin DI setup
      |
      v
MainActivity
      |
      v
AppNavigation                <-- Navigation 3
      |
      v
LanguageSelectionScreen      <-- First screen
```

### Navigation

```
LanguageSelection --> LoadingDictionary --> HomeScreen
                            |
                            v
                     FailedDownload (on error)
```

### Data Flow

```
User taps language
        |
        v
ViewModel.downloadDictionary()
        |
        v
Repository:
  1. HTTP GET dictionary JS file
  2. Extract JSON from "const HASHED={...}"
  3. Parse words
  4. Insert in batches of 1000
        |
        v
StateFlow updates UI with progress
        |
        v
Navigate to HomeScreen on success
```

### Download States

```
Idle --> Downloading --> Inserting(progress) --> Success
                   \
                    --> Error
```

### Project Structure

```
com.wordsbyfarber/
  WordsByFarberApplication.kt    Entry point
  MainActivity.kt                Single Activity
  di/AppModule.kt                Koin module
  data/
    model/Language.kt            Supported languages
    database/                    Room (WordEntity, WordDao, WordDatabase)
    repository/                  Network + DB operations
    preferences/                 DataStore
  viewmodel/DictionaryViewModel.kt
  ui/
    navigation/AppNavigation.kt  Routes + NavDisplay
    screens/                     Compose screens
    theme/                       Colors, typography
```

### Kotlin concepts

| Concept        | Example                                     | What it does                             |
| -------------- | ------------------------------------------- | ---------------------------------------- |
| data class     | data class Language(val code: String)       | Auto-generates equals/hashCode/copy      |
| object         | object SupportedLanguages                   | Singleton - one instance                 |
| sealed class   | sealed class DownloadState                  | All subclasses known at compile time     |
| Flow<T>        | fun getWordCount(): Flow<Int>               | Async stream of values                   |
| StateFlow<T>   | val downloadState: StateFlow<DownloadState> | Flow with current value                  |
| by             | val count by flow.collectAsState()          | Delegation - converts Flow to State      |
| @Composable    | @Composable fun HomeScreen()                | UI function that re-runs on state change |
| LaunchedEffect | LaunchedEffect(Unit) { ... }                | Runs coroutine when screen appears       |

### Per-Language Databases

Each language gets its own Room database file (`de.db`, `en.db`, etc.). Only one database is open at a time - switching languages closes the previous one.
