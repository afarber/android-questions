# TabRowMap

Compose example keeping an OpenMapView and its camera state beneath a TabRow when switching tabs.

## Details

The tab row remains part of the surrounding Compose layout. Returning to the map tab restores the existing map position instead of starting with a new camera view.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Android Jetpack Compose open street map conflict with tabrow](https://stackoverflow.com/q/76161027).

## Screenshot

![TabRowMap screenshot](screenshot.gif)
