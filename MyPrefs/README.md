# MyPrefs

Preference-screen example with checkboxes, text fields, sliders, and a custom number-picker dialog.

## Details

A shared-preference listener updates each setting's summary as values change. Custom SeekBarPreference and NumberPickerPreference classes demonstrate embedding controls in the legacy preference UI.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyPrefs screenshot](screenshot.png)
