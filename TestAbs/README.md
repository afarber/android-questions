# TestAbs

Legacy AbsoluteLayout experiment positioning and dragging custom letter-tile child views.

## Details

SmallTile and BigTile combine an image with letter and score labels. The parent places child views at random coordinates and changes their AbsoluteLayout parameters during a drag.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![TestAbs screenshot](screenshot.png)
