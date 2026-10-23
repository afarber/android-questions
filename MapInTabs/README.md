# MapInTabs

Compose navigation-tab example preserving a map's camera when switching to another tab and back.

## Details

One NavigationBar destination hosts OpenMapView. The camera state survives destination changes, so returning to the map keeps the position selected before leaving it.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [osmdroid map disappears after switchings tabs](https://stackoverflow.com/q/77384062).

## Recording

Build and install the debug app, then run `./record-gif.sh` from this directory
with `Automotive_1408p_landscape` (1408×792) running. Requires ADB and FFmpeg;
set `ANDROID_SERIAL` if the emulator is not `emulator-5554`.
The script uses the repository's shared recorder to crop the system bars and
produce an 800 px wide `screenshot.gif` below 4 MB.

## Screenshot

![MapInTabs screenshot](screenshot.gif)
