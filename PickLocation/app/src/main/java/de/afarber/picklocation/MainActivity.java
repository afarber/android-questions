package de.afarber.picklocation;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.Marker;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    private static final LatLng START_POINT = new LatLng(43.650, 7.00517);

    private OpenMapView mapView;
    private TextView et_long, et_lat;
    private Marker etape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_long = findViewById(R.id.edit_long_etape);
        et_lat = findViewById(R.id.edit_lat_etape);

        // No Configuration.load() and no onPause()/onResume() forwarding needed
        mapView = findViewById(R.id.map_form_etape);
        mapView.getUiSettings().setZoomControlsEnabled(true);
        mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(START_POINT, 18f));

        // Called for taps on the map, but not for taps on a marker
        mapView.setOnMapClickListener(this::pickLocation);
    }

    private void pickLocation(LatLng p) {
        et_lat.setText(String.format(Locale.US, "%.5f", p.latitude));
        et_long.setText(String.format(Locale.US, "%.5f", p.longitude));

        // One marker for the picked location: add it on the first tap, then move it
        if (etape == null) {
            etape = mapView.addMarker(new MarkerOptions().position(p).title("Étape"));
        } else {
            etape.setPosition(p);
        }
    }
}
