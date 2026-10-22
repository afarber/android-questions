# MyActionBar

Legacy framework ActionBar example showing an indeterminate progress indicator while the Activity is resumed.

## Details

The Activity requests the ActionBar and indeterminate-progress window features before inflating its layout. onResume shows the progress indicator and onPause hides it; the settings menu is a placeholder.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyActionBar screenshot](screenshot.png)
