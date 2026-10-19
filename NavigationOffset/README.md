NavigationOffset
================

Java app with [OpenMapView](https://github.com/afarber/OpenMapView), which shows the current location near the bottom of the map in navigation mode, so that more of the road ahead is visible. A top map padding with `setMapPadding()` moves the camera target down, and the following camera updates keep it there. A button switches between the bottom and the center.

Answer to the Stack Overflow question [osmdroid: animate to current location with pixel offset](https://stackoverflow.com/q/74046688).

![screenshot](screenshot.gif)
