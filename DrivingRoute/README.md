# AAOS Route Planner with OpenMapView

A modern Android Automotive OS (AAOS) route planning application built with Jetpack Compose and OpenMapView. Users can place start and finish markers on a fullscreen map, calculate routes using the public OSRM API, and view routes with automatic zoom-to-fit functionality.

![screenshot](screenshot.gif)

## Completed Features

- Fullscreen map display using OpenMapView
- Touch-based marker placement for start and finish points
- OSRM integration for route calculation with error handling
- Visual route display with blue polyline rendering
- Auto-zoom to fit both markers and route with 1.3x padding
- Green and red markers for start and finish points
- State-managed floating action button (Cancel)
- Toast notifications for route info (distance and duration)
- Complete error handling and network timeout management

### Target Platform
- Minimum SDK: API 34 (Android 14)
- Target SDK: API 35
- Compile SDK: API 36
- Optimized for Google Pixel Tablet emulator with AAOS image

## Architecture

### Modern Android Stack
- **Jetpack Compose** - Declarative UI framework
- **OpenMapView** - Custom map library for Android (replaces osmdroid)
- **MVVM Pattern** - ViewModel with StateFlow for reactive state management
- **Coroutines** - Async operations for network calls
- **Retrofit 3** - Type-safe HTTP client for OSRM API

### Core Components
1. **MainActivity** - ComponentActivity hosting Compose UI in DrivingRouteTheme
2. **MainScreen** - Composable UI with Scaffold, FloatingActionButton, and AndroidView for map integration
3. **MainViewModel** - State management with 6 StateFlows:
   - `appState`: Tracks interaction state through 4 states
   - `startMarker`: LatLng? for green start marker
   - `finishMarker`: LatLng? for red finish marker
   - `routePoints`: List<LatLng> for blue polyline
   - `routeInfo`: String? for distance/duration toast
   - `errorMessage`: String? for snackbar errors
4. **RouteRepository** - Retrofit setup with OkHttp, 10-second timeouts, logging interceptor
5. **OSRMService** - Retrofit interface for OSRM API calls
6. **OpenMapView Integration** - AndroidView wrapper with lifecycle observer, markers, polylines, and camera animations

### State Flow

```
IDLE → START_MARKER → FINISH_MARKER → ROUTE_DISPLAYED
  ↑        ↓              ↓              ↓
  └────── CANCEL ────── CANCEL ──────── CANCEL
```

- **IDLE**: No markers, Cancel FAB hidden, waiting for first tap
- **START_MARKER**: Green marker placed, Cancel FAB visible
- **FINISH_MARKER**: Red marker placed, route calculation starts
- **ROUTE_DISPLAYED**: Blue polyline shown, auto-zoom to fit, distance/duration toast

## Dependencies

The project uses Gradle version catalogs for dependency management. See `gradle/libs.versions.toml` for current versions.

### Key Libraries
- **OpenMapView** - Custom Android map library
- **Retrofit** - HTTP client for OSRM API
- **Compose BOM** - Jetpack Compose UI components
- **Material3** - Material Design 3 components
- **Coroutines** - Async operations
- **Lifecycle** - ViewModel and lifecycle-aware components
- **OkHttp Logging Interceptor** - HTTP request/response logging
- **Kotlin** - Primary language with JVM target 11

### Version Catalog Reference

Check `gradle/libs.versions.toml` for current dependency versions. Key sections:
- `[versions]` - Version numbers for all dependencies
- `[libraries]` - Library definitions with group/name/version references
- `[plugins]` - Gradle plugins (Android, Kotlin, Compose)

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

# Run instrumentation tests
./gradlew :automotive:connectedAndroidTest
```

### How to Use
1. **Launch the app** - Fullscreen map loads centered on Wolfsburg
2. **Place start marker** - Tap anywhere on the map to place green start marker
3. **Place finish marker** - Tap again to place red finish marker and calculate route
4. **View route** - Blue polyline appears with auto-zoom and distance/duration toast
5. **Reset** - Tap Cancel FAB to clear markers and route, return to IDLE state

### Network Requirements
- App requires internet connection for route calculation
- Uses public OSRM API: `https://router.project-osrm.org/route/v1/driving/`
- 10-second timeout configured for network requests

## File Structure

```
automotive/src/main/java/de/afarber/drivingroute/
├── MainActivity.kt                 # Compose entry point (18 lines)
├── model/
│   ├── AppState.kt                # 4-state enum for UI flow
│   ├── RoutePoint.kt              # Data class for route points
│   └── OSRMResponse.kt            # Data classes for OSRM API response
├── network/
│   ├── OSRMService.kt             # Retrofit service interface
│   └── RouteRepository.kt         # API client with OkHttp, timeouts, logging
├── ui/
│   ├── MainScreen.kt              # Compose UI with map, FAB, state handling
│   ├── MainViewModel.kt           # StateFlow-based state management
│   └── theme/                     # Color, Type, Theme definitions
└── utils/
    ├── PolylineDecoder.kt         # Google polyline algorithm (1e5 precision)
    └── MapUtils.kt                # Bounding box calculations with padding

automotive/src/main/res/
├── drawable/
│   └── ic_close.xml               # Cancel FAB icon
└── values/
    └── strings.xml                # App name and labels
```

## Implementation Details

### State Management (AppState.kt)
```kotlin
enum class AppState {
    IDLE,               // No markers, Cancel FAB hidden
    START_MARKER,       // Green marker placed, Cancel FAB visible
    FINISH_MARKER,      // Red marker placed, route calculation in progress
    ROUTE_DISPLAYED     // Blue polyline shown with auto-zoom
}
```

### Jetpack Compose Integration

**MainActivity.kt** - Simple entry point:
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrivingRouteTheme {
                MainScreen()
            }
        }
    }
}
```

**MainScreen.kt** - Manages UI and map integration using:
- `Scaffold` with `FloatingActionButton` and `SnackbarHost`
- `AndroidView` for OpenMapView integration
- `collectAsState()` to observe ViewModel StateFlows
- `LaunchedEffect` for side effects (toasts, snackbars)

**MainViewModel.kt** - Handles:
- Map click events and state transitions
- Route calculation with coroutines
- Error handling with Result<T> wrapper
- State exposure via StateFlow

### OpenMapView Integration

OpenMapView is integrated using `AndroidView` with proper lifecycle management:

```kotlin
AndroidView(
    factory = { ctx ->
        OpenMapView(ctx).apply {
            lifecycleOwner.lifecycle.addObserver(this)
            setZoom(15.0)
            setCenter(LatLng(52.4227, 10.7865))
            setOnMapClickListener { latLng -> onMapClick(latLng) }
        }
    },
    update = { mapView ->
        mapView.clearMarkers()
        mapView.clearPolylines()
        // Re-add markers and polylines based on current state
    }
)
```

**Key Points:**
- OpenMapView implements LifecycleObserver for proper lifecycle handling
- `update` block runs on every recomposition - must clear before re-adding
- Uses `BitmapDescriptorFactory.defaultMarker()` for colored markers
- Polyline strokeColor requires Int (not Compose Color)

### OSRM API Integration

**RouteRepository.kt** Implementation:
- **Base URL**: `https://router.project-osrm.org/`
- **Endpoint**: `route/v1/driving/{coordinates}`
- **Format**: `{lng1},{lat1};{lng2},{lat2}?overview=full&geometries=polyline`
- **Timeout**: 10 seconds for connect, read, and write operations
- **Logging**: HTTP body logging via OkHttp interceptor
- **Error Handling**: Result<T> wrapper for success/failure handling

### Polyline Processing
- Decodes OSRM polyline geometry using Google's algorithm
- Converts to `List<LatLng>` for OpenMapView rendering
- Handles precision scaling (1e5 factor) correctly
- Renders as blue polyline with 8f stroke width

## UI Implementation Details

### Floating Action Button
- **Position**: Bottom-right (end) with standard padding
- **Color**: Red background with white close icon
- **Visibility**: Hidden in IDLE state, visible in all other states
- **Behavior**: Clears all markers/routes and returns to IDLE state

### Map Configuration
- **Tile Source**: OpenStreetMap (default)
- **Initial Zoom**: Level 15.0
- **Initial Center**: Wolfsburg, Germany (52.4227, 10.7865)
- **Zoom Range**: 3.0-20.0 with multitouch enabled
- **Interaction**: Pan, zoom, pinch gestures fully functional

### Markers
- **Start Marker**: Green marker using `BitmapDescriptorFactory.HUE_GREEN`
- **Finish Marker**: Red marker using `BitmapDescriptorFactory.HUE_RED`
- **Placement**: Created via OpenMapView's Marker class
- **Lifecycle**: Cleared and re-added on state changes

### Route Polyline
- **Color**: Blue (defined as Int in theme/Color.kt)
- **Width**: 8f stroke
- **Auto-zoom**: 1.3x padding factor using MapUtils.createBoundingBoxWithPadding()
- **Animation**: Smooth camera transition via CameraUpdateFactory.newLatLngBounds()

## Testing & Quality

### Current Test Coverage
- **Unit Tests**: Located in `automotive/src/test/java/` (JUnit)
- **Instrumentation Tests**: Located in `automotive/src/androidTest/java/` (AndroidX Test + Espresso)
- **Manual Testing**: All core functionality verified on emulator

### Verified Functionality
1. **Happy Path**: Place Start → Place Finish → Calculate route → Auto-zoom works
2. **Cancel Operations**: Cancel works in all states (START_MARKER, FINISH_MARKER, ROUTE_DISPLAYED)
3. **Map Interactions**: Pan, zoom, pinch gestures functional
4. **Network Handling**: Route calculation with proper error handling and timeouts
5. **State Management**: All state transitions work correctly
6. **Auto-zoom**: Proper bounding box calculation with 1.3x padding
7. **Markers**: Green/red markers render correctly
8. **Route Display**: Blue polyline renders with distance/duration toast

### Performance Features

**Memory Management**
- Markers created via OpenMapView's BitmapDescriptorFactory
- Previous polylines cleared before adding new ones
- Map tile caching handled by OpenMapView

**Network Optimization**
- 10-second timeout implemented for all operations
- Proper error handling with user-friendly messages
- HTTP logging interceptor for debugging

**AAOS Optimization**
- Touch targets sized appropriately
- Landscape orientation optimized
- Proper lifecycle handling with LifecycleObserver

## HTTP Client Architecture Note

### Current Implementation: Retrofit + OkHttp

This project uses Retrofit 3.0.0 with OkHttp as the underlying HTTP engine. This is the standard, well-supported architecture for Android networking.

**Why OkHttp?**
- Native integration with Retrofit (default HTTP client)
- Excellent interceptor support (logging, authentication, etc.)
- Built-in connection pooling and caching
- Mature, battle-tested library
- First-class Kotlin coroutines support

**Alternative Considered: Ktor**

Ktor was evaluated as an alternative HTTP client. However, **Ktor is not compatible with Retrofit** because:
1. Retrofit is architecturally built on OkHttp - no Ktor adapter exists
2. They are competing HTTP frameworks, not complementary solutions
3. Retrofit's CallAdapter and Converter APIs expect OkHttp types

**If Ktor is desired:** Would require removing Retrofit entirely and implementing direct Ktor HttpClient calls with kotlinx.serialization. This would be a complete networking layer rewrite with no clear technical benefit over the current implementation.

**Recommendation:** Keep the current Retrofit + OkHttp architecture. It is idiomatic, performant, and well-maintained.

## Future Enhancement Opportunities
- Multiple route alternatives
- Offline map caching
- Voice guidance integration
- Route waypoint support
- Traffic-aware routing
