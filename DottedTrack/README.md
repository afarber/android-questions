DottedTrack
===========

Java app with [OpenMapView](https://github.com/afarber/OpenMapView), which reads a GPX track from the assets and draws one polyline per track segment. The button switches between a solid, a dashed and a dotted line with `PolylineOptions.strokePattern()`. The round dots are a circle stamped along the line with an `android.graphics.PathDashPathEffect`. The track is a sample loop around the Aussenalster in Hamburg.

Answer to the Stack Overflow question [Is there a way to draw dotted line using osmdroid polyline on osm map?](https://stackoverflow.com/q/56685476).

![screenshot](screenshot.gif)
