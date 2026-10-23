#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.composeonlymap

start_demo

gestures() {
    sleep 1
    adb shell input tap 704 363 # Isfahan marker above its map-center anchor
    sleep 2
    adb shell input tap 450 500
    adb shell input swipe 800 450 620 410 600
    sleep 3
    adb shell input swipe 620 410 800 450 600
}

record_demo 9
