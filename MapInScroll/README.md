# MapInScroll

Java form with an OpenMapView inside a ScrollView, allowing map drags to pan while form drags scroll the page.

## Details

The form collects an area name, notes, and type before the map. Touch handling lets the embedded map receive its own drag gestures while the surrounding form retains vertical scrolling.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Block Mapview from Scrollview interaction Android](https://stackoverflow.com/q/73364416).

## Screenshot

![MapInScroll screenshot](screenshot.gif)
