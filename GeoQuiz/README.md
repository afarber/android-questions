# GeoQuiz

True-or-false geography quiz with next-question navigation and a separate answer-reveal screen.

## Details

The quiz checks answers and shows feedback. CheatActivity can reveal the current answer, and the quiz tracks that result to change its response when the user returns.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![GeoQuiz screenshot](screenshot.png)
