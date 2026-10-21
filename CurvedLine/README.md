# CurvedLine

Compose map comparing a straight line, a geodesic, and a Bezier arc between two draggable markers.

## Details

Dragging either endpoint updates all three paths. The gray path is a straight reference, the blue path follows a great circle, and the dashed orange path samples a quadratic Bezier curve in latitude/longitude coordinates.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [How to draw a curved line between two coordinates using Osmdroid library](https://stackoverflow.com/q/54502178).

## Recording

After installing the app on the 1408×792 Automotive emulator, run `./record-gif.sh` from this directory. The script uses ADB, FFmpeg, and `scripts/capture-gif.sh` from an OpenMapView checkout. Set `ANDROID_SERIAL` and `OPENMAPVIEW_DIR` when their defaults do not match your setup.

## Screenshot

![CurvedLine screenshot](screenshot.gif)
