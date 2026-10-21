# MyScroll

ListView experiment appending one item per second and clearing the list with a button or double-tap.

## Details

A background task posts additions to the UI thread. The list is configured to stack from the bottom, and a gesture detector handles the double-tap clear action.

This is a legacy Eclipse/ADT project. Its declared SDK and library references are in [project.properties](project.properties).

## Screenshot

![MyScroll screenshot](screenshot.png)
