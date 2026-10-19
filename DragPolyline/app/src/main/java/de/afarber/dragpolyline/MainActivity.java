package de.afarber.dragpolyline;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.afarber.openmapview.BitmapDescriptor;
import de.afarber.openmapview.BitmapDescriptorFactory;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.Marker;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OnMarkerDragListener;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.Polyline;
import de.afarber.openmapview.PolylineOptions;
import de.afarber.openmapview.SphericalUtil;

public class MainActivity extends AppCompatActivity {
    // A few points in Lahore, ending at the endPoint from the question
    private static final LatLng[] START = {
            new LatLng(31.3912, 74.2445),
            new LatLng(31.3870, 74.2530),
            new LatLng(31.382108, 74.260107),
    };

    // The polyline points and their draggable markers, in the same order
    private final List<LatLng> points = new ArrayList<>();
    private final List<Marker> vertices = new ArrayList<>();
    private OpenMapView map;
    private Polyline line;
    private BitmapDescriptor vertexIcon;
    private TextView lengthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lengthView = findViewById(R.id.length);

        map = findViewById(R.id.map);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.3875, 74.2545), 15f));
        vertexIcon = BitmapDescriptorFactory.fromResource(R.drawable.vertex);

        for (LatLng point : START) {
            points.add(point);
        }
        line = map.addPolyline(new PolylineOptions().addAll(points).color(0xFF1565C0).width(12f));
        for (LatLng point : START) {
            addVertex(point);
        }
        showLength();

        // A long press on the map adds a point at the end of the line
        map.setOnMapLongClickListener(latLng -> {
            points.add(latLng);
            addVertex(latLng);
            line.setPoints(points);
            showLength();
        });

        // The line follows a marker while it is dragged, not only when it is dropped
        map.setOnMarkerDragListener(new OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                movePoint(marker);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                movePoint(marker);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                movePoint(marker);
            }
        });
    }

    private void addVertex(LatLng point) {
        vertices.add(map.addMarker(new MarkerOptions()
                .position(point)
                .icon(vertexIcon)
                .anchor(0.5f, 0.5f)
                .draggable(true)));
    }

    // Replaces the point of the dragged marker, so the line keeps its other points
    private void movePoint(Marker marker) {
        int index = vertices.indexOf(marker);
        if (index < 0) {
            return;
        }
        points.set(index, marker.getPosition());
        line.setPoints(points);
        showLength();
    }

    private void showLength() {
        double meters = SphericalUtil.computeLength(points);
        lengthView.setText(String.format(Locale.US, "%d points, %.2f km - drag a point, long press to add one",
                points.size(), meters / 1000));
    }
}
