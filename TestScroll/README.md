# TestScroll

Custom board-view experiment using canvas translation and scaling for scrolling, flinging, and zooming.

## Details

GestureDetector and ScaleGestureDetector update explicit offsets and a scale value. OverScroller handles inertial movement, and double-tapping toggles the board's zoom level.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![TestScroll screenshot](screenshot.png)
