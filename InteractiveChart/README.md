# InteractiveChart

Custom chart-view example demonstrating animated zoom, scrolling, flinging, and edge effects.

## Details

Gesture detectors convert touch movement into chart viewport changes. The overflow menu also offers explicit zoom and pan actions, and the view draws axes and a sample curve on its canvas.

Based on the Android guide [Animate a scroll gesture](https://developer.android.com/develop/ui/views/touch-and-input/gestures/scroll). Original Android Open Source Project Apache 2.0 notices remain in the source.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The minimum Android version is 6.0 (API 23); the app targets API 35. Dependencies are pinned in `gradle/libs.versions.toml`.

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest
./gradlew :app:installDebug
```

## Recording

After installing the app on `Automotive_1408p_landscape` (1408×792):

```sh
./record-gif.sh
```

Set `ANDROID_SERIAL` to select the emulator and `OPENMAPVIEW_DIR` if the OpenMapView checkout is elsewhere. The script uses its `scripts/capture-gif.sh` helper and FFmpeg, crops the system bars, and keeps `screenshot.gif` below 4 MiB. The original PNG is retained.

## Screenshot

![InteractiveChart screenshot](screenshot.gif)
