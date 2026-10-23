# MovingMarker

Compose map example animating a navigation arrow along a predefined route while the camera follows it.

## Details

A single animation updates marker position, marker rotation, and camera position together. The route is predefined; the example focuses on keeping marker movement and camera tracking synchronized.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Osmdroid how to move position marker in animative way with animateTo function](https://stackoverflow.com/q/74200873).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![MovingMarker screenshot](screenshot.gif)
