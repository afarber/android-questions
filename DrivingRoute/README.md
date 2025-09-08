# AAOS OSM Route Planner

A fully implemented Kotlin app for Android Automotive OS (AAOS) that displays a fullscreen OpenStreetMap with touch-based route planning. Users can place start and finish markers, calculate routes using the public OSRM API, and view routes with automatic zoom-to-fit functionality.

![screenshot](https://raw.github.com/afarber/android-newbie/master/DrivingRoute/screenshot.gif)

## ✅ Completed Features

- ✅ Fullscreen OpenStreetMap display using osmdroid 6.1.20
- ✅ Touch-based marker placement for start and finish points
- ✅ OSRM integration for route calculation with error handling
- ✅ Visual route display with blue polyline rendering
- ✅ Auto-zoom to fit both markers and route with 1.3x padding
- ✅ Custom circular markers (green "S" for start, red "F" for finish)
- ✅ State-managed floating action button (Cancel)
- ✅ Toast notifications for route info (distance and duration)
- ✅ Complete error handling and network timeout management

### Target Platform
- Minimum SDK: API 34 (Android 14)
- Target SDK: API 35
- Compile SDK: API 36
- Optimized for Google Pixel Tablet emulator with AAOS image

## Architecture

### Core Components
1. **MainActivity** - Single activity with fullscreen MapView, implements MapEventsReceiver
2. **AppState** - Enum managing 4 interaction states with proper transitions
3. **RouteRepository** - Handles OSRM API calls using Retrofit with error handling
4. **MapController** - Manages map interactions, markers, and auto-zoom functionality
5. **PolylineDecoder** - Decodes OSRM polyline geometry using standard algorithm
6. **MapUtils** - Utility functions for bounding box calculations with padding

### State Flow (Implemented)
```
IDLE → START_MARKER → FINISH_MARKER → ROUTE_DISPLAYED
  ↑        ↓              ↓              ↓
  └────── CANCEL ────── CANCEL ──────── CANCEL
```

- **IDLE**: No markers, Cancel FAB hidden, waiting for first tap
- **START_MARKER**: Green "S" marker placed, Cancel FAB visible
- **FINISH_MARKER**: Red "F" marker placed, route calculation starts
- **ROUTE_DISPLAYED**: Blue polyline shown, auto-zoom to fit, distance/duration toast

## Dependencies

### Key Libraries (Current Versions)
The project uses Gradle version catalogs for dependency management. Current versions:

- **osmdroid**: 6.1.20 - OpenStreetMap Android library
- **Retrofit**: 3.0.0 - HTTP client for OSRM API  
- **Material Design**: 1.13.0 - UI components and FAB
- **Coroutines**: 1.10.2 - Async operations for network calls
- **Lifecycle**: 2.9.3 - ViewModel and lifecycle-aware components
- **OkHttp**: 5.1.0 - HTTP logging interceptor
- **Android Gradle Plugin**: 8.11.1
- **Kotlin**: 2.2.10

### gradle/libs.versions.toml (Current)
```toml
[versions]
agp = "8.11.1"
kotlin = "2.2.10"
osmdroid = "6.1.20"
retrofit = "3.0.0"
material = "1.13.0"
coroutines = "1.10.2"
lifecycle = "2.9.3"
loggingInterceptor = "5.1.0"

[libraries]
osmdroid-android = { group = "org.osmdroid", name = "osmdroid-android", version.ref = "osmdroid" }
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "loggingInterceptor" }
```

### automotive/build.gradle.kts
```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    
    // OSM
    implementation(libs.osmdroid.android)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
```

### Permissions (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## Build & Usage

### Prerequisites
- Android Studio (latest version)
- Android Automotive OS emulator or physical device
- Internet connection for OSRM API calls

### Building the App
```bash
# Build debug APK
./gradlew :automotive:assembleDebug

# Install to connected device/emulator
./gradlew :automotive:installDebug

# Run tests
./gradlew :automotive:test
```

### How to Use
1. **Launch the app** - Fullscreen map loads centered on Wolfsburg
2. **Place start marker** - Tap anywhere on the map to place green "S" marker
3. **Place finish marker** - Tap again to place red "F" marker and calculate route
4. **View route** - Blue polyline appears with auto-zoom and distance/duration toast
5. **Reset** - Tap Cancel FAB to clear markers and route, return to IDLE state

### Network Requirements
- App requires internet connection for route calculation
- Uses public OSRM API: `https://router.project-osrm.org/route/v1/driving/`
- 10-second timeout configured for network requests

## File Structure (Implemented)

```
automotive/src/main/java/de/afarber/drivingroute/
├── MainActivity.kt              # Main activity, MapEventsReceiver implementation
├── model/
│   ├── AppState.kt             # 4-state enum (IDLE → START_MARKER → FINISH_MARKER → ROUTE_DISPLAYED)
│   ├── RoutePoint.kt           # Data class for route points
│   └── OSRMResponse.kt         # Data classes for OSRM API response parsing
├── network/
│   ├── OSRMService.kt          # Retrofit service interface for OSRM API
│   └── RouteRepository.kt      # Repository with OkHttp logging and error handling
├── ui/
│   └── MapController.kt        # Map management, custom markers, auto-zoom
└── utils/
    ├── PolylineDecoder.kt      # Google polyline algorithm implementation
    └── MapUtils.kt             # Bounding box utilities with padding

automotive/src/main/res/
├── layout/
│   └── activity_main.xml       # Fullscreen MapView + Cancel FAB
├── drawable/
│   └── ic_close.xml           # Cancel FAB icon
└── values/
    ├── colors.xml             # Green/Red marker colors, blue route color
    ├── strings.xml            # App name and labels
    └── themes.xml             # Material theme configuration
```

## Implementation Details

### State Management (AppState.kt)
```kotlin
enum class AppState {
    IDLE,               // No markers, Cancel FAB hidden
    START_MARKER,       // Green "S" marker placed, Cancel FAB visible
    FINISH_MARKER,      // Red "F" marker placed, route calculation starts
    ROUTE_DISPLAYED     // Blue polyline shown with auto-zoom
}
```

### Key Implementation Files

**MainActivity.kt** (187 lines)
- Implements MapEventsReceiver for touch handling
- Manages state transitions and UI updates
- Handles route calculation with coroutines and error handling
- Displays toast notifications for route info

**MapController.kt** (156 lines)
- Creates custom circular markers with Canvas drawing
- Handles polyline rendering and auto-zoom functionality
- Manages map overlays and invalidation

**RouteRepository.kt** (49 lines)
- Retrofit setup with OkHttp logging interceptor
- 10-second timeout configuration
- Proper Result<T> error handling

**PolylineDecoder.kt** (45 lines)
- Standard Google polyline algorithm implementation
- Converts OSRM encoded geometry to List<GeoPoint>

## OSRM API Integration

### Live Implementation
- **Endpoint**: `https://router.project-osrm.org/route/v1/driving/`
- **Format**: `{lng1},{lat1};{lng2},{lat2}?overview=full&geometries=polyline`
- **Response**: JSON with routes array containing geometry, duration, distance
- **Timeout**: 10 seconds for connect/read/write operations
- **Error Handling**: Network failures show user-friendly toast messages

### Polyline Processing
- Decodes OSRM polyline geometry using Google's algorithm
- Converts to `List<GeoPoint>` for osmdroid rendering
- Handles precision scaling (1e5 factor) correctly
- Renders as blue polyline with 8dp stroke width

## UI Implementation Details

### Floating Action Button (Implemented)
- **Position**: Right side, vertically centered with 16dp end margin
- **Color**: Red (#F44336) background with white close icon
- **Visibility**: Hidden in IDLE state, visible in all other states
- **Behavior**: Clears all markers/routes and returns to IDLE state

### Map Configuration (Live)
- **Tile Source**: OpenStreetMap Mapnik
- **Initial Zoom**: Level 15.0
- **Initial Center**: Wolfsburg (52.4227, 10.7865)
- **Zoom Range**: 3.0-20.0 with multitouch enabled
- **Interaction**: Pan, zoom, pinch gestures fully functional

### Custom Markers (Canvas-drawn)
- **Start Marker**: 72px green circle (#4CAF50) with white "S" text
- **Finish Marker**: 72px red circle (#F44336) with white "F" text
- **Design**: White border, bold text, centered anchor point
- **Placement**: Created dynamically with Canvas/Bitmap

### Route Polyline (Auto-zoom)
- **Color**: Blue (#2196F3)
- **Width**: 8dp stroke with rounded caps/joins
- **Auto-zoom**: 1.3x padding factor using BoundingBox.increaseByScale()
- **Animation**: Smooth zoom transition after route calculation

## Testing & Quality

### Current Test Coverage
- **Unit Tests**: Located in `automotive/src/test/java/` (basic test structure in place)
- **Instrumentation Tests**: Located in `automotive/src/androidTest/java/` (basic test structure in place)
- **Manual Testing**: All core functionality verified on emulator

### Verified Functionality ✅
1. ✅ **Happy Path**: Place Start → Place Finish → Calculate route → Auto-zoom works
2. ✅ **Cancel Operations**: Cancel works in all states (START_MARKER, FINISH_MARKER, ROUTE_DISPLAYED)
3. ✅ **Map Interactions**: Pan, zoom, pinch gestures functional
4. ✅ **Network Handling**: Route calculation with proper error handling and timeouts
5. ✅ **State Management**: All state transitions work correctly
6. ✅ **Auto-zoom**: Proper bounding box calculation with 1.3x padding
7. ✅ **Custom Markers**: Green/red circular markers render correctly
8. ✅ **Route Display**: Blue polyline renders with distance/duration toast

### Performance Features

**Memory Management**
- Custom markers use Canvas/Bitmap with proper resource handling
- Previous polylines cleared before adding new ones
- Map tile caching handled by osmdroid

**Network Optimization**  
- ✅ 10-second timeout implemented for all operations
- ✅ Proper error handling with user-friendly toast messages
- ✅ HTTP logging interceptor for debugging

**AAOS Optimization**
- ✅ Touch targets sized appropriately (72px markers, 56dp FAB)
- ✅ Landscape orientation optimized
- ✅ Proper lifecycle handling (onResume/onPause/onDestroy)

## Future Enhancement Opportunities
- Multiple route alternatives
- Offline map caching
- Voice guidance integration  
- Route waypoint support
- Traffic-aware routing
