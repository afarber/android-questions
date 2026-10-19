FitRoute
========

Java app with [OpenMapView](https://github.com/afarber/OpenMapView), which zooms the map to a car route with `newLatLngBounds()`, called in `onCreate()` before the map has a size. The map applies the update after its layout, so no first layout listener is needed. The button animates back to the whole route.

Answer to the Stack Overflow question [osmdroid zoomToBoundingBox not zooming to boundingBox coordinates](https://stackoverflow.com/q/57258078).

![screenshot](screenshot.gif)
