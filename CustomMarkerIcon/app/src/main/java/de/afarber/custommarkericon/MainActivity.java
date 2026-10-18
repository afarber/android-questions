package de.afarber.custommarkericon;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import de.afarber.openmapview.BitmapDescriptor;
import de.afarber.openmapview.BitmapDescriptorFactory;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.LatLngBounds;
import de.afarber.openmapview.Marker;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.PolylineOptions;

public class MainActivity extends AppCompatActivity {
    // Approximate stops of the bus line 100 in Berlin
    private static final String[] NAMES = {
            "Zoologischer Garten", "Großer Stern", "Bundestag",
            "Brandenburger Tor", "Staatsoper", "Lustgarten", "Alexanderplatz",
    };
    private static final LatLng[] STOPS = {
            new LatLng(52.5069, 13.3323), new LatLng(52.5145, 13.3501), new LatLng(52.5186, 13.3730),
            new LatLng(52.5163, 13.3797), new LatLng(52.5170, 13.3950), new LatLng(52.5186, 13.4010),
            new LatLng(52.5219, 13.4132),
    };

    private final List<Marker> markers = new ArrayList<>();
    private boolean centered = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        float density = getResources().getDisplayMetrics().density;

        OpenMapView map = findViewById(R.id.map);
        map.addPolyline(new PolylineOptions()
                .add(STOPS)
                .color(Color.rgb(0, 191, 255))
                .width(6 * density));

        // A vector or bitmap drawable, drawn at its size in dp: no scaling needed
        BitmapDescriptor bus = BitmapDescriptorFactory.fromResource(R.drawable.bus);
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (int i = 0; i < STOPS.length; i++) {
            // The anchor is the point of the icon placed on the stop: the center of a round
            // icon, while the bottom (0.5, 1) is only right for a pin pointing down
            markers.add(map.addMarker(new MarkerOptions()
                    .position(STOPS[i])
                    .title(NAMES[i])
                    .icon(bus)
                    .anchor(0.5f, 0.5f)));
            bounds.include(STOPS[i]);
        }
        map.post(() -> map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), Math.round(48 * density))));

        // Switch between the two anchors, to compare them
        Button button = findViewById(R.id.anchor);
        button.setText("Anchor: center");
        button.setOnClickListener(v -> {
            centered = !centered;
            button.setText(centered ? "Anchor: center" : "Anchor: bottom");
            for (Marker marker : markers) {
                marker.setAnchor(0.5f, centered ? 0.5f : 1f);
            }
        });
    }
}
