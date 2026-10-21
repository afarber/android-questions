# LabelMarker

Compose map example drawing a hollow pin with a rounded two-line label that can change at runtime.

## Details

The custom marker combines the pin and its label. Updating the text demonstrates keeping a multi-line label attached to its geographic point while the map is panned.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [How to make such a custom marker? osmdroid](https://stackoverflow.com/q/45898293).

## Recording

After installing the app on the 1408×792 Automotive emulator, run `./record-gif.sh` from this directory. The script uses ADB, FFmpeg, and `scripts/capture-gif.sh` from an OpenMapView checkout. Set `ANDROID_SERIAL` and `OPENMAPVIEW_DIR` when their defaults do not match your setup.

## Screenshot

![LabelMarker screenshot](screenshot.gif)
