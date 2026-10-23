# ComposeMap

Kotlin/Compose example wrapping OpenMapView in a reusable OpenStreetMap composable and moving its camera when the selected place changes.

## Details

The reusable composable owns the map integration while selection state controls the camera. It demonstrates embedding a map in a Compose screen and reacting to changes from the surrounding UI.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Create a map using openstreetmap with jetpack compose in Kotlin](https://stackoverflow.com/q/77297775).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![ComposeMap screenshot](screenshot.gif)
