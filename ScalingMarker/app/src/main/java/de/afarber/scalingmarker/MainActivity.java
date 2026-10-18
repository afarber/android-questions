package de.afarber.scalingmarker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.afarber.openmapview.BitmapDescriptorFactory;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.CircleOptions;
import de.afarber.openmapview.GroundOverlayOptions;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    private static final LatLng BRANDENBURG_GATE = new LatLng(52.5163, 13.3777);
    private static final LatLng REICHSTAG = new LatLng(52.5186, 13.3762);
    private static final LatLng TIERGARTEN = new LatLng(52.5163, 13.3745);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenMapView map = findViewById(R.id.map);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.5172, 13.3765), 14f));

        // A Marker icon keeps its size in pixels at every zoom level
        map.addMarker(new MarkerOptions().position(TIERGARTEN).title("Marker"));

        // A ground overlay is sized in meters, so it grows and shrinks with the map:
        // the round icon is 60 m wide and centered on the Brandenburg Gate
        map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.spot))
                .position(BRANDENBURG_GATE, 60f)
                .clickable(true)
                .tag("Brandenburg Gate"));

        // A circle with a radius of 60 m
        map.addCircle(new CircleOptions()
                .center(REICHSTAG)
                .radius(60f)
                .strokeColor(0xFF1565C0)
                .strokeWidth(6f)
                .fillColor(0x551E88E5)
                .clickable(true)
                .tag("Reichstag"));

        // Both are clickable, with their own listeners
        TextView label = findViewById(R.id.label);
        map.setOnGroundOverlayClickListener(overlay -> label.setText("Tapped: " + overlay.getTag()));
        map.setOnCircleClickListener(circle -> label.setText("Tapped: " + circle.getTag()));
    }
}
