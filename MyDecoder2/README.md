# MyDecoder2

Variant of the sprite-sheet word board that caches scaled letter bitmaps for small tiles and enlarged drag previews.

## Details

BitmapRegionDecoder extracts the letter regions, and cached scaled images are reused while drawing. The view keeps tile hit testing and dragging separate from the board's pan and zoom transform.

This is a legacy Eclipse/ADT project. Its declared SDK and library references are in [project.properties](project.properties).

## Screenshot

![MyDecoder2 screenshot](screenshot.png)
