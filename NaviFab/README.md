# NaviFab

Navigation-drawer example combining a circular profile image, game entries, and account fragments.

## Details

The drawer header displays a profile and an account button. Opening accounts replaces the main content with a social-provider list, whose items navigate to another demonstration fragment.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The minimum Android version is 6.0 (API 23); the app targets API 35. Dependencies are pinned in `gradle/libs.versions.toml`.

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest
./gradlew :app:installDebug
```

## Screenshot

![NaviFab screenshot](screenshot.png)
