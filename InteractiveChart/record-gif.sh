#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
PACKAGE=com.example.android.interactivechart

if [ ! -x "$OPENMAPVIEW_DIR/scripts/capture-gif.sh" ]; then
    echo "Set OPENMAPVIEW_DIR to the checkout containing scripts/capture-gif.sh" >&2
    exit 1
fi
if ! adb shell wm size | tr -d '\r' | grep -qx 'Physical size: 1408x792'; then
    echo "This gesture sequence requires a 1408x792 emulator." >&2
    exit 1
fi

adb shell am start -S -n "$PACKAGE/.MainActivity" > /dev/null
sleep 2
adb shell uiautomator dump /sdcard/interactivechart-ui.xml > /dev/null
if ! adb shell cat /sdcard/interactivechart-ui.xml | grep -Fq 'Interactive Chart Demo'; then
    echo "Expected the initial app screen; inspect the app before recording." >&2
    exit 1
fi

(
    sleep 2
    adb shell 'input tap 730 410; sleep 0.1; input tap 730 410' # Double-tap zoom.
    sleep 2
    adb shell input swipe 730 410 850 460 400
    sleep 2
    adb shell input swipe 850 460 650 380 400
    sleep 2
    adb shell 'input tap 730 410; sleep 0.1; input tap 730 410'
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 16 1408:620:0:76
wait "$gestures"
trap - EXIT
