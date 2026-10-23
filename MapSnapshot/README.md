# MapSnapshot

Compose map example saving a map image containing a running track as run.png.

## Details

The screenshot is taken from the running map after rendering the track. It demonstrates capturing the map content rather than a screenshot of the entire Android screen.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [how to get screen shot with OSMDroid](https://stackoverflow.com/q/79513346).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![MapSnapshot screenshot](screenshot.gif)
