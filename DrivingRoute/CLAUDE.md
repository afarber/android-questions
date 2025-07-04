# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android Automotive OS (AAOS) route planning application built with Kotlin. The app displays a fullscreen OpenStreetMap with draggable markers for route planning using the OSRM routing service.

**Target Platform**: Google Pixel Tablet emulator with AAOS image, API 34 (Android 14)

## Development Commands

Standard Android development commands for this project:

```bash
# Build the app
./gradlew assembleDebug

# Run tests
./gradlew test

# Install on device/emulator
./gradlew installDebug

# Clean build
./gradlew clean

# Lint check
./gradlew lint
```

## Architecture

### Core State Management
The app uses a simple state machine with 4 states:
- `IDLE` → `START_MARKER` → `FINISH_MARKER` → `ROUTE_DISPLAYED`
- All states can transition back to `IDLE` via Cancel button

### Key Components
1. **MainActivity** - Single activity hosting MapView and Cancel FAB
2. **MapController** - Manages map interactions, marker placement, zoom control
3. **RouteRepository** - Handles OSRM API calls and route data
4. **AppState** - Enum managing UI state transitions

### Data Flow
1. User touches map → Places marker → Updates AppState
2. Both markers placed → OSRM API call → Route calculation
3. Route response → Polyline rendering → Auto-zoom to fit markers

## Key Dependencies

- **osmdroid** (6.1.20) - OpenStreetMap Android library
- **Retrofit** (2.11.0) - HTTP client for OSRM API
- **Kotlin Coroutines** (1.8.1) - Async operations for network calls
- **Material Components** (1.12.0) - UI components and FAB
- **Lifecycle** (2.8.7) - ViewModel and lifecycle-aware components

## OSRM Integration

### API Endpoint
```
https://router.project-osrm.org/route/v1/driving/{start_lng},{start_lat};{finish_lng},{finish_lat}
```

### Response Structure
- Routes contain encoded polyline geometry
- Requires Google polyline algorithm decoding
- Precision factor: 1e5

## UI Specifications

### Map Configuration
- **Tile Source**: OpenStreetMap Mapnik
- **Initial Center**: Wolfsburg (10.7865, 52.4227)
- **Initial Zoom**: Level 15
- **Zoom Range**: 3-20

### Markers
- **Start Marker**: Green circle with "S" label
- **Finish Marker**: Red circle with "F" label  
- **Active Start**: Green circle with checkmark
- **Size**: 24dp diameter

### Route Display
- **Polyline Color**: Blue (#2196F3)
- **Width**: 8dp
- **Auto-zoom**: Fits both markers with appropriate padding

## File Structure

```
automotive/src/main/java/de/afarber/drivingroute/
├── MainActivity.kt
├── model/
│   ├── AppState.kt
│   ├── RoutePoint.kt
│   └── OSRMResponse.kt
├── network/
│   ├── OSRMService.kt
│   └── RouteRepository.kt
├── ui/
│   ├── MapController.kt
│   └── FloatingButtonManager.kt
└── utils/
    ├── PolylineDecoder.kt
    └── MapUtils.kt
```

## Testing Strategy

### Manual Testing Focus
- Touch gesture accuracy (marker placement)
- State transitions and button visibility
- Network error handling
- Auto-zoom behavior with various marker distances
- Cancel functionality at each state

### Test Scenarios
1. Happy path: Start marker → Finish marker → Route display
2. Cancel at each state
3. Network errors (airplane mode)
4. Edge cases (ocean coordinates, very close/distant markers)

