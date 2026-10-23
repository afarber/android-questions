#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.drivingroute

start_demo 8

gestures() {
    sleep 1
    adb shell input tap 620 440 # Start, on the road network near central Wolfsburg
    sleep 1
    adb shell input tap 940 350 # Destination
    sleep 5
    require_text "Route:"
    sleep 1
    tap_label Cancel # Locate the actual floating action button bounds.
}

# This app is fullscreen: preserve its route card above the usual status-bar inset.
record_demo 16 1408:696:0:0
