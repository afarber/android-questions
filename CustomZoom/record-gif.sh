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

adb shell am start -S -n de.afarber.customzoom/.MainActivity > /dev/null
sleep 5 # Let the tiles load before recording.

# Centers from the UI tree: zoomIn [1156,300][1385,380], zoomOut [1156,391][1385,471].
(
    sleep 1.0 # Show the initial zoom 13 with both buttons enabled.
    adb shell input tap 1270 340 # Zoom 13 -> 14; Zoom in becomes disabled.
    sleep 2.0
    adb shell input tap 1270 430 # Zoom 14 -> 13; both buttons become enabled.
    sleep 2.0
    adb shell input tap 1270 430 # Zoom 13 -> 12; Zoom out becomes disabled.
    sleep 5.5 # Hold the minimum zoom; keep the last animation near the recording end.
    adb shell input tap 1270 340 # Zoom 12 -> 13; both buttons become enabled again.
    # A late final animation keeps screenrecord from dropping a long idle tail.
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 12 1408:620:0:76
wait "$gestures"
trap - EXIT
