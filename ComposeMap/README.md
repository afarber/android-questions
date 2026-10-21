# ComposeMap

Kotlin/Compose example wrapping OpenMapView in a reusable OpenStreetMap composable and moving its camera when the selected place changes.

## Details

The reusable composable owns the map integration while selection state controls the camera. It demonstrates embedding a map in a Compose screen and reacting to changes from the surrounding UI.

Map rendering uses [OpenMapView](https://github.com/afarber/OpenMapView).

Answer to the Stack Overflow question [Create a map using openstreetmap with jetpack compose in Kotlin](https://stackoverflow.com/q/77297775).

## Screenshot

![ComposeMap screenshot](screenshot.gif)
