DarkModeMap
===========

Kotlin / Jetpack Compose app with [OpenMapView](https://github.com/afarber/OpenMapView), which keeps the camera and a polygon drawn by the user when switching between light and dark mode recreates the Activity. The map follows dark mode with `mapColorScheme`: the OpenStreetMap tiles are darkened without an API key, while the markers and the polygon keep their colors. A designed dark style is `MapType.CARTO_DARK`, which needs a CARTO API key.

Answer to the Stack Overflow question [Handling configuration changes in OSMDroid + Jetpack Compose](https://stackoverflow.com/q/77229152).

![screenshot](screenshot.gif)
