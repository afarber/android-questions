package de.afarber.mylogin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            SocialFragment fragment = new SocialFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root, fragment, SocialFragment.TAG)
                    .commit();
        }
    }

}
