#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
PACKAGE=de.afarber.livemarker

if [ ! -x "$OPENMAPVIEW_DIR/scripts/capture-gif.sh" ]; then
    echo "Set OPENMAPVIEW_DIR to the OpenMapView checkout containing scripts/capture-gif.sh" >&2
    exit 1
fi
if ! adb shell wm size | tr -d '\r' | grep -qx 'Physical size: 1408x792'; then
    echo "This gesture sequence requires a 1408x792 emulator." >&2
    exit 1
fi

# Prepare foreground permission and GPS before the recording, for the active automotive user.
EMULATOR_USER=$(adb shell am get-current-user | tr -d '\r')
adb shell pm grant --user "$EMULATOR_USER" "$PACKAGE" android.permission.ACCESS_COARSE_LOCATION
adb shell pm grant --user "$EMULATOR_USER" "$PACKAGE" android.permission.ACCESS_FINE_LOCATION
adb shell cmd location set-location-enabled true --user "$EMULATOR_USER"
# Longitude first: the sample position from the accepted Stack Overflow answer.
adb emu geo fix 72.9342 20.5992 > /dev/null
adb shell am start -S -n "$PACKAGE/.MainActivity" > /dev/null

ready=false
for attempt in {1..10}; do
    sleep 1
    adb emu geo fix 72.9342 20.5992 > /dev/null
    adb shell uiautomator dump /sdcard/livemarker-ui.xml > /dev/null
    if adb shell cat /sdcard/livemarker-ui.xml | grep -q 'Precise location: 20.59920, 72.93420'; then
        ready=true
        break
    fi
done
if [ "$ready" != true ]; then
    echo "The initial GPS fix did not arrive; inspect the app's location status." >&2
    exit 1
fi
# Center bounds were read from the UI tree; ensure the initial camera uses the seeded fix.
adb shell input tap 1322 134
sleep 5 # Let the initial tiles load.

(
    sleep 1.0
    adb shell input tap 704 386 # Open the drawable marker's info window.
    sleep 1.3
    adb shell input tap 500 550 # Dismiss it with a map tap.
    sleep 1.0
    adb emu geo fix 72.9362 20.5999 > /dev/null # Move the same marker; camera stays put.
    sleep 1.8
    adb shell input swipe 650 550 750 520 500 # Pan away from the updated position.
    sleep 4.4
    adb shell input tap 1322 134 # Center on the latest fix near the recording end.
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 11 1408:620:0:76
wait "$gestures"
trap - EXIT
