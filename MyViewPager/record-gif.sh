#!/bin/bash
# Reproduce screenshot.gif on Automotive_1408p_landscape (1408x792).
set -euo pipefail

EXAMPLE_DIR=$(cd "$(dirname "$0")" && pwd)
OPENMAPVIEW_DIR=${OPENMAPVIEW_DIR:-"$HOME/src/OpenMapView"}
export PATH="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools:$PATH"
export ANDROID_SERIAL=${ANDROID_SERIAL:-emulator-5554}
PACKAGE=de.afarber.myviewpager

if [ ! -x "$OPENMAPVIEW_DIR/scripts/capture-gif.sh" ]; then
    echo "Set OPENMAPVIEW_DIR to the checkout containing scripts/capture-gif.sh" >&2
    exit 1
fi
if ! adb shell wm size | tr -d '\r' | grep -qx 'Physical size: 1408x792'; then
    echo "This gesture sequence requires a 1408x792 emulator." >&2
    exit 1
fi

adb shell am start -S -n "$PACKAGE/.MainActivity" > /dev/null
sleep 2
adb shell uiautomator dump /sdcard/myviewpager-ui.xml > /dev/null
if ! adb shell cat /sdcard/myviewpager-ui.xml | grep -Fq 'MyViewPager'; then
    echo "Expected the initial app screen; inspect the app before recording." >&2
    exit 1
fi

# Read the drawer row bounds so font scaling does not change the selected game.
tap_text() {
    adb shell uiautomator dump /sdcard/myviewpager-ui.xml > /dev/null
    local center
    center=$(adb shell cat /sdcard/myviewpager-ui.xml | python3 -c '
import re, sys, xml.etree.ElementTree as E
root = E.fromstring(sys.stdin.read())
node = next(n for n in root.iter("node") if n.get("text") == sys.argv[1])
x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.get("bounds")))
print((x1 + x2) // 2, (y1 + y2) // 2)
' "$1")
    read -r x y <<< "$center"
    adb shell input tap "$x" "$y"
}

(
    sleep 1
    adb shell input tap 50 122 # Open the navigation drawer.
    sleep 1
    tap_text 'Game 1'
    sleep 2
    adb shell input swipe 1100 400 400 400 400 # Game -> Chat.
    sleep 2
    adb shell input swipe 400 400 1100 400 400 # Chat -> Game.
    sleep 1
    adb shell input swipe 400 400 1100 400 400 # Game -> Moves.
) &
gestures=$!
trap 'kill "$gestures" 2>/dev/null || true' EXIT

"$OPENMAPVIEW_DIR/scripts/capture-gif.sh" "$EXAMPLE_DIR" 14 1408:620:0:76
wait "$gestures"
trap - EXIT
