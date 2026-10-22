# SwipeBooks

ViewPager example displaying three swipeable placeholder sections using fragments.

## Details

SectionsPagerAdapter creates a PlaceholderFragment for each of three numbered pages. It demonstrates the pager structure and page titles; the checked-in pages use placeholder content.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![SwipeBooks screenshot](screenshot.png)
