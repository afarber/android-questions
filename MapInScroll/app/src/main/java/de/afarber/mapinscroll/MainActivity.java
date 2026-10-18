package de.afarber.mapinscroll;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.LatLngBounds;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    private static final LatLng VIENNA = new LatLng(48.2082, 16.3738);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nothing special for the ScrollView: a drag that starts on the map pans the map,
        // a drag that starts on the form scrolls the page
        OpenMapView map = findViewById(R.id.mapBbox);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VIENNA, 12f));

        // The idle listener shows the box after each drag or zoom. moveCamera() above calls
        // it right away, before the map is laid out, so the loaded callback shows the first box
        map.setOnCameraIdleListener(this::showBoundingBox);
        map.setOnMapLoadedCallback(this::showBoundingBox);
    }

    private void showBoundingBox() {
        OpenMapView map = findViewById(R.id.mapBbox);
        TextView bbox = findViewById(R.id.bbox);
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        bbox.setText(String.format(Locale.US, "Bounding box: %.4f, %.4f - %.4f, %.4f",
                bounds.southwest.latitude, bounds.southwest.longitude,
                bounds.northeast.latitude, bounds.northeast.longitude));
    }
}
