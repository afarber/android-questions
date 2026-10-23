# ComposeOnlyMap

Kotlin/Compose example displaying OpenMapView without an XML layout or explicit User-Agent configuration.

## Details

The map is created from Compose rather than inflated from a layout resource. This is a minimal starting point for placing an OpenStreetMap view in a Compose application.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Using osmdroid in a jetpack compose project](https://stackoverflow.com/q/77890423).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![ComposeOnlyMap screenshot](screenshot.gif)
