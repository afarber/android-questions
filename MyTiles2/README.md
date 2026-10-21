# MyTiles2

Word-board variant combining grid-snapped tile dragging with constrained scrolling, flinging, and zooming.

## Details

The custom view tracks occupied cells and uses an enlarged tile while dragging. Pan and zoom calculations constrain the board to the view, with compatibility checks around gesture features on older Android versions.

This is a legacy Eclipse/ADT project. Its declared SDK and library references are in [project.properties](project.properties).

## Screenshot

![MyTiles2 screenshot](screenshot.png)
