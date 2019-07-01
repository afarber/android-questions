package de.afarber.vehicles;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public final static String TAG = "Vehicles";

    // https://fake-poi-api.mytaxi.com/?p1Lat={Latitude1}&p1Lon={Longitude1}&p2Lat={Latitude2}&p2Lon={Longitude2}

    public final static String URL = "https://fake-poi-api.mytaxi.com/?p1Lat=%f&p1Lon=%f&p2Lat=%f&p2Lon=%f";

    public final static float NE_LAT = 52.51570426234859f;
    public final static float NE_LNG = 13.287037834525108f;
    public final static float SW_LAT = 52.48417497476959f;
    public final static float SW_LNG = 13.251724876463415f;

    private OkHttpClient mClient;
    private MapsViewModel mViewModel;
    private Drawable mCar;
    private Drawable mTaxi;

    private final Callback mCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException ex) {
            Log.w(TAG, "mCallback onFailure", ex);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful() && response.body() != null) {
                String jsonStr = response.body().string();
                //Log.d(TAG, "mCallback onResponse jsonStr=" + jsonStr);
                List<Poi> pois = parseJson(jsonStr);
                PoiDatabase.getDao(getApplicationContext()).insertVehicles(pois);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = new OkHttpClient.Builder().build();

        mCar = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_car)
                .color(Color.BLACK)
                .sizeDp(24);

        mTaxi = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_taxi)
                .color(Color.RED)
                .sizeDp(24);

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
        LatLng cityCube = new LatLng(52.5002212, 13.2685643);
        googleMap.addMarker(new MarkerOptions().position(cityCube).title("You are here"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityCube, 14f));

        mViewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
        mViewModel.getVehicles().observe(this, new Observer<List<Poi>>() {
            @Override
            public void onChanged(List<Poi> pois) {
                if (pois != null) {
                    for (Poi poi: pois) {
                        Log.d(TAG, poi.toString());
                    }
                }
            }
        });

        fetch(NE_LAT, NE_LNG, SW_LAT, SW_LNG, mCallback);
    }

    public void fetch(float lat1, float lng1, float lat2, float lng2, Callback callback) {
        @SuppressLint("DefaultLocale")
        Request request = new Request.Builder()
                .url(String.format(URL, lat1, lng1, lat2, lng2))
                .build();

        mClient.newCall(request).enqueue(callback);
    }

    @NonNull
    private List<Poi> parseJson(@NonNull String jsonStr) {
        List<Poi> pois = new ArrayList<>();

        try {
            JSONObject rootObj = new JSONObject(jsonStr);
            JSONArray jsonArray = rootObj.getJSONArray("poiList");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                JSONObject coordObj = jsonObj.getJSONObject("coordinate");

                Poi poi = new Poi();
                poi.id = jsonObj.getInt("id");
                poi.latitude = coordObj.getDouble("latitude");
                poi.longitude = coordObj.getDouble("longitude");
                poi.fleetType = jsonObj.getString("fleetType");
                poi.heading = jsonObj.getDouble("heading");

                //Log.d(TAG, "i = " + i + ", poi = " + poi);
                pois.add(poi);
            }

        } catch (JSONException ex) {
            Log.w(TAG, "parsing JSON failed", ex);
        }

        return pois;
    }
}
