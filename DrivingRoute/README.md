# AAOS OSM Route Planner

A simple Kotlin app for Android Automotive OS (AAOS) that displays draggable and zoomable OpenStreetMap (OSM) fullscreen with up to 2 markers and a Cancel floating action button (FAB) with route planning capabilities using public OSRM backend.

![screenshot](https://raw.github.com/afarber/android-newbie/master/DrivingRoute/screenshot.gif)

## Project Overview

### Features
- Fullscreen OpenStreetMap display using osmdroid
- Touch-based marker placement for start and finish points
- OSRM integration for route calculation
- Visual route display with polyline rendering
- Auto-zoom to fit both markers (Start and Finish)
- Clean floating action button interface

### Target Platform
- Google Pixel Tablet emulator with AAOS image
- Target SDK: API 34 (Android 14)
- For simplicity, lower Android versions are not supported

## Architecture

### Core Components
1. **MainActivity** - Single activity with fullscreen MapView
2. **AppState** - Enum managing interaction states
3. **RouteManager** - Handles OSRM API calls and polyline rendering
4. **MapController** - Manages map interactions and markers

### State Flow
```
IDLE → START_MARKER → FINISH_MARKER → ROUTE_DISPLAYED
  ↑        ↓              ↓              ↓
  └────── CANCEL ────── CANCEL ──────── CANCEL
```

## Dependencies

### Key Libraries (Version Catalog)
The project uses Gradle version catalogs for dependency management. Current versions:

- **osmdroid**: 6.1.20 - OpenStreetMap Android library
- **Retrofit**: 2.11.0 - HTTP client for OSRM API
- **Material Design**: 1.12.0 - UI components and FAB
- **Coroutines**: 1.8.1 - Async operations for network calls
- **Lifecycle**: 2.8.7 - ViewModel and lifecycle-aware components

### gradle/libs.versions.toml
```toml
[versions]
osmdroid = "6.1.20"
retrofit = "2.11.0"
material = "1.12.0"
coroutines = "1.8.1"
lifecycle = "2.8.7"

[libraries]
osmdroid-android = { group = "org.osmdroid", name = "osmdroid-android", version.ref = "osmdroid" }
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
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

## Implementation Plan

### Phase 1: Basic Map Setup
- [ ] Create MainActivity with fullscreen MapView
- [ ] Configure osmdroid with basic settings
- [ ] Set initial map center and zoom level
- [ ] Test touch interactions (pan/zoom)

### Phase 2: State Management
- [ ] Create AppState enum
- [ ] Implement state transition logic
- [ ] Add floating action buttons with proper positioning
- [ ] Handle button visibility based on state

### Phase 3: Marker Placement
- [ ] Implement single-touch marker placement
- [ ] Create custom marker icons for start/finish
- [ ] Handle marker state changes (start → active start)
- [ ] Clear markers on cancel

### Phase 4: Route Integration
- [ ] Create OSRM service interface
- [ ] Implement HTTP client with Retrofit
- [ ] Parse polyline geometry from OSRM response
- [ ] Render polyline on map

### Phase 5: Auto-zoom
- [ ] Calculate bounding box for both markers (Start and Finish)
- [ ] Use BoundingBox.increaseByScale() for padding
- [ ] Implement smooth zoom animation
- [ ] Add appropriate padding for AAOS screen

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

automotive/src/main/res/
├── layout/
│   └── activity_main.xml
├── drawable/
│   ├── ic_start.xml
│   ├── ic_finish.xml
│   ├── ic_route.xml
│   └── ic_cancel.xml
└── values/
    ├── colors.xml
    ├── dimens.xml
    └── strings.xml
```

## Key Classes Overview

### AppState.kt
```kotlin
enum class AppState {
    IDLE,               // Both markers and Cancel FAB are hidden, waiting for first user touch
    START_MARKER,       // Start marker has been placed and is shown, Cancel FAB is shown
    FINISH_MARKER,      // Finish marker placed and shown, Start marker is shown, Cancel FAB is shown
    ROUTE_DISPLAYED     // Both markers and Cancel FAB are shown, route calculated and drawn as polyline
}
```

### MainActivity.kt
- Single activity managing the entire app lifecycle
- Hosts MapView and the Cancel FAB
- Coordinates between MapController and RouteManager

### MapController.kt
- Handles all map-related operations
- Manages marker placement and removal
- Controls map zoom and centering

### RouteRepository.kt
- Encapsulates OSRM API communication
- Handles network requests with proper error handling
- Manages response parsing and caching

## OSRM Integration

### API Endpoint
```
https://router.project-osrm.org/route/v1/driving/{start_lng},{start_lat};{finish_lng},{finish_lat}
```

### Expected Response Structure
```json
{
  "routes": [
    {
      "geometry": "encoded_polyline_string",
      "duration": 1234.5,
      "distance": 5678.9
    }
  ]
}
```

### Polyline Decoding
- Use standard Google polyline algorithm
- Convert to List<GeoPoint> for osmdroid
- Handle precision scaling (factor 1e5)

## UI Specifications

### Floating Action Button
- **Position**: Right side of screen, vertically centered
- **Spacing**: 16dp between buttons
- **Size**: 56dp diameter (standard red FAB with white cross)
- **Elevation**: 6dp
- **Colors**: Material Design primary/secondary

### Map Configuration
- **Tile Source**: OpenStreetMap Mapnik
- **Initial Zoom**: Level 15
- **Initial Center**: Wolfsburg (10.7865, 52.4227)
- **Min/Max Zoom**: 3-20
- **Multitouch**: Enabled for AAOS gestures

### Markers
- **Start Marker**: Green circle with "S" label
- **Finish Marker**: Red circle with "F" label
- **Active Start**: Green circle with checkmark
- **Size**: 24dp diameter

### Route Polyline
- **Color**: Blue (#2196F3)
- **Width**: 8dp

### Auto-zoom Behavior
- **Trigger**: After route calculation and polyline rendering
- **Target**: Bounding box containing both start and finish markers
- **Padding**: Use BoundingBox.increaseByScale() with appropriate scale factor
- **Animation**: Smooth zoom transition

## Testing Strategy

### Manual Testing Checklist
- [ ] Map loads and displays correctly
- [ ] Touch gestures work (pan, zoom, pinch)
- [ ] Single touch places markers accurately
- [ ] Button states transition correctly
- [ ] Cancel functionality clears state
- [ ] Route calculation succeeds
- [ ] Polyline renders properly
- [ ] Auto-zoom shows both markers clearly
- [ ] Rotation handling (if supported)

### Test Scenarios
1. **Happy Path**: Place Start marker → place Finish marker → calculate route → verify auto-zoom
2. **Cancel at Start**: Place Start marker → press Cancel FAB
3. **Cancel at Finish**: Place Start marker → Place Finish marker → press Cancel FAB
4. **Cancel after Route**: Complete happy path → press Cancel FAB
5. **Network Error**: Test with airplane mode
6. **Invalid Coordinates**: Test with ocean coordinates
7. **Close Markers**: Test auto-zoom with markers very close together
8. **Distant Markers**: Test auto-zoom with markers far apart

## Performance Considerations

### Memory Management
- Recycle bitmaps for custom markers
- Clear previous polylines before adding new ones
- Use appropriate map tile cache settings

### Network Optimization
- Implement request timeout (10 seconds)
- Add retry logic for failed requests (max 3 retries)
- Consider caching route responses

### AAOS Optimization
- Use appropriate touch target sizes (48dp minimum)
- Optimize for landscape orientation
- Handle automotive-specific lifecycle events

## Development Notes

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Keep functions small and focused
- Add inline comments for complex logic

### Error Handling
- Graceful degradation for network failures
- User-friendly error messages
- Proper exception logging

### Future Enhancements
- Voice guidance integration
- Multiple route options
- Traffic-aware routing
- Offline map support
- Route history
