# Dependency Injection Setup (Hilt)

This document outlines the Hilt dependency injection setup for the WordsByFarber app.

## Overview

The app uses Dagger Hilt for dependency injection, providing a clean separation of concerns and easier testing. All dependencies are provided through Hilt modules and injected where needed.

## Modules

### 1. DatabaseModule.kt
- **Purpose**: Provides Room database instances and DAOs
- **Scope**: Singleton
- **Key Providers**:
  - `WordsDatabase` - Main database instance
  - `WordDao` - Data access for words
  - `PlayerDao` - Data access for players
  - `DatabaseProvider` - Multi-language database provider

### 2. NetworkModule.kt
- **Purpose**: Provides network-related dependencies
- **Scope**: Singleton
- **Key Providers**:
  - `OkHttpClient` - HTTP client with configured timeouts
  - `DictionaryDownloader` - Service for downloading dictionaries
  - `DictionaryParser` - Service for parsing JavaScript dictionaries

### 3. RepositoryModule.kt
- **Purpose**: Provides repository implementations
- **Scope**: Singleton
- **Key Providers**:
  - `SharedPreferences` - App preferences storage
  - `PreferencesRepository` - Wrapper for SharedPreferences
  - `DictionaryRepository` - Main repository for dictionary operations

### 4. UseCaseModule.kt
- **Purpose**: Provides use case implementations
- **Scope**: Singleton
- **Key Providers**:
  - `GetLanguagesUseCase` - Business logic for language operations
  - `SelectLanguageUseCase` - Business logic for language selection
  - `DownloadDictionaryUseCase` - Business logic for dictionary downloads
  - `SearchWordsUseCase` - Business logic for word searching
  - `GetWordsUseCase` - Business logic for word retrieval

### 5. AppModule.kt
- **Purpose**: Provides utility dependencies and aggregates all modules
- **Scope**: Singleton
- **Key Providers**:
  - `NetworkUtils` - Network connectivity utilities
  - `StringUtils` - String manipulation utilities

## Application Setup

### WordsByFarberApplication.kt
```kotlin
@HiltAndroidApp
class WordsByFarberApplication : Application()
```

### MainActivity.kt
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

## ViewModels

All ViewModels are annotated with `@HiltViewModel` and use constructor injection:

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val repository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel()
```

## Key Features

### Multi-Language Database Support
The `DatabaseProvider` interface allows for language-specific databases:
```kotlin
interface DatabaseProvider {
    fun getDatabase(languageCode: String): WordsDatabase
}
```

### Network Configuration
OkHttpClient is configured with appropriate timeouts:
- Download timeout: 5 minutes
- Read timeout: 1 minute
- Connection retry enabled

### SharedPreferences Wrapper
`PreferencesRepository` provides a clean interface for all preference operations:
- Language selection
- Download state tracking
- General preference storage

## Testing Support

### HiltTestModule.kt
Provides test-specific implementations:
- In-memory databases for testing
- Mock network components
- Test-specific configurations

## Usage in Screens

Screens use `hiltViewModel()` to get ViewModels:
```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel()
) {
    // Screen implementation
}
```

## Benefits

1. **Clean Architecture**: Clear separation between data, domain, and UI layers
2. **Testability**: Easy to mock dependencies for unit and integration tests
3. **Singleton Management**: Automatic lifecycle management for expensive objects
4. **Type Safety**: Compile-time dependency resolution
5. **Multi-Language Support**: Efficient handling of language-specific databases
6. **Performance**: Lazy initialization and proper scope management

## Build Configuration

### app/build.gradle.kts
- Hilt plugin enabled
- KAPT annotation processing enabled
- All required dependencies included

### Permissions
- `INTERNET` - For dictionary downloads
- `ACCESS_NETWORK_STATE` - For network connectivity checks