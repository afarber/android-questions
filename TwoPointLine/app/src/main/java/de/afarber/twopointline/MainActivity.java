package de.afarber.twopointline;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.afarber.openmapview.CameraUpdate;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.LatLngBounds;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.PolylineOptions;

public class MainActivity extends AppCompatActivity {
    // The same Berlin coordinates as in the Stack Overflow answers.
    private static final LatLng START = new LatLng(52.507621, 13.407334);
    private static final LatLng END = new LatLng(52.527621, 13.427334);

    private OpenMapView map;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        status = findViewById(R.id.status);
        map.getUiSettings().setZoomControlsEnabled(true);

        // A direct connection between the coordinates, not a calculated road route.
        map.addPolyline(new PolylineOptions()
                .add(START, END)
                .color(Color.RED)
                .width(10f));
        map.addMarker(new MarkerOptions().position(START).title(getString(R.string.start)));
        map.addMarker(new MarkerOptions().position(END).title(getString(R.string.end)));

        map.setOnCameraMoveListener(this::showStatus);
        map.setOnCameraIdleListener(this::showStatus);
        findViewById(R.id.fit).setOnClickListener(v -> map.animateCamera(fitEndpoints()));
        // Bounds fitting needs the map's measured width and height.
        map.post(() -> map.moveCamera(fitEndpoints()));
        showStatus();
    }

    private CameraUpdate fitEndpoints() {
        LatLngBounds bounds = new LatLngBounds.Builder().include(START).include(END).build();
        int padding = Math.round(80 * getResources().getDisplayMetrics().density);
        return CameraUpdateFactory.newLatLngBounds(bounds, padding);
    }

    private void showStatus() {
        status.setText(getString(R.string.status, map.getCameraPosition().zoom));
    }
}
