# MyActionBar

Legacy framework ActionBar example showing an indeterminate progress indicator while the Activity is resumed.

## Details

The Activity requests the ActionBar and indeterminate-progress window features before inflating its layout. onResume shows the progress indicator and onPause hides it; the settings menu is a placeholder.

This is a legacy Eclipse/ADT project. Its declared SDK and library references are in [project.properties](project.properties).

## Screenshot

![MyActionBar screenshot](screenshot.png)
