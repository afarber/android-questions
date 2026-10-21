# QREncoder

Java example generating a fixed QR code with ZXing and displaying it in an ImageView.

## Details

QRCodeWriter encodes a string into a BitMatrix, which is copied into a 400-by-400 bitmap. The sample has no editing controls, so its original static screenshot is retained.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The minimum Android version is 6.0 (API 23); the app targets API 35. Dependencies are pinned in `gradle/libs.versions.toml`.

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest
./gradlew :app:installDebug
```

## Screenshot

![QREncoder screenshot](screenshot.png)
