#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
PACKAGE=de.afarber.bottomsheet1

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
adb shell uiautomator dump /sdcard/bottomsheet1-ui.xml > /dev/null
if ! adb shell cat /sdcard/bottomsheet1-ui.xml | grep -Fq 'showDialogButton'; then
    echo "Expected the initial app screen; inspect the app before recording." >&2
    exit 1
fi

(
    sleep 1
    adb shell input tap 704 271 # Expand the persistent sheet.
    sleep 2
    adb shell input tap 704 271 # Collapse it again.
    sleep 2
    adb shell input tap 704 202 # Show the modal bottom sheet.
    sleep 2
    adb shell input swipe 704 650 704 350 400 # Expand the modal sheet.
    sleep 3
    adb shell input keyevent KEYCODE_BACK
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 16 1408:620:0:76
wait "$gestures"
trap - EXIT
