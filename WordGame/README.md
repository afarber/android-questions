# WordGame

Dialog examples for choosing a letter, selecting tiles to swap, and entering a word.

## Details

RecyclerView presents the letter grid and swap list, while an EditText collects a search word. Dialog callbacks return the chosen values to MainActivity for feedback.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The minimum Android version is 6.0 (API 23); the app targets API 35. Dependencies are pinned in `gradle/libs.versions.toml`.

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest
./gradlew :app:installDebug
```

## Screenshot

![WordGame screenshot](screenshot.png)
