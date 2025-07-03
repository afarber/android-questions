# AAOS OSM Route Planner

A simple Kotlin app for Android Automotive OS (AAOS) that displays draggable and zoomable OpenStreetMap (OSM) fullscreen with up to 2 markers and a Cancel floating action button (FAB) with route planning capabilities using OSRM.

## Project Overview

### Features
- Fullscreen OpenStreetMap display using osmdroid
- Touch-based marker placement for start and finish points
- OSRM integration for route calculation
- Visual route display with polyline rendering
- Auto-zoom to fit both markers and route
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

### build.gradle (Module: app)
```kotlin
dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // OSM
    implementation 'org.osmdroid:osmdroid-android:6.1.17'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
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
- [ ] Calculate bounding box for markers and route
- [ ] Implement smooth zoom animation
- [ ] Add appropriate padding for AAOS screen

## File Structure

```
app/src/main/java/de/afarber/aaosrouter/
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

app/src/main/res/
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
- **Initial Zoom**: Level 10
- **Initial Center**: Berlin (13.4050, 52.5200)
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
- **Cap**: Round
- **Join**: Round

## Testing Strategy

### Manual Testing Checklist
- [ ] Map loads and displays correctly
- [ ] Touch gestures work (pan, zoom, pinch)
- [ ] Single touch places markers accurately
- [ ] Button states transition correctly
- [ ] Cancel functionality clears state
- [ ] Route calculation succeeds
- [ ] Polyline renders properly
- [ ] Auto-zoom includes all elements
- [ ] Rotation handling (if supported)

### Test Scenarios
1. **Happy Path**: Place start marker → place finish → calculate route
2. **Cancel at Start**: Place Start marker → press Cancel FAB
3. **Cancel at Finish**: Place Start marker → Place Finish marker → press Cancel FAB
4. **Cancel after Route**: Complete happy path → press Cancel FAB
5. **Network Error**: Test with airplane mode
6. **Invalid Coordinates**: Test with ocean coordinates

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

