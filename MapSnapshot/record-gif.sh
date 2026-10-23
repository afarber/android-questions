#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.mapsnapshot

start_demo

gestures() {
    sleep 1
    adb shell input tap 704 636 # Finish run
    sleep 3
    adb shell input swipe 800 400 720 430 600 # Saved preview stays unchanged
}

record_demo 8
require_text "Saved run.png"
