# TestScroll2

Board-view experiment combining a transformation matrix, tile dragging, and an enlarged drag preview.

## Details

The custom view tracks small tiles over the board and maps touches into its transformed coordinates. A larger tile copies the selected tile's letter and value while it is dragged.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![TestScroll2 screenshot](screenshot.png)
