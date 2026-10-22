# TestScroll3

Custom board-view experiment combining View scrolling, inertial flinging, and focal-point scaling.

## Details

The canvas scales the board while scrollTo constrains the view offsets. A gesture listener updates the scale around the touch focus, and OverScroller drives inertial movement through computeScroll.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![TestScroll3 screenshot](screenshot.png)
