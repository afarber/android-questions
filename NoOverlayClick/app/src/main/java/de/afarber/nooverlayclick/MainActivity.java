package de.afarber.nooverlayclick;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.Marker;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.Polygon;
import de.afarber.openmapview.PolygonOptions;
import de.afarber.openmapview.Polyline;
import de.afarber.openmapview.PolylineOptions;

public class MainActivity extends AppCompatActivity {
    private static final LatLng GATE = new LatLng(52.516275, 13.377704);
    private TextView status;
    private CheckBox suppress;
    private int mapTaps;
    private int routeTaps;
    private int polygonTaps;
    private int markerTaps;
    private String lastEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = findViewById(R.id.status);
        suppress = findViewById(R.id.suppress);
        lastEvent = getString(R.string.tap_hint);

        OpenMapView map = findViewById(R.id.map);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(GATE, 15f));

        // An illustrative route west of the marker, separate from the polygon.
        Polyline route = map.addPolyline(new PolylineOptions()
                .add(new LatLng(52.5180, 13.3630), new LatLng(52.5150, 13.3630),
                        new LatLng(52.5140, 13.3680))
                .color(0xFF1565C0).width(18f).clickable(false));
        Polygon area = map.addPolygon(new PolygonOptions()
                .add(new LatLng(52.5180, 13.3840), new LatLng(52.5180, 13.3910),
                        new LatLng(52.5140, 13.3910), new LatLng(52.5140, 13.3840))
                .strokeColor(0xFFE65100).strokeWidth(6f).fillColor(0x55FF9800).clickable(false));
        Marker marker = map.addMarker(new MarkerOptions().position(GATE)
                .title(getString(R.string.landmark)).snippet(getString(R.string.marker_snippet)));

        map.setOnMapClickListener(position -> {
            mapTaps++;
            showEvent(R.string.map_event);
        });
        map.setOnPolylineClickListener(line -> {
            routeTaps++;
            showEvent(R.string.route_event);
        });
        map.setOnPolygonClickListener(polygon -> {
            polygonTaps++;
            showEvent(R.string.polygon_event);
        });
        map.setOnMarkerClickListener(clicked -> {
            markerTaps++;
            showEvent(suppress.isChecked() ? R.string.marker_consumed : R.string.marker_default);
            // true consumes the click; false allows the info window and camera movement.
            return suppress.isChecked();
        });
        suppress.setOnCheckedChangeListener((button, checked) -> {
            route.setClickable(!checked);
            area.setClickable(!checked);
            if (checked) {
                marker.hideInfoWindow();
            }
            showEvent(checked ? R.string.suppression_on : R.string.suppression_off);
        });
        showStatus();
    }

    private void showEvent(int message) {
        lastEvent = getString(message);
        showStatus();
    }

    private void showStatus() {
        status.setText(getString(R.string.status, lastEvent, mapTaps, routeTaps, polygonTaps, markerTaps));
    }
}
