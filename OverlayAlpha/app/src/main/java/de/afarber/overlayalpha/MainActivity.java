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
                // The overlay is a live handle: the map redraws it at once, keeping its tiles
                topoOverlay.setTransparency(1f - progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void showAlpha(int alpha) {
        alphaText.setText("Topo layer alpha: " + alpha + "%");
    }
}
