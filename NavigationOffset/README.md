# NavigationOffset

Java map example placing the current-location marker near the bottom of a heading-up map using camera bearing and map padding.

## Details

The padding shifts the visible navigation focus away from the screen center. Camera bearing and the location indicator demonstrate a navigation-style presentation with more map area ahead of the user.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [osmdroid: animate to current location with pixel offset](https://stackoverflow.com/q/74046688).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![NavigationOffset screenshot](screenshot.gif)
