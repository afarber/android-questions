# PinchZoom

Custom board view demonstrating focal-point zooming, panning, and flinging with a transformation matrix.

## Details

GestureDetector and ScaleGestureDetector forward gestures to Board. Double-tapping runs a four-second scale animation, and the view saves and restores the board matrix across recreation.

The Matrix approach follows a suggestion by [pskink](https://stackoverflow.com/users/2252830/pskink).

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

![PinchZoom screenshot](screenshot.gif)
