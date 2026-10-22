# AndroidBooks

Book-list and description example adapting fragment navigation to the available screen layout.

## Details

Selecting a book updates the description through the Activity's callback interface. Layout variants allow the description to appear beside the list or in a separate Activity.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![AndroidBooks screenshot](screenshot.png)
