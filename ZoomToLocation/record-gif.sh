#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.zoomtolocation

adb shell pm grant --user "$EMULATOR_USER" "$PACKAGE" android.permission.ACCESS_COARSE_LOCATION
adb shell pm grant --user "$EMULATOR_USER" "$PACKAGE" android.permission.ACCESS_FINE_LOCATION
adb shell cmd location set-location-enabled true --user "$EMULATOR_USER"
adb emu geo fix 13.3777 52.5163 >/dev/null
# Warm the location tiles, then launch afresh so the first GPS fix centers the map.
start_demo
adb emu geo fix 13.3777 52.5163 >/dev/null
sleep 4
start_demo 3 # Reopen after GPS caches contain the seeded fix, rather than an old fix.

gestures() {
    sleep 1
    adb shell input tap 704 430 # Open location marker info window
    sleep 2
    adb shell input tap 500 500
    adb emu geo fix 13.3790 52.5168 >/dev/null
    sleep 3
    adb shell input swipe 800 450 700 420 600
}

record_demo 10
