# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a fully implemented Android Automotive OS (AAOS) application that provides route planning using OpenStreetMap and OSRM. The app displays a fullscreen map where users can place start and finish markers, calculate routes, and view polyline overlays with automatic zoom-to-fit functionality.

## Build Commands

### Build and Run
```bash
./gradlew :automotive:assembleDebug    # Build debug APK
./gradlew :automotive:assembleRelease  # Build release APK
./gradlew :automotive:installDebug     # Install debug APK to connected device
```

### Testing
```bash
./gradlew :automotive:test                    # Run unit tests
./gradlew :automotive:connectedAndroidTest    # Run instrumentation tests
./gradlew :automotive:testDebugUnitTest       # Run debug unit tests only
```

### Code Quality
```bash
./gradlew :automotive:lintDebug        # Run lint checks
./gradlew :automotive:lintRelease      # Run lint for release build
```

### Clean
```bash
./gradlew clean                        # Clean all build artifacts
./gradlew :automotive:clean           # Clean automotive module only
```

## Architecture

### Core Components
- **MainActivity**: Single activity managing the entire app lifecycle, implements MapEventsReceiver
- **AppState**: Enum managing interaction states (IDLE → START_MARKER → FINISH_MARKER → ROUTE_DISPLAYED)
- **MapController**: Handles map interactions, marker placement, route rendering, and auto-zoom
- **RouteRepository**: Manages OSRM API calls using Retrofit with proper error handling

### State Management
The app uses a simple state machine:
```
IDLE → START_MARKER → FINISH_MARKER → ROUTE_DISPLAYED
  ↑        ↓              ↓              ↓
  └────── CANCEL ────── CANCEL ──────── CANCEL
```

### Key Libraries (Current Versions)
- **osmdroid 6.1.20**: OpenStreetMap rendering
- **Retrofit 3.0.0**: HTTP client for OSRM API
- **Coroutines 1.10.2**: Async operations
- **Material Design 1.13.0**: UI components
- **OkHttp 5.1.0**: HTTP logging interceptor

## Package Structure

```
de.afarber.drivingroute/
├── MainActivity.kt          # Main activity and state management
├── model/
│   ├── AppState.kt         # App state enum
│   ├── OSRMResponse.kt     # OSRM API response models
│   └── RoutePoint.kt       # Route point data class
├── network/
│   ├── OSRMService.kt      # Retrofit service interface
│   └── RouteRepository.kt  # Repository pattern for API calls
├── ui/
│   └── MapController.kt    # Map interaction and rendering
└── utils/
    ├── MapUtils.kt         # Map utility functions
    └── PolylineDecoder.kt  # Polyline geometry decoding
```

## Key Implementation Details

### Map Interaction
- Single-touch places markers in sequence (start → finish)
- MapEventsReceiver interface handles touch events in MainActivity:63-73
- Auto-zoom to fit both markers after route calculation using MapUtils:21-25
- Cancel FAB clears all markers and routes (MainActivity:95-102)
- Custom marker icons with green "S" for start, red "F" for finish (MapController:119-152)

### OSRM Integration
- Uses public OSRM endpoint: `https://router.project-osrm.org/route/v1/driving/`
- Polyline geometry decoded using Google's polyline algorithm (PolylineDecoder:7-44)
- Network calls handled with Kotlin coroutines and proper error handling (MainActivity:115-144)
- Retrofit with OkHttp logging interceptor for debugging (RouteRepository:15-32)

### State Management Implementation
- AppState enum with 4 states: IDLE, START_MARKER, FINISH_MARKER, ROUTE_DISPLAYED
- State transitions handled in MainActivity:152-156
- UI updates based on current state (MainActivity:158-167)
- Cancel button visibility managed per state

### Target Platform
- Minimum SDK: API 34 (Android 14)
- Target SDK: API 35
- Compile SDK: API 36
- Optimized for AAOS (Android Automotive OS)
- Google Pixel Tablet emulator with AAOS image

## Testing Strategy

### Unit Tests
- Located in `automotive/src/test/java/`
- Focus on business logic and data models
- Use JUnit 4.13.2

### Instrumentation Tests
- Located in `automotive/src/androidTest/java/`
- Test UI interactions and map functionality
- Use Espresso 3.6.1

### Manual Testing Scenarios
1. Happy path: Place start → place finish → calculate route
2. Cancel operations at each state
3. Network error handling
4. Invalid coordinates handling
5. Map gestures (pan, zoom, pinch)

## Development Notes

### Gradle Configuration
- Uses Kotlin DSL for build files
- Version catalog in `gradle/libs.versions.toml`
- Minimum SDK: 34, Target SDK: 35, Compile SDK: 36
- Java 11 compatibility
- Android Gradle Plugin: 8.11.1
- Kotlin: 2.2.10

### Dependencies Management
All dependencies are managed through the version catalog system. When adding new dependencies:
1. Add version to `gradle/libs.versions.toml`
2. Add library reference in `[libraries]` section
3. Reference in `automotive/build.gradle.kts` using `libs.` prefix

### Code Conventions
- Follow standard Kotlin conventions
- Use meaningful variable names and package structure
- Implement proper error handling with try-catch blocks
- Log important state transitions and errors using Android Log
- Use coroutines for all network operations