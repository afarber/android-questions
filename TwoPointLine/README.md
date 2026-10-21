# TwoPointLine

Java/XML map example drawing a red polyline between two fixed Berlin coordinates and fitting both endpoints in view.

## Details

Pan or zoom the map, then use Fit endpoints to restore the overview. The segment joins the coordinates directly and does not represent a calculated road route.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [How can draw a line (Polyline) in Android studio in OSM?](https://stackoverflow.com/q/51068005).

## Recording

After installing the app on the 1408×792 Automotive emulator, run `./record-gif.sh` from this directory. The script uses ADB, FFmpeg, and `scripts/capture-gif.sh` from an OpenMapView checkout. Set `ANDROID_SERIAL` and `OPENMAPVIEW_DIR` when their defaults do not match your setup.

## Screenshot

![TwoPointLine screenshot](screenshot.gif)
