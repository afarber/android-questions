WorldWrap
=========

Java app with [OpenMapView](https://github.com/afarber/OpenMapView), which repeats the world only horizontally, like Google Maps: the map can be panned around the world, a flight from Beijing to San Francisco continues over the Pacific, and there is no copy of the world above the north pole or below the south pole. A button limits the camera target with `setLatLngBoundsForCameraTarget`, so panning stops just before the date line.

Answer to the Stack Overflow question [OSMDroid Showing more than one maps and infinite scroll](https://stackoverflow.com/q/44943357).

![screenshot](screenshot.gif)
