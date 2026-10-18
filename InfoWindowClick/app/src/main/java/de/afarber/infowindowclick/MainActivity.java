package de.afarber.infowindowclick;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.MarkerOptions;
import de.afarber.openmapview.OpenMapView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenMapView map = findViewById(R.id.map);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.5462, 9.9860), 15f));

        // A tap on a marker opens its info window with the title and the snippet
        addPlace(map, 53.5413, 9.9841, "Elbphilharmonie", "Concert hall, opened in 2017");
        addPlace(map, 53.5503, 9.9920, "Rathaus", "Town hall, finished in 1897");
        addPlace(map, 53.5482, 9.9790, "St. Michaelis", "Church with a 132 m tower");

        // A tap on an open info window
        map.setOnInfoWindowClickListener(marker -> new AlertDialog.Builder(this)
                .setTitle(marker.getTitle())
                .setMessage("You have clicked the info window of " + marker.getTitle())
                .setPositiveButton(android.R.string.ok, null)
                .show());
    }

    private static void addPlace(OpenMapView map, double latitude, double longitude, String title, String snippet) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(snippet)
                .anchor(0.5f, 1f));
    }
}
