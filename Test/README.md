# Test

Minimal custom View drawing an eight-by-eight checkerboard.

## Details

The Activity constructs GameBoard directly and supplies gray and white colors. onDraw alternates rectangle colors, while onMeasure chooses a square view size.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![Test screenshot](screenshot.png)
