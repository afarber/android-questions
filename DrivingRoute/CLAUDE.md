# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android Automotive OS (AAOS) application that provides route planning functionality using OpenStreetMap and OSRM routing service. The app displays a fullscreen map where users can place start and finish markers by tapping, then calculates and displays the route between them.

## Build Commands

### Standard Android Development Commands
```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Install debug APK to connected device
./gradlew installDebug
```

### Linting and Code Quality
```bash
# Run lint checks
./gradlew lint

# Run Kotlin linter (if ktlint is configured)
./gradlew ktlintCheck

# Auto-fix Kotlin formatting issues
./gradlew ktlintFormat
```

## Architecture

### State Management
The app uses a simple state machine pattern with `AppState` enum:
- `IDLE`: No markers placed, waiting for user input
- `START_MARKER`: Start marker placed, waiting for finish marker
- `FINISH_MARKER`: Both markers placed, ready for route calculation
- `ROUTE_DISPLAYED`: Route calculated and displayed

### Key Components
- **MainActivity**: Main activity implementing `MapEventsReceiver` for map touch handling
- **MapController**: Manages map operations, markers, and route display
- **RouteRepository**: Handles OSRM API communication using Retrofit
- **AppState**: Enum defining application states
- **PolylineDecoder**: Utility for decoding OSRM polyline geometry

### Data Flow
1. User taps map → MainActivity handles touch via `MapEventsReceiver`
2. State transitions managed in `handleMapTap()`
3. Route calculation triggered when finish marker is placed
4. OSRM API called via `RouteRepository`
5. Route polyline decoded and displayed via `MapController`

## Key Dependencies

### Core Libraries
- **osmdroid**: 6.1.20 - OpenStreetMap display
- **Retrofit**: 2.11.0 - HTTP client for OSRM API
- **Kotlin Coroutines**: 1.8.1 - Async operations
- **Material Design**: 1.12.0 - UI components

### Target Platform
- **Min SDK**: 34 (Android 14)
- **Target SDK**: 35
- **Compile SDK**: 35
- **Java Version**: 11

## File Structure

```
automotive/src/main/java/de/afarber/drivingroute/
├── MainActivity.kt           # Main activity with map event handling
├── model/
│   ├── AppState.kt          # Application state enum
│   ├── OSRMResponse.kt      # API response data classes
│   └── RoutePoint.kt        # Route point data model
├── network/
│   ├── OSRMService.kt       # Retrofit service interface
│   └── RouteRepository.kt   # Network operations repository
├── ui/
│   └── MapController.kt     # Map operations and marker management
└── utils/
    ├── MapUtils.kt          # Map utility functions
    └── PolylineDecoder.kt   # OSRM polyline decoder
```

## Configuration

### Map Configuration
- Initial center: Wolfsburg (52.4227, 10.7865)
- Initial zoom: Level 15
- Tile source: OpenStreetMap Mapnik
- Multi-touch controls enabled

### OSRM Integration
- Base URL: `https://router.project-osrm.org/`
- Endpoint: `/route/v1/driving/{coordinates}`
- Response format: JSON with encoded polyline geometry

## Development Notes

### Working with Maps
- Map initialization handled in `MapController.setupMap()`
- Markers created programmatically with custom icons
- Route polylines added at index 0 to draw under markers
- Auto-zoom implemented using bounding box calculation

### State Management
- State transitions only occur in `MainActivity.updateState()`
- UI updates triggered by state changes in `updateUI()`
- Cancel button visibility tied to current state

### Network Operations
- All API calls use coroutines with proper error handling
- Repository pattern isolates network logic from UI
- Route calculation errors displayed as Toast messages

### Testing
- Unit tests in `automotive/src/test/`
- Instrumented tests in `automotive/src/androidTest/`
- Test target: Google Pixel Tablet emulator with AAOS image