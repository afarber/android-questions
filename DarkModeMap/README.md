# DarkModeMap

Compose map preserving its camera and a user-drawn polygon when switching between light and dark mode.

## Details

The Activity can be recreated without losing the camera or polygon. OpenMapView's mapColorScheme darkens the default tiles without an API key; the optional CARTO dark map type requires a CARTO API key.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Handling configuration changes in OSMDroid + Jetpack Compose](https://stackoverflow.com/q/77229152).

## Screenshot

![DarkModeMap screenshot](screenshot.gif)
