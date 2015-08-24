package de.afarber.rotatedlist;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int DRAWER_CLOSE_DELAY = 250;

    public static final String[] ACCOUNT_LABELS = {
            "Google+",
            "Facebook",
            "Twitter",
            "Vk.com",
            "Odnoklassniki",
            "Mail.ru"
    };

    public static final int[] ACCOUNT_ICONS = {
            R.drawable.ic_google_plus_black_36dp,
            R.drawable.ic_facebook_black_36dp,
            R.drawable.ic_twitter_black_36dp,
            R.drawable.ic_vk_black_36dp,
            R.drawable.ic_odnoklassniki_black_36dp,
            R.drawable.ic_mail_ru_black_36dp
    };

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root, fragment, MainFragment.TAG)
                    .commit();
        }
    }

    public void showAccounts(View v) {
        final AccountFragment fragment = new AccountFragment();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.root, fragment, AccountFragment.TAG)
                        .commit();
            }
        }, DRAWER_CLOSE_DELAY);

    }


}
