# FitAllMarkers

Java map example fitting the camera to a collection of markers with newLatLngBounds.

## Details

The camera bounds are calculated from the marker coordinates. This demonstrates showing the whole collection in one view instead of selecting a fixed camera center and zoom.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [OSMDROID - Zoom to show all markers](https://stackoverflow.com/q/68643747).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![FitAllMarkers screenshot](screenshot.gif)
