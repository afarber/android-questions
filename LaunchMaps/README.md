# LaunchMaps

Java example launching an installed map application with a geo URI containing a labeled coordinate.

## Details

The button sends ACTION_VIEW for a geo: URI naming the point Treasure. It first checks that an installed application can handle the intent; it does not embed a map or calculate a route.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![LaunchMaps screenshot](screenshot.png)
