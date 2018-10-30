package de.afarber.topplayers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private static final int CACHE_SIZE = 30 * 1024 * 1024; // 30 MB
    private static OkHttpClient sClient;
    private static Picasso sPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (sClient == null && sPicasso == null) {
            init();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TopFragment.newInstance())
                    .commitNow();
        }
    }

    private void init() {
        sClient = new OkHttpClient.Builder()
                .cache(new Cache(getCacheDir(), CACHE_SIZE))
                .build();

        sPicasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(sClient))
                .build();

        sPicasso.setIndicatorsEnabled(true);
        sPicasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(sPicasso);
    }

    public static void fetch(Request request, Callback responseCallback) {
        sClient.newCall(request).enqueue(responseCallback);
    }

    public static Picasso getPicasso() {
        return sPicasso;
    }
}
