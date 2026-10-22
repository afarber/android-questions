# DragAndDraw

Custom drawing-view example creating translucent rectangles from touch drags alongside sample letter tiles.

## Details

A touch starts a box and movement changes its opposite corner. Previously drawn boxes remain in the collection and are repainted on the canvas together with the tile examples.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![DragAndDraw screenshot](screenshot.png)
