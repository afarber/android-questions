#!/bin/bash
# Sourced by a sample's record-gif.sh; gestures use full-screen 1408x792 pixels.
set -euo pipefail
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
RECORDING_TOOLS=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

if ! adb emu avd name | tr -d '\r' | grep -qx Automotive_1408p_landscape; then
    echo "Select Automotive_1408p_landscape with ANDROID_SERIAL." >&2
    exit 1
fi
if ! adb shell wm size | tr -d '\r' | grep -qx 'Physical size: 1408x792'; then
    echo "The gesture sequences require a 1408x792 emulator." >&2
    exit 1
fi
EMULATOR_USER=$(adb shell am get-current-user | tr -d '\r')

start_demo() {
    adb shell am start -S --user "$EMULATOR_USER" -n "$PACKAGE/.MainActivity" >/dev/null
    sleep "${1:-5}"
}

# Assert visible application status before/after a gesture sequence.
require_text() {
    adb shell uiautomator dump /sdcard/map-demo-ui.xml >/dev/null
    if ! adb shell cat /sdcard/map-demo-ui.xml | grep -qi "$1"; then
        echo "Expected app status not found: $1" >&2
        return 1
    fi
}

tap_label() {
    local node bounds_re='bounds="\[([0-9]+),([0-9]+)\]\[([0-9]+),([0-9]+)\]"'
    adb shell uiautomator dump /sdcard/map-demo-ui.xml >/dev/null
    node=$(adb shell cat /sdcard/map-demo-ui.xml | grep -o '<node[^>]*' | \
        grep -E "(text|content-desc)=\"$1\"" | head -1)
    if [[ ! "$node" =~ $bounds_re ]]; then
        echo "Cannot find control: $1" >&2
        return 1
    fi
    adb shell input tap "$(( (BASH_REMATCH[1] + BASH_REMATCH[3]) / 2 ))" \
        "$(( (BASH_REMATCH[2] + BASH_REMATCH[4]) / 2 ))"
}

record_demo() {
    local seconds=$1
    gestures &
    local gestures_pid=$!
    trap 'kill "$gestures_pid" 2>/dev/null || true' EXIT
    "$RECORDING_TOOLS/capture-gif.sh" "$EXAMPLE_DIR" "$seconds" "${2:-1408:620:0:76}"
    wait "$gestures_pid"
    trap - EXIT
}
