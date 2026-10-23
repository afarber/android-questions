# ZeroSetupMap

Kotlin map example displaying OpenStreetMap tiles without storage permissions or explicit cache initialization.

## Details

The Activity creates the map with minimal setup. It demonstrates the default tile-loading behavior without adding application-managed storage or cache configuration.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [How do I get OSMDroid sample code to show map tiles after clearing the app storage?](https://stackoverflow.com/q/78873489).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

This recording clears this demo’s app data before verifying default tile loading.

## Screenshot

![ZeroSetupMap screenshot](screenshot.gif)
