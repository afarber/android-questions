package de.afarber.worldwrap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.LatLngBounds;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.PolylineOptions;

public class MainActivity extends AppCompatActivity {
    private static final LatLng BEIJING = new LatLng(39.9042, 116.4074);
    private static final LatLng SAN_FRANCISCO = new LatLng(37.7749, -122.4194);

    // The camera target stays within one world, so panning stops just before the date line
    private static final LatLngBounds ONE_WORLD = new LatLngBounds(new LatLng(-85, -179), new LatLng(85, 179));

    private OpenMapView map;
    private TextView myTxt;
    private Button limitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No Configuration.load() and no User-Agent setup needed
        setContentView(R.layout.activity_main);
        myTxt = findViewById(R.id.myTxt);

        // The zoom levels from the question. Like Google Maps, the map repeats only
        // horizontally, so the world can be panned around. There is no copy above
        // the north pole or below the south pole.
        map = findViewById(R.id.map);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMinZoomPreference(2f);
        map.setMaxZoomPreference(3f);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BEIJING, 2f));

        // Markers and lines are drawn on every copy, the flight continues over the Pacific
        map.addMarker(new MarkerOptions().position(BEIJING).title("Beijing"));
        map.addMarker(new MarkerOptions().position(SAN_FRANCISCO).title("San Francisco"));
        map.addPolyline(new PolylineOptions().add(BEIJING, SAN_FRANCISCO).geodesic(true).color(0xFF1565C0).width(8f));

        map.setOnCameraMoveListener(this::showTarget);
        map.setOnCameraIdleListener(this::showTarget);

        limitBtn = findViewById(R.id.limitBtn);
        limitBtn.setOnClickListener(v -> toggleLimit());
        showLimit();
    }

    private void toggleLimit() {
        map.setLatLngBoundsForCameraTarget(map.getLatLngBoundsForCameraTarget() == null ? ONE_WORLD : null);
        showLimit();
    }

    private void showLimit() {
        limitBtn.setText(map.getLatLngBoundsForCameraTarget() == null ? "Stop at the date line" : "Pan around the world");
    }

    private void showTarget() {
        LatLng target = map.getCameraPosition().target;
        myTxt.setText(String.format(Locale.US, "Camera target: %.1f, %.1f", target.latitude, target.longitude));
    }
}
