# HelloMoon

Fragment layout example showing the moon-landing image with Play and Stop controls.

## Details

The Activity hosts a fragment containing the image and two buttons. This sample stops at the UI: the playback controls have no click handlers or media player implementation.

From [Android Programming: The Big Nerd Ranch Guide](https://www.amazon.de/stores/Brian-Hardy/author/B00C9F5OTQ).

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![HelloMoon screenshot](screenshot.png)
