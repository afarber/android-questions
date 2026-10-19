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

adb shell am start -S -n de.afarber.curvedline/.MainActivity > /dev/null
sleep 5 # Let the tiles load before recording.

(
    sleep 1.2 # Show the initial straight, great-circle, and Bezier lines.
    adb shell input swipe 364 428 455 490 900 # Drag the start marker southeast.
    sleep 1.7 # Hold the reshaped lines.
    adb shell input swipe 1034 318 1100 400 900 # Drag the end marker southeast.
    sleep 5.7 # Hold the second reshaping; keep the last pan near the recording end.
    adb shell input swipe 780 560 700 520 600 # Pan briefly; all lines stay attached.
    # A late final gesture keeps screenrecord from dropping a long idle tail.
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 12 1408:620:0:76
wait "$gestures"
trap - EXIT
