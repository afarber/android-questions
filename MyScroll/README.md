# MyScroll

ListView experiment appending one item per second and clearing the list with a button or double-tap.

## Details

A background task posts additions to the UI thread. The list is configured to stack from the bottom, and a gesture detector handles the double-tap clear action.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyScroll screenshot](screenshot.png)
