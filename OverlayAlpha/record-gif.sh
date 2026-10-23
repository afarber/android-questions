#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.overlayalpha

start_demo 8 # Let the base map and topographic overlay tiles load.
require_text "Topo layer alpha: 50%"

# The UI tree puts the SeekBar at [0,119][1408,191]; its padded track spans x=23..1385.
gestures() {
    sleep 1
    adb shell input swipe 704 155 23 155 700 # 50% -> 0%: base map only.
    sleep 2
    adb shell input swipe 23 155 1385 155 700 # 0% -> 100%: opaque topographic layer.
    sleep 2
    adb shell input swipe 1385 155 704 155 700 # Restore the 50% blend.
}

record_demo 10
require_text "Topo layer alpha: 50%"
