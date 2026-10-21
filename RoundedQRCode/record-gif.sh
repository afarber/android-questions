#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
PACKAGE=de.afarber.roundedqrcode

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
adb shell uiautomator dump /sdcard/roundedqrcode-ui.xml > /dev/null
if ! adb shell cat /sdcard/roundedqrcode-ui.xml | grep -Fq 'editText'; then
    echo "Expected the initial app screen; inspect the app before recording." >&2
    exit 1
fi

(
    sleep 1
    adb shell input tap 700 650
    adb shell input text 'https://example.com'
    adb shell input keyevent KEYCODE_ENTER
    sleep 3
    adb shell input tap 700 650
    adb shell input text 'Hello%sworld'
    adb shell input keyevent KEYCODE_ENTER
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 14 1408:620:0:76
wait "$gestures"
trap - EXIT
