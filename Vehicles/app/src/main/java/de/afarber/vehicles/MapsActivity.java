package de.afarber.vehicles;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public final static String TAG = "Vehicles";

    public final static String URL = "https://fake-poi-api.mytaxi.com/?p1Lat=%f&p1Lon=%f&p2Lat=%f&p2Lon=%f";

    public final static float NE_LAT = 52.51570426234859f;
    public final static float NE_LNG = 13.287037834525108f;
    public final static float SW_LAT = 52.48417497476959f;
    public final static float SW_LNG = 13.251724876463415f;

    private GoogleMap mMap;
    private OkHttpClient mClient;

    private final Callback mCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException ex) {
            Log.w(TAG, "mCallback onFailure", ex);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful() && response.body() != null) {
                String jsonStr = response.body().string();
                Log.d(TAG, "mCallback onResponse jsonStr=" + jsonStr);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = new OkHttpClient.Builder().build();

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng cityCube = new LatLng(52.5002212, 13.2685643);
        mMap.addMarker(new MarkerOptions().position(cityCube).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityCube, 14f));

        // https://fake-poi-api.mytaxi.com/?p1Lat={Latitude1}&p1Lon={Longitude1}&p2Lat={Latitude2}&p2Lon={Longitude2}

        fetch(NE_LAT, NE_LNG, SW_LAT, SW_LNG, mCallback);
    }

    public void fetch(float lat1, float lng1, float lat2, float lng2, Callback callback) {
        //FormBody.Builder builder = new FormBody.Builder().add(KEY_UID, String.valueOf(uid));

        Request request = new Request.Builder()
                .url(String.format(URL, lat1, lng1, lat2, lng2))
                //.post(builder.build())
                .build();

        mClient.newCall(request).enqueue(callback);
    }
}
