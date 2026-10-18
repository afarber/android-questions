package de.afarber.fitallmarkers;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import de.afarber.openmapview.CameraUpdate;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.LatLngBounds;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    // Added one by one with the button
    private static final LatLng[] MORE_WAYPOINTS = {
            new LatLng(48.1351, 11.5820),   // Munich
            new LatLng(50.9375, 6.9603),    // Cologne
            new LatLng(55.6761, 12.5683),   // Copenhagen
            new LatLng(48.2082, 16.3738),   // Vienna
    };

    private final List<LatLng> waypoints = new ArrayList<>();
    private OpenMapView map;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        add = findViewById(R.id.add);
        addWaypoint(new LatLng(53.5511, 9.9937));   // Hamburg
        addWaypoint(new LatLng(52.5200, 13.4050));  // Berlin

        // In onCreate() the map has no size yet, so fit the markers once it is laid out
        map.post(() -> map.moveCamera(fitAllWaypoints()));

        // Each new waypoint makes the camera zoom out to show all of them
        add.setOnClickListener(v -> {
            addWaypoint(MORE_WAYPOINTS[waypoints.size() - 2]);
            map.animateCamera(fitAllWaypoints(), 1000);
        });
    }

    private void addWaypoint(LatLng point) {
        waypoints.add(point);
        map.addMarker(new MarkerOptions().position(point).title("Waypoint " + waypoints.size()));
        add.setText("Add waypoint (" + waypoints.size() + " shown)");
        add.setEnabled(waypoints.size() - 2 < MORE_WAYPOINTS.length);
    }

    private CameraUpdate fitAllWaypoints() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : waypoints) {
            builder.include(point);
        }
        // The padding keeps the marker icons, which stand above their points, on the screen
        int padding = Math.round(64 * getResources().getDisplayMetrics().density);
        return CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
    }
}
