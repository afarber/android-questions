#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.draggablemarker

start_demo

gestures() {
    sleep 1
    adb shell input touchscreen draganddrop 704 365 870 450 1000
    sleep 2
    adb shell input touchscreen draganddrop 870 450 610 360 1000
}

record_demo 9
require_text "Dropped"
