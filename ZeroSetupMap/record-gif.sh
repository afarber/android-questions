#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail
EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
# shellcheck source=scripts/record-map-demo.sh
source "$EXAMPLE_DIR/../scripts/record-map-demo.sh"
PACKAGE=de.afarber.zerosetupmap

# Reset only this demo, proving tile loading without application-managed cache setup.
adb shell pm clear --user "$EMULATOR_USER" "$PACKAGE" >/dev/null
start_demo 8
require_text "Map loaded"

gestures() {
    sleep 1
    adb shell input swipe 800 420 660 450 600
    sleep 3
    adb shell input swipe 660 450 800 420 600
}

record_demo 8
