#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.mapintabs

start_demo
adb shell input tap 704 635 # Map tab
sleep 5

gestures() {
    sleep 1
    adb shell input swipe 800 400 600 450 600
    sleep 2
    adb shell input tap 1173 635 # Data tab shows saved camera coordinates
    sleep 2
    adb shell input tap 234 635 # Settings
    sleep 1
    adb shell input tap 704 635 # Return to preserved map
}

record_demo 11
