# MyBars

Custom word-game board with a separate tile rack, draggable letter tiles, and board panning and zooming.

## Details

GameBoard owns the board transform while the rack and enlarged drag preview are drawn in screen coordinates. Board tiles snap to a 15-by-15 grid, and neighboring tiles determine which tile edges are drawn.

The board SVG is by [Denelson83 on Wikimedia Commons](http://en.wikipedia.org/wiki/File:Blank_Scrabble_board_with_coordinates.svg), licensed under CC BY-SA 3.0.

This is a legacy Eclipse/ADT project. Its declared SDK and library references are in [project.properties](project.properties).

## Screenshot

![MyBars screenshot](screenshot.png)
