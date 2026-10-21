# LiveMarker

Java map example moving one drawable marker to the device's live location and centering the camera on the first fix.

## Details

Later fixes update the same marker, and Center returns to the latest position. The UI handles precise or approximate location access, missing permission, disabled location, and a last known fix.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [How to Add Marker in osmdroid?](https://stackoverflow.com/q/55705988).

## Recording

After installing the app on the 1408×792 Automotive emulator, run `./record-gif.sh` from this directory. The script uses ADB, FFmpeg, and `scripts/capture-gif.sh` from an OpenMapView checkout. Set `ANDROID_SERIAL` and `OPENMAPVIEW_DIR` when their defaults do not match your setup.

## Screenshot

![LiveMarker screenshot](screenshot.gif)
