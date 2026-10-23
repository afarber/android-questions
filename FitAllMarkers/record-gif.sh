#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.fitallmarkers

start_demo

gestures() {
    sleep 1
    adb shell input tap 160 640 # Munich
    sleep 2
    adb shell input tap 160 640 # Cologne
    sleep 2
    adb shell input tap 160 640 # Copenhagen
    sleep 2
    adb shell input tap 160 640 # Vienna
}

record_demo 11
require_text "6 shown"
