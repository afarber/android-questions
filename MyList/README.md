# MyList

Fragment-based list picker preserving the selected item index in SharedPreferences.

## Details

A button opens a single-choice ListFragment. Selecting a row stores its index, pops the fragment back stack, and updates the main fragment's selection text.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyList screenshot](screenshot.png)
