# MyDecoder

Word-game board experiment decoding individual letter images from sprite sheets with BitmapRegionDecoder.

## Details

Small and enlarged letter tiles are extracted from bitmap regions and cached by character. The custom view combines tile dragging with a Matrix-based board transform for panning and zooming.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyDecoder screenshot](screenshot.png)
