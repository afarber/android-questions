package de.afarber.livemarker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import de.afarber.openmapview.BitmapDescriptorFactory;
import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.Marker;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private OpenMapView map;
    private TextView status;
    private Button action;
    private LocationManager locations;
    private Marker marker;
    private LatLng position;
    private boolean resumed;
    private boolean requestedPermission;
    private boolean listening;
    private boolean providerEnabled;
    private boolean receivedFix;
    private boolean centeredOnce;

    // This still arrives when every provider is off and there is no active subscription.
    private final BroadcastReceiver providerChanges = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) { startUpdates(); }
    };

    // Implement all callbacks for devices running below API 30, too.
    private final LocationListener listener = new LocationListener() {
        @Override public void onLocationChanged(@NonNull Location location) { showLocation(location); }
        @Override public void onProviderEnabled(@NonNull String provider) { }
        @Override public void onProviderDisabled(@NonNull String provider) { }
        @Override public void onStatusChanged(String provider, int state, Bundle extras) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = findViewById(R.id.map);
        status = findViewById(R.id.status);
        action = findViewById(R.id.action);
        locations = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestedPermission = savedInstanceState != null && savedInstanceState.getBoolean("requestedPermission");
        action.setOnClickListener(v -> {
            if (!hasPermission()) {
                if (requestedPermission && !ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS[0])
                        && !ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS[1])) {
                    openAppSettings();
                } else {
                    requestPermission();
                }
            } else if (!providerEnabled) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } else if (!listening) {
                openAppSettings();
            } else if (receivedFix && position != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        ContextCompat.registerReceiver(this, providerChanges,
                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED);
        if (!hasPermission() && !requestedPermission) {
            requestPermission();
        } else {
            startUpdates();
        }
    }

    @Override
    protected void onPause() {
        resumed = false;
        unregisterReceiver(providerChanges);
        stopUpdates();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("requestedPermission", requestedPermission);
        super.onSaveInstanceState(outState);
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        requestedPermission = true;
        updateStatus();
        ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == LOCATION_PERMISSION) {
            startUpdates();
        }
    }

    // Permission can also be revoked while the app is running. Handle each provider separately.
    @SuppressLint("MissingPermission")
    private void startUpdates() {
        stopUpdates();
        providerEnabled = false;
        if (resumed && hasPermission() && locations != null) {
            for (String provider : new String[]{LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER}) {
                try {
                    if (locations.isProviderEnabled(provider)) {
                        providerEnabled = true;
                        locations.requestLocationUpdates(provider, 1000, 0, listener, Looper.getMainLooper());
                        listening = true;
                    }
                } catch (SecurityException | IllegalArgumentException ignored) {
                    // Another provider may still be usable with the granted permission.
                }
            }
        }
        updateStatus();
    }

    private void stopUpdates() {
        if (locations != null) {
            locations.removeUpdates(listener);
        }
        listening = false;
        receivedFix = false;
    }

    private void showLocation(Location location) {
        if (!resumed || !hasPermission()) return;
        LatLng latest = new LatLng(location.getLatitude(), location.getLongitude());
        String coordinates = getString(R.string.coordinates, latest.latitude, latest.longitude);
        if (marker == null) {
            // A shape drawable needs an explicit size; fromResource renders it at that size.
            marker = map.addMarker(new MarkerOptions().position(latest)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerosm))
                    .anchor(0.5f, 0.5f).title(getString(R.string.marker_title)).snippet(coordinates));
        } else if (!latest.equals(position)) {
            marker.setPosition(latest);
            marker.setSnippet(coordinates);
        }
        position = latest;
        receivedFix = true;
        if (!centeredOnce) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16f));
            centeredOnce = true;
        }
        updateStatus();
    }

    private void updateStatus() {
        action.setEnabled(true);
        if (marker != null) marker.setVisible(hasPermission());
        if (!hasPermission()) {
            status.setText(R.string.permission_required);
            action.setText(R.string.enable_location);
        } else if (!providerEnabled) {
            status.setText(marker == null ? R.string.location_disabled : R.string.location_disabled_last_fix);
            action.setText(R.string.location_settings);
        } else if (!listening) {
            status.setText(R.string.no_provider);
            action.setText(R.string.app_settings);
        } else if (!receivedFix) {
            status.setText(marker == null ? R.string.waiting : R.string.waiting_last_fix);
            action.setText(R.string.center);
            action.setEnabled(false);
        } else {
            boolean precise = ActivityCompat.checkSelfPermission(this, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED;
            status.setText(getString(R.string.location_status,
                    getString(precise ? R.string.precise : R.string.approximate), position.latitude, position.longitude));
            action.setText(R.string.center);
        }
    }

    private void openAppSettings() {
        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
    }
}
