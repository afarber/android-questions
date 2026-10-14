package de.afarber.zoomtolocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import de.afarber.openmapview.BitmapDescriptor;
import de.afarber.openmapview.BitmapDescriptorFactory;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.Marker;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private OpenMapView map;
    private LocationManager locationManager;
    private BitmapDescriptor redDot;
    private Marker userMarker;
    private boolean hasCenteredOnce = false;

    private final LocationListener locationListener = this::showUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        getLifecycle().addObserver(map);
        map.getUiSettings().setZoomControlsEnabled(true);

        redDot = BitmapDescriptorFactory.INSTANCE.fromBitmap(createRedDot(60));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasLocationPermission()) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (hasLocationPermission()) {
            startLocationUpdates();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        for (String provider : locationManager.getProviders(true)) {
            Location last = locationManager.getLastKnownLocation(provider);
            if (last != null) {
                showUserLocation(last);
            }
            locationManager.requestLocationUpdates(provider, 2000, 2, locationListener);
        }
    }

    private void showUserLocation(@NonNull Location location) {
        LatLng userPoint = new LatLng(location.getLatitude(), location.getLongitude());

        if (userMarker != null) {
            map.removeMarker(userMarker);
        }
        userMarker = map.addMarker(new MarkerOptions()
                .position(userPoint)
                .title("You are here")
                .icon(redDot)
                .anchor(0.5f, 0.5f));

        // Zoom in only on the first fix, so that the user can pan the map afterwards
        if (!hasCenteredOnce) {
            map.animateCamera(CameraUpdateFactory.INSTANCE.newLatLngZoom(userPoint, 17f));
            hasCenteredOnce = true;
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private static Bitmap createRedDot(int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawCircle(size / 2f, size / 2f, size / 2.5f, paint);
        return bitmap;
    }
}
