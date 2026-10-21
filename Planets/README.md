# Planets

RecyclerView example displaying planets with star icons and click feedback.

## Details

LinearLayoutManager lays out the planet names. Each ViewHolder handles its row click and displays a toast naming the selected planet.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The minimum Android version is 6.0 (API 23); the app targets API 35. Dependencies are pinned in `gradle/libs.versions.toml`.

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest
./gradlew :app:installDebug
```

## Screenshot

![Planets screenshot](screenshot.png)
