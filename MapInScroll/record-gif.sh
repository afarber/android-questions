#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.mapinscroll

start_demo

gestures() {
    sleep 1
    adb shell input swipe 1300 620 1300 180 700 # Scroll down from the form
    sleep 2
    adb shell input swipe 800 400 600 440 700 # Pan inside the map
    sleep 2
    adb shell input swipe 1350 180 1350 600 700 # Scroll from outside the map
}

record_demo 10
