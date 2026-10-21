#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
PACKAGE=de.afarber.dialogcountdown

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
adb shell uiautomator dump /sdcard/dialogcountdown-ui.xml > /dev/null
if ! adb shell cat /sdcard/dialogcountdown-ui.xml | grep -Fq 'show_dialog_button'; then
    echo "Expected the initial app screen; inspect the app before recording." >&2
    exit 1
fi

(
    sleep 2
    adb shell input tap 704 432 # Show the dialog; it dismisses after 20 seconds.
    sleep 22
    adb shell uiautomator dump /sdcard/dialogcountdown-finished.xml > /dev/null
    if ! adb shell cat /sdcard/dialogcountdown-finished.xml | grep -Fq 'show_dialog_button'; then
        echo "Expected the countdown dialog to dismiss automatically." >&2
        exit 1
    fi
    # A press dragged off the button adds a ripple without opening another dialog,
    # keeping screenrecord active until the dismissal transition has finished.
    adb shell input swipe 704 432 704 250 400
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 30 1408:620:0:76
wait "$gestures"
trap - EXIT
