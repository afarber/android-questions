package de.afarber.customzoom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    private static final LatLng BRANDENBURG_GATE = new LatLng(52.516275, 13.377704);
    private static final float MIN_ZOOM = 12f;
    private static final float MAX_ZOOM = 14f;

    private OpenMapView map;
    private TextView zoomText;
    private Button zoomIn;
    private Button zoomOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        zoomText = findViewById(R.id.zoomText);
        zoomIn = findViewById(R.id.zoomIn);
        zoomOut = findViewById(R.id.zoomOut);

        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
        map.setMinZoomPreference(MIN_ZOOM);
        map.setMaxZoomPreference(MAX_ZOOM);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BRANDENBURG_GATE, 13f));
        map.addMarker(new MarkerOptions().position(BRANDENBURG_GATE).title(getString(R.string.landmark)));

        // Connect each custom button to its camera update explicitly.
        zoomIn.setOnClickListener(v -> map.animateCamera(CameraUpdateFactory.zoomIn()));
        zoomOut.setOnClickListener(v -> map.animateCamera(CameraUpdateFactory.zoomOut()));

        // Wait for the current camera move before accepting another button press.
        map.setOnCameraMoveStartedListener(reason -> {
            zoomIn.setEnabled(false);
            zoomOut.setEnabled(false);
        });
        map.setOnCameraMoveListener(this::showZoom);
        map.setOnCameraIdleListener(this::updateControls);
        // Interrupting an animation reports cancellation instead of an idle event.
        map.setOnCameraMoveCanceledListener(this::updateControls);
        updateControls();
    }

    private void showZoom() {
        zoomText.setText(getString(R.string.zoom_status, map.getCameraPosition().zoom));
    }

    private void updateControls() {
        showZoom();
        float zoom = map.getCameraPosition().zoom;
        zoomIn.setEnabled(zoom < MAX_ZOOM);
        zoomOut.setEnabled(zoom > MIN_ZOOM);
    }
}
