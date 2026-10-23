#!/bin/bash
# Shared recorder for the 1408x792 Automotive emulator. Never reduces GIF width.
# Usage: capture-gif.sh <example directory> <seconds> [width:height:x:y]
set -euo pipefail

export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
EXAMPLE_DIR=${1:?Pass the example directory}
DURATION=${2:-10}
CROP=${3:-1408:620:0:76}
WORK_DIR=$(mktemp -d)
REMOTE_VIDEO="/sdcard/demo-capture-$$.mp4"
# shellcheck disable=SC2329 # Invoked indirectly by the EXIT trap.
cleanup() {
    adb shell rm -f "$REMOTE_VIDEO" >/dev/null 2>&1 || true
    rm -rf "$WORK_DIR"
}
trap 'cleanup' EXIT

adb shell screenrecord --time-limit "$DURATION" "$REMOTE_VIDEO"
adb pull "$REMOTE_VIDEO" "$WORK_DIR/capture.mp4" >/dev/null

# Use one palette for the clip, no noisy dithering, and a fixed 800 px width.
# Retry at lower frame rates/palette sizes, never at a smaller image width.
for setting in "8 64" "6 64" "6 48" "4 48"; do
    read -r fps colors <<< "$setting"
    filters="crop=$CROP,fps=$fps,scale=800:-2:flags=lanczos"
    ffmpeg -loglevel error -y -i "$WORK_DIR/capture.mp4" \
        -vf "$filters,palettegen=max_colors=$colors" -update 1 "$WORK_DIR/palette.png"
    ffmpeg -loglevel error -y -i "$WORK_DIR/capture.mp4" -i "$WORK_DIR/palette.png" \
        -lavfi "$filters [x]; [x][1:v] paletteuse=dither=none:diff_mode=rectangle" \
        -loop 0 "$WORK_DIR/screenshot.gif"
    bytes=$(wc -c < "$WORK_DIR/screenshot.gif" | tr -d ' ')
    echo "$(basename "$EXAMPLE_DIR"): 800 px, $fps fps, $colors colors, $bytes bytes"
    if [ "$bytes" -lt 4000000 ]; then
        mv "$WORK_DIR/screenshot.gif" "$EXAMPLE_DIR/screenshot.gif"
        exit 0
    fi
done
echo "Clip exceeds 4 MB at 800 px; shorten its gesture sequence and record again." >&2
exit 1
