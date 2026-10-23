#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.navigationoffset

start_demo 5

gestures() {
    sleep 1
    adb shell input tap 110 120 # Overview: centered marker and north-up map
    sleep 3
    adb shell input tap 110 120 # Navigation: heading-up map with lower marker
}

record_demo 8
require_text "Navigation"
