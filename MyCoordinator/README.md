# MyCoordinator

Collapsing-toolbar example with a palette-colored header, RecyclerView, and animated floating action button.

## Details

Palette extracts a muted color from the header image for the collapsing scrim. List items show click feedback, and a Handler periodically toggles the floating button's animation.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The minimum Android version is 6.0 (API 23); the app targets API 35. Dependencies are pinned in `gradle/libs.versions.toml`.

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest
./gradlew :app:installDebug
```

## Screenshot

![MyCoordinator screenshot](screenshot.png)
