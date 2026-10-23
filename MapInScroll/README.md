# MapInScroll

Java form with an OpenMapView inside a ScrollView, allowing map drags to pan while form drags scroll the page.

## Details

The form collects an area name, notes, and type before the map. Touch handling lets the embedded map receive its own drag gestures while the surrounding form retains vertical scrolling.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Block Mapview from Scrollview interaction Android](https://stackoverflow.com/q/73364416).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![MapInScroll screenshot](screenshot.gif)
