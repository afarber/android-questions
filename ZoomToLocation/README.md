# ZoomToLocation

Java map example moving the camera to the device's location.

## Details

The location action demonstrates centering OpenMapView on the position supplied by the device. Location availability and the app's permissions affect which position can be shown.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [I'm trying to zoom in on the user's location but it doesn't work](https://stackoverflow.com/q/79900408).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

The script grants location permissions to the active automotive user and seeds emulator GPS.

## Screenshot

![ZoomToLocation screenshot](screenshot.gif)
