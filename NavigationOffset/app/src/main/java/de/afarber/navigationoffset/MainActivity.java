package de.afarber.navigationoffset;

import android.animation.ValueAnimator;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import de.afarber.openmapview.BitmapDescriptorFactory;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.Marker;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.PolylineOptions;

public class MainActivity extends AppCompatActivity {
    // A car route from Potsdamer Platz to Alexanderplatz in Berlin, from the public OSRM server
    private static final double[][] ROUTE = {
            {52.509959, 13.376710}, {52.510176, 13.376714}, {52.512279, 13.377175},
            {52.513173, 13.377216}, {52.514585, 13.377467}, {52.514694, 13.379160},
            {52.515157, 13.381671}, {52.515451, 13.386058}, {52.516670, 13.385822},
            {52.517138, 13.392965}, {52.517236, 13.393421}, {52.517587, 13.398930},
            {52.517681, 13.399544}, {52.518023, 13.400503}, {52.519055, 13.403118},
            {52.522634, 13.410171}, {52.523893, 13.412151}, {52.523864, 13.412333},
    };

    // A simulated location update every second, 15 m further along the route
    private static final long FIX_INTERVAL_MS = 1000;
    private static final float FIX_STEP_METERS = 15f;

    // In navigation mode the location is shown at 80 % of the map height
    private static final float LOCATION_AT = 0.8f;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<LatLng> route = new ArrayList<>();
    private OpenMapView mapView;
    private Marker car;
    private Button offsetButton;
    private boolean bottomOffset = true;
    private int paddingTop;
    private float driven;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (double[] p : ROUTE) {
            route.add(new LatLng(p[0], p[1]));
        }
        currentLocation = route.get(0);

        mapView = findViewById(R.id.map);
        mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f));
        mapView.addPolyline(new PolylineOptions().addAll(route).color(0xFF42A5F5).width(14f));
        car = mapView.addMarker(new MarkerOptions()
                .position(currentLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
                .anchor(0.5f, 0.5f));

        offsetButton = findViewById(R.id.offset);
        offsetButton.setOnClickListener(v -> {
            bottomOffset = !bottomOffset;
            applyOffset(true);
        });
        // The padding depends on the map height, which is known after the layout
        mapView.post(() -> applyOffset(false));

        handler.post(nextFix);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(nextFix);
        super.onDestroy();
    }

    // The camera target is the center of the area inside the map padding: a top padding
    // of 60 % of the height puts it at 80 %. The padding is kept, so every following
    // camera update shows the location there, with no scrollBy() and no second animation.
    private void applyOffset(boolean animated) {
        int target = bottomOffset ? (int) (mapView.getHeight() * (2 * LOCATION_AT - 1)) : 0;
        offsetButton.setText(bottomOffset ? "Location: bottom" : "Location: center");
        if (!animated) {
            setPaddingTop(target);
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(paddingTop, target);
        animator.setDuration(500);
        animator.addUpdateListener(a -> setPaddingTop((int) a.getAnimatedValue()));
        animator.start();
    }

    private void setPaddingTop(int top) {
        paddingTop = top;
        mapView.setMapPadding(0, top, 0, 0);
    }

    // A new location fix: glide to it in the time until the next fix, moving the marker
    // and the camera together in every frame
    private final Runnable nextFix = new Runnable() {
        @Override
        public void run() {
            float from = driven;
            driven = driven + FIX_STEP_METERS >= routeLength() ? 0f : driven + FIX_STEP_METERS;
            ValueAnimator animator = ValueAnimator.ofFloat(from, driven);
            animator.setDuration(FIX_INTERVAL_MS);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(a -> {
                float distance = (float) a.getAnimatedValue();
                currentLocation = positionAt(distance);
                // The map is north up, so the arrow turns instead of the map
                car.setRotation(headingAt(distance));
                car.setPosition(currentLocation);
                mapView.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            });
            animator.start();
            handler.postDelayed(this, FIX_INTERVAL_MS);
        }
    };

    private float segmentLength(int i, float[] results) {
        LatLng a = route.get(i);
        LatLng b = route.get(i + 1);
        Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results);
        return results[0];
    }

    private float routeLength() {
        float[] results = new float[2];
        float length = 0f;
        for (int i = 0; i < route.size() - 1; i++) {
            length += segmentLength(i, results);
        }
        return length;
    }

    private LatLng positionAt(float distance) {
        float[] results = new float[2];
        for (int i = 0; i < route.size() - 1; i++) {
            float length = segmentLength(i, results);
            if (distance <= length) {
                double f = distance / length;
                LatLng a = route.get(i);
                LatLng b = route.get(i + 1);
                return new LatLng(a.latitude + (b.latitude - a.latitude) * f,
                        a.longitude + (b.longitude - a.longitude) * f);
            }
            distance -= length;
        }
        return route.get(route.size() - 1);
    }

    private float headingAt(float distance) {
        float[] results = new float[2];
        for (int i = 0; i < route.size() - 1; i++) {
            float length = segmentLength(i, results);
            if (distance < length) {
                return results[1];
            }
            distance -= length;
        }
        return results[1];
    }
}
