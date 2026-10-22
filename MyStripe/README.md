# MyStripe

Word-board experiment drawing draggable letter tiles from regions of horizontal sprite strips.

## Details

The tile classes select source rectangles from small and large English-letter bitmaps. A Matrix transforms the board, while an enlarged tile image is used for drag feedback.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyStripe screenshot](screenshot.png)
