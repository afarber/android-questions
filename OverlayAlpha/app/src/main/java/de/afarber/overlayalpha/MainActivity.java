package de.afarber.overlayalpha;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.TileOverlay;
import de.afarber.openmapview.TileOverlayOptions;
import de.afarber.openmapview.UrlTileProvider;

public class MainActivity extends AppCompatActivity {
    private OpenMapView map;
    private TextView alphaText;
    private TileOverlay topoOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alphaText = findViewById(R.id.alphaText);
        map = findViewById(R.id.map);
        getLifecycle().addObserver(map);

        // The base map shows the standard OpenStreetMap tiles
        map.setCenter(new LatLng(47.4210, 10.9863));
        map.setZoom(14f);

        // The topo layer is drawn on top of the base map
        topoOverlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(new UrlTileProvider("https://a.tile.opentopomap.org/{z}/{x}/{y}.png") {})
                .transparency(0.5f));
        showAlpha(50);

        SeekBar alphaSeekBar = findViewById(R.id.alphaSeekBar);
        alphaSeekBar.setProgress(50);
        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // The overlay is immutable, so replace it by a copy with the new transparency
                map.removeTileOverlay(topoOverlay);
                topoOverlay = map.addTileOverlay(topoOverlay.withTransparency(1f - seekBar.getProgress() / 100f));
            }
        });
    }

    private void showAlpha(int alpha) {
        alphaText.setText("Topo layer alpha: " + alpha + "%");
    }
}
