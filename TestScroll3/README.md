# TestScroll3

Custom board-view experiment combining View scrolling, inertial flinging, and focal-point scaling.

## Details

The canvas scales the board while scrollTo constrains the view offsets. A gesture listener updates the scale around the touch focus, and OverScroller drives inertial movement through computeScroll.

This is a legacy Eclipse/ADT project. Its declared SDK and library references are in [project.properties](project.properties).

## Screenshot

![TestScroll3 screenshot](screenshot.png)
