package de.afarber.fitroute;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import de.afarber.openmapview.CameraUpdate;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.LatLngBounds;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.PolylineOptions;

public class MainActivity extends AppCompatActivity {
    private static final LatLng ORIGIN = new LatLng(52.24896, 0.71795);
    private static final LatLng DESTINATION = new LatLng(54.27916, -1.9732);

    private OpenMapView map;
    private CameraUpdate fitRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.getUiSettings().setZoomControlsEnabled(true);
        // The zoom levels of the tiles from the question
        map.setMinZoomPreference(7f);
        map.setMaxZoomPreference(13f);

        List<LatLng> route = readRoute();
        map.addPolyline(new PolylineOptions().addAll(route).color(0xFF1565C0).width(10f));
        map.addMarker(new MarkerOptions().position(ORIGIN).title("Origin"));
        map.addMarker(new MarkerOptions().position(DESTINATION).title("Destination"));

        // The bounds of the whole route, not only of its two ends
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng point : route) {
            builder.include(point);
        }
        int padding = (int) (48 * getResources().getDisplayMetrics().density);
        fitRoute = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);

        // Called in onCreate(), before the map has a size: the map keeps the update and
        // applies it after its layout, so no setMinZoomLevel() or OnFirstLayoutListener is needed
        map.moveCamera(fitRoute);

        findViewById(R.id.fit).setOnClickListener(v -> map.animateCamera(fitRoute, 1000));
    }

    // The route as "latitude,longitude" lines, e.g. from OSRM
    private List<LatLng> readRoute() {
        List<LatLng> route = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("route.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(",");
                route.add(new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return route;
    }
}
