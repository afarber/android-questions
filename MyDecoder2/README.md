# MyDecoder2

Variant of the sprite-sheet word board that caches scaled letter bitmaps for small tiles and enlarged drag previews.

## Details

BitmapRegionDecoder extracts the letter regions, and cached scaled images are reused while drawing. The view keeps tile hit testing and dragging separate from the board's pan and zoom transform.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyDecoder2 screenshot](screenshot.png)
