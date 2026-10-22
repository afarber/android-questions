# CriminalIntent

Fragment-based crime-log exercise with a list, editable crime details, dates, and solved flags.

## Details

CrimeLab provides the shared collection, a pager navigates between crime records, and a dialog edits the date. The example demonstrates master/detail navigation and communication between fragments and Activities.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![CriminalIntent screenshot](screenshot.png)
