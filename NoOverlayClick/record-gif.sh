#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}

if [ ! -x "$OPENMAPVIEW_DIR/scripts/capture-gif.sh" ]; then
    echo "Set OPENMAPVIEW_DIR to the OpenMapView checkout containing scripts/capture-gif.sh" >&2
    exit 1
fi
if ! adb shell wm size | tr -d '\r' | grep -qx 'Physical size: 1408x792'; then
    echo "This gesture sequence requires a 1408x792 emulator." >&2
    exit 1
fi

adb shell am start -S -n de.afarber.nooverlayclick/.MainActivity > /dev/null
sleep 5 # Let the tiles load before recording.

# Checkbox bounds from the UI tree: [1048,99][1385,168].
# Route, polygon, and marker coordinates were verified on the main-display screenshot.
(
    sleep 0.8
    adb shell input tap 214 400 # Suppressed route: Map 1, Route 0.
    sleep 0.9
    adb shell input tap 1030 400 # Suppressed polygon: Map 2, Polygon 0.
    sleep 0.9
    adb shell input tap 704 344 # Consume marker tap; no info window or camera movement.
    sleep 0.9
    adb shell input tap 1210 134 # Turn suppression off without changing the counters.
    sleep 0.9
    adb shell input tap 214 400 # Route callback: Route 1.
    sleep 0.9
    adb shell input tap 1030 400 # Polygon callback: Polygon 1.
    sleep 0.9
    adb shell input tap 704 344 # Default marker behavior: open its info window.
    sleep 4.3 # Hold the popup; place the final visual change near the recording end.
    adb shell input tap 1210 134 # Restore suppression and close the window.
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 12 1408:620:0:76
wait "$gestures"
trap - EXIT
