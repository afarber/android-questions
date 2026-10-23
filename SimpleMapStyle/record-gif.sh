#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.simplemapstyle

start_demo
# Warm each provider before showing the switches in the recording.
for x in 176 528 880 1232; do
    adb shell input tap "$x" 110
    sleep 5
done
adb shell input tap 176 110
sleep 3

gestures() {
    sleep 1
    adb shell input tap 528 110 # Humanitarian
    sleep 2
    adb shell input tap 880 110 # Public transport
    sleep 2
    adb shell input tap 1232 110 # CyclOSM
    sleep 2
    adb shell input tap 176 110 # Standard OSM
}

record_demo 10
