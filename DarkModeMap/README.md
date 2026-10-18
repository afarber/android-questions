DarkModeMap
===========

Kotlin / Jetpack Compose app with [OpenMapView](https://github.com/afarber/OpenMapView), which keeps the camera and a polygon drawn by the user when switching between light and dark mode recreates the Activity. The dark tiles are a workaround: the OpenStreetMap tiles are inverted with a color filter (Android 12 and newer). A real dark style is `MapType.CARTO_DARK`, which needs a CARTO API key.

Answer to the Stack Overflow question [Handling configuration changes in OSMDroid + Jetpack Compose](https://stackoverflow.com/q/77229152).

![screenshot](screenshot.gif)
