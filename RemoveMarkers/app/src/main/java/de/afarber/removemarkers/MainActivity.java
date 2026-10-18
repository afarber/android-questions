package de.afarber.removemarkers;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedHashMap;
import java.util.Map;

import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.Marker;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    private static final Map<String, LatLng> PLACES = new LinkedHashMap<>();

    static {
        PLACES.put("Marker", new LatLng(41.3746312, 19.7710733));
        PLACES.put("Skanderbeg Square", new LatLng(41.3275, 19.8187));
        PLACES.put("Airport", new LatLng(41.4147, 19.7206));
        PLACES.put("Dajti Ekspres", new LatLng(41.3531, 19.8917));
        PLACES.put("Grand Park", new LatLng(41.3150, 19.8180));
    }

    // The markers on the map, by their id, so that a single one can be removed
    private final Map<String, Marker> markers = new LinkedHashMap<>();
    private OpenMapView map;
    private TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        label = findViewById(R.id.label);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.3700, 19.8000), 11.5f));
        addAll();

        // A tap on a marker removes just that marker
        map.setOnMarkerClickListener(marker -> {
            removeMarker((String) marker.getTag());
            return true;   // no info window, no camera move
        });

        findViewById(R.id.removeAll).setOnClickListener(v -> {
            map.clearMarkers();
            markers.clear();
            updateLabel();
        });
        findViewById(R.id.addAll).setOnClickListener(v -> addAll());
    }

    private void addAll() {
        for (Map.Entry<String, LatLng> place : PLACES.entrySet()) {
            String id = place.getKey();
            if (!markers.containsKey(id)) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(place.getValue())
                        .title(id)
                        .tag(id));
                markers.put(id, marker);
            }
        }
        updateLabel();
    }

    private void removeMarker(String id) {
        Marker marker = markers.remove(id);
        if (marker != null) {
            marker.remove();
        }
        updateLabel();
    }

    private void updateLabel() {
        label.setText(markers.size() + " markers - tap one to remove it");
    }
}
