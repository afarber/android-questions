# DraggableMarker

Kotlin map example with a draggable marker using a custom icon.

## Details

The marker keeps its custom appearance while its position changes through direct dragging. The example focuses on OpenMapView marker configuration and drag interaction.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Marker in osmdroid map](https://stackoverflow.com/q/79250427).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![DraggableMarker screenshot](screenshot.gif)
