# Words by Farber

A multilingual word game application built with **Jetpack Compose** and **Kotlin**.

## Overview

Words by Farber is a stub application for 6 different word games supporting multiple languages. The app demonstrates modern Android development practices including streaming dictionary downloads, Room database persistence, and comprehensive testing.

## Features

### 🌍 Multi-language Support
- **6 supported languages**: German (de), English (en), French (fr), Dutch (nl), Polish (pl), Russian (ru)
- Full localization with language-specific resources
- Dynamic language switching with automatic dictionary management

### 📱 17 Comprehensive Screens
1. **Language Selection** - Choose your preferred language
2. **Loading Dictionary** - Streaming download with progress tracking
3. **Download Failed** - Error handling with retry functionality
4. **Home** - Main navigation hub
5. **Game 1** - Static 15x15 letter grid
6. **Game 2** - Static 5x5 letter grid
7. **Top Players** - Leaderboard with search functionality
8. **Your Profile** - User profile display
9. **Find a Word** - Word lookup with explanation
10. **2-letter Words** - Filtered word lists
11. **3-letter Words** - Filtered word lists
12. **Words with Rare Letter 1** - Language-specific rare letters
13. **Words with Rare Letter 2** - Language-specific rare letters
14. **Preferences** - App settings
15. **Help** - User assistance
16. **Privacy Policy** - Legal information
17. **Terms of Service** - Legal information

### 🚀 Advanced Technical Features

#### Memory-Efficient Dictionary Streaming
- **Streaming parser** using OkHttp and JsonReader for large dictionaries (3M+ words for Polish)
- **Curly bracket detection** with regex backtracking to handle split patterns across chunks
- **Progress tracking** with download (0-50%) and parsing (50-100%) phases
- **Word hashing** with ECHO salt for obfuscation (clear text for rare letters and short words)

#### Robust Data Architecture
- **Room database** for persistent storage with separate tables for words and players
- **Repository pattern** with clean separation of concerns
- **Use cases** for business logic encapsulation
- **SharedPreferences** limited to language and login only (enforced by tests)

#### Modern UI/UX
- **Jetpack Compose** with Material 3 design
- **Navigation Compose** for type-safe navigation
- **Reactive ViewModels** with proper lifecycle management
- **Custom components** including flag icons, letter grids, and search fields

## Architecture

### Clean Architecture Layers

```
├── UI Layer (Jetpack Compose)
│   ├── Screens (17 screens)
│   ├── Components (Reusable UI elements)
│   ├── ViewModels (State management)
│   └── Navigation (Compose Navigation)
├── Domain Layer
│   ├── Use Cases (Business logic)
│   └── Models (Domain entities)
└── Data Layer
    ├── Database (Room with DAO patterns)
    ├── Network (OkHttp with streaming)
    ├── Repository (Data source abstraction)
    └── Models (Data entities)
```

### Key Components

- **DictionaryStreamParser**: Memory-efficient streaming parser for large dictionary files
- **DownloadTracker**: Singleton for managing download states across app lifecycle
- **WordsDatabase**: Room database with proper migration support
- **DictionaryRepository**: Main data access layer with caching and error handling

## Dependencies

### Core Android
- **Jetpack Compose** with Material 3
- **Room Database** with KSP annotation processing
- **Navigation Compose** for screen navigation
- **ViewModel & Lifecycle** for reactive UI

### Networking & Data
- **OkHttp** for efficient HTTP streaming
- **Kotlin Coroutines** for asynchronous operations
- **Coil** for image loading (player photos)

### Testing
- **JUnit** for unit testing
- **Mockito** for mocking dependencies
- **Espresso & Compose Testing** for UI tests
- **Coroutines Test** for async testing

## Installation & Setup

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 24+ (minimum)
- Kotlin 1.9+

### Build & Run
```bash
git clone [repository-url]
cd WordsByFarber
./gradlew assembleDebug
./gradlew installDebug
```

### Testing
```bash
# Run unit tests
./gradlew testDebug

# Run instrumentation tests
./gradlew connectedAndroidTest

# Generate test coverage
./gradlew testDebugUnitTestCoverage
```

## Dictionary Format & Processing

### Dictionary Structure
- **JavaScript files** hosted at wordsbyfarber.com (e.g., `Consts-en.js`)
- **Word hashing** using MD5 with ECHO salt for obfuscation
- **Clear text words** for:
  - 2-3 letter words
  - Words containing language-specific rare letters (Q, Y for German; Ń, Ź for Polish, etc.)
- **Optional explanations** for educational value

### Processing Pipeline
1. **Streaming download** in 8KB chunks
2. **Pattern detection** for `const HASHED = {` with regex backtracking
3. **JSON parsing** with Android's JsonReader for memory efficiency
4. **Database insertion** with batch operations for performance
5. **Progress updates** every 1000 entries to avoid UI blocking

## Testing Strategy

### Comprehensive Test Coverage
- **Unit Tests**: Repository patterns, use cases, ViewModels, utilities
- **Integration Tests**: Database operations, network streaming, SharedPreferences constraints
- **UI Tests**: Screen navigation, user interactions, compose components
- **End-to-End Tests**: Complete user flows from language selection to word lookup

### Key Test Areas
- **SharedPreferences validation** ensuring only `language` and `login` keys
- **Dictionary streaming** with malformed data handling
- **Language switching** with proper cleanup and reinitialization
- **Download cancellation** and retry mechanisms
- **Memory efficiency** during large dictionary processing

## Performance Optimizations

- **Streaming architecture** prevents memory exhaustion with large dictionaries
- **Database indexing** for fast word lookups and filtering
- **Lazy loading** of UI components and images
- **Coroutine scoping** for proper resource management
- **Progress tracking** without blocking main thread

## Future Enhancements

The current implementation serves as a robust foundation for:
- Actual word game mechanics (currently static grids)
- User authentication (Google/Amazon/Huawei login)
- Multiplayer functionality
- Advanced word scoring algorithms
- Offline gameplay modes

## License

This project demonstrates modern Android development practices and serves as a comprehensive example of clean architecture implementation.