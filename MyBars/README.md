# MyBars

Custom word-game board with a separate tile rack, draggable letter tiles, and board panning and zooming.

## Details

GameBoard owns the board transform while the rack and enlarged drag preview are drawn in screen coordinates. Board tiles snap to a 15-by-15 grid, and neighboring tiles determine which tile edges are drawn.

The board SVG is by [Denelson83 on Wikimedia Commons](http://en.wikipedia.org/wiki/File:Blank_Scrabble_board_with_coordinates.svg), licensed under CC BY-SA 3.0.

## Build and run

Requires JDK 17 or newer and Android SDK 37. The app supports Android 6.0 (API 23) and newer, and targets API 35. Dependencies are pinned in [gradle/libs.versions.toml](gradle/libs.versions.toml).

From this directory:

```sh
./gradlew :app:assembleDebug :app:lintDebug
./gradlew :app:installDebug
```

## Screenshot

Original emulator screenshot, preserved.

![MyBars screenshot](screenshot.png)
