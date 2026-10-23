# SimpleMapStyle

Kotlin map example switching between OpenMapView map styles without an API key.

## Details

The style selection changes the tile presentation while retaining the map interaction. The example focuses on styles available without configuring a provider API key.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [I can't display map tiles other than MAPNIK with osmdroid, I want a simplier map](https://stackoverflow.com/q/78547714).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![SimpleMapStyle screenshot](screenshot.gif)
