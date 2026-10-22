# MyMask

Canvas experiment drawing a bitmap overlay on a word-game board.

## Details

The active drawing path paints the board and then an ARGB bitmap containing a green rectangle. The PorterDuff DST_IN masking experiment is commented out in the source.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyMask screenshot](screenshot.png)
