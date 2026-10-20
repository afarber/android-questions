#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
PACKAGE=de.afarber.twopointline

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
adb shell uiautomator dump /sdcard/twopointline-ui.xml > /dev/null
if ! adb shell cat /sdcard/twopointline-ui.xml | grep -q '2 points · 1 segment · Zoom: 13.5'; then
    echo "Expected both fixed endpoints to be fitted at zoom 13.5; inspect the app." >&2
    exit 1
fi

# UI tree: map [0,76][1408,696], Fit endpoints [1199,99][1385,168].
# The zoom controls are canvas-drawn, absent from the UI tree; their centers
# (1357,541) and (1357,610) were checked on the main-display screenshot.
(
    sleep 1.0
    adb shell input tap 1357 610 # Zoom out: 13.5 -> 12.5; geometry stays fixed.
    sleep 1.7
    adb shell input swipe 680 440 820 480 400 # Short pan, clear of the System UI handle.
    sleep 1.6
    adb shell input tap 1357 541 # Zoom back in to 13.5 around the new center.
    sleep 6.0
    adb shell input tap 1292 134 # Fit both endpoints again near the recording end.
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 12 1408:620:0:76
wait "$gestures"
trap - EXIT
