CurvedLine
==========

Kotlin / Jetpack Compose app with MIT-licensed [OpenMapView](https://github.com/afarber/OpenMapView) 0.29.1, which draws curved lines between two draggable markers:

- Gray: a straight reference line on the map.
- Blue: a great-circle path using `Polyline(geodesic = true)`.
- Orange: a dashed polyline through the sampled points of a quadratic Bezier arc.

Drag either marker to reshape all three lines. The Bezier arc is a visual curve computed in latitude/longitude degrees; the geodesic follows the shortest path on a sphere.

Answer to the Stack Overflow question [How to draw a curved line between two coordinates using Osmdroid library](https://stackoverflow.com/q/54502178).

![screenshot](screenshot.gif)

Build and install with `./gradlew :app:installDebug`. With the 1408x792 automotive emulator running and the tiles loaded, run `./record-gif.sh` to reproduce the 12-second recording. The script uses `~/src/OpenMapView/scripts/capture-gif.sh` unchanged and crops out the system bars.
