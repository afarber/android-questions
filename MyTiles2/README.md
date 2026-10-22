# MyTiles2

Word-board variant combining grid-snapped tile dragging with constrained scrolling, flinging, and zooming.

## Details

The custom view tracks occupied cells and uses an enlarged tile while dragging. Pan and zoom calculations constrain the board to the view, with compatibility checks around gesture features on older Android versions.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyTiles2 screenshot](screenshot.png)
