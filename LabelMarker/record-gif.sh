#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
PACKAGE=de.afarber.labelmarker

if [ ! -x "$OPENMAPVIEW_DIR/scripts/capture-gif.sh" ]; then
    echo "Set OPENMAPVIEW_DIR to the OpenMapView checkout containing scripts/capture-gif.sh" >&2
    exit 1
fi
if ! adb shell wm size | tr -d '\r' | grep -qx 'Physical size: 1408x792'; then
    echo "This gesture sequence requires a 1408x792 emulator." >&2
    exit 1
fi

adb shell am start -S -n "$PACKAGE/.MainActivity" > /dev/null
sleep 5 # Let the initial tiles load.
adb shell uiautomator dump /sdcard/labelmarker-ui.xml > /dev/null
if ! adb shell cat /sdcard/labelmarker-ui.xml | grep -q 'Label: Text / Text'; then
    echo "Expected the original two-line label; inspect the app." >&2
    exit 1
fi

# Change label bounds from the UI tree: [1198,99][1385,168].
# The marker is drawn on the canvas; its placement was checked in screenshots.
(
    sleep 1.0
    adb shell input tap 1292 134 # Text / Text -> Berlin / City center; the pin stays put.
    sleep 2.0
    adb shell input swipe 650 500 810 540 400 # Pan the complete marker with the map.
    sleep 7.2
    adb shell input tap 1292 134 # Restore Text / Text near the end; keep the panned camera.
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 12 1408:620:0:76
wait "$gestures"
trap - EXIT
