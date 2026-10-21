# CustomZoom

Java map example with custom buttons that animate zoom while built-in zoom controls and zoom gestures are disabled.

## Details

Panning stays enabled. The map starts over Brandenburg Gate at zoom 13 with limits of 12 and 14; camera idle and cancellation callbacks restore button state after animation or interruption.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Custom zoom buttons for osmdroid](https://stackoverflow.com/q/46925456).

## Recording

After installing the app on the 1408×792 Automotive emulator, run `./record-gif.sh` from this directory. The script uses ADB, FFmpeg, and `scripts/capture-gif.sh` from an OpenMapView checkout. Set `ANDROID_SERIAL` and `OPENMAPVIEW_DIR` when their defaults do not match your setup.

## Screenshot

![CustomZoom screenshot](screenshot.gif)
