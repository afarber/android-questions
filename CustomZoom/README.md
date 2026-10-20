CustomZoom
==========

Java / XML app with [OpenMapView](https://github.com/afarber/OpenMapView), showing custom zoom buttons with the built-in controls and zoom gestures disabled. Map panning remains enabled.

Each button has an explicit click listener:

```java
map.getUiSettings().setZoomControlsEnabled(false);
map.getUiSettings().setZoomGesturesEnabled(false);
zoomIn.setOnClickListener(v -> map.animateCamera(CameraUpdateFactory.zoomIn()));
zoomOut.setOnClickListener(v -> map.animateCamera(CameraUpdateFactory.zoomOut()));
```

The map starts at zoom 13 over Berlin's Brandenburg Gate, with limits of 12 and 14. A readout follows the animated camera zoom. Buttons are disabled during camera movement, and the corresponding button stays disabled at each zoom limit. Idle and cancellation listeners restore the controls even when a map touch interrupts a zoom animation.

Answer to the Stack Overflow question [Custom zoom buttons for osmdroid](https://stackoverflow.com/q/46925456).

![Custom buttons animating the map zoom](screenshot.gif)
