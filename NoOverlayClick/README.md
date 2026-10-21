# NoOverlayClick

Java map example comparing normal overlay clicks with shapes that pass taps through and a marker that consumes its clicks.

## Details

Suppression makes the route and polygon non-clickable while the marker callback consumes its click without opening an info window. Disabling suppression restores shape callbacks and normal marker behavior; counters and camera state remain visible.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Disable onClick event on Osmdroid's overlays](https://stackoverflow.com/q/43332459).

## Recording

After installing the app on the 1408×792 Automotive emulator, run `./record-gif.sh` from this directory. The script uses ADB, FFmpeg, and `scripts/capture-gif.sh` from an OpenMapView checkout. Set `ANDROID_SERIAL` and `OPENMAPVIEW_DIR` when their defaults do not match your setup.

## Screenshot

![NoOverlayClick screenshot](screenshot.gif)
