#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.composemap

# Warm all three destination views before restoring the initial selection.
start_demo
adb shell input tap 250 122
sleep 4
adb shell input tap 420 122
sleep 4
adb shell input tap 80 122
sleep 3

gestures() {
    sleep 1
    adb shell input tap 250 122 # Alexanderplatz
    sleep 3
    adb shell input tap 420 122 # Tempelhof
    sleep 3
    adb shell input tap 80 122 # Reichstag
}

record_demo 10
