# NavigationDrawerExample

Two-sided navigation-drawer example with a planet list on the left and music actions on the right.

## Details

The toolbar toggles the drawers, planet selection replaces the content fragment, and right-drawer actions are sent to the fragment through LocalBroadcastManager. List rows include icons.

Adapted from the Android Open Source Project navigation-drawer example. Original Apache 2.0 notices remain in the source.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![NavigationDrawerExample screenshot](screenshot.png)
