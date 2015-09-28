package de.afarber.mylogin;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class Utils {
    public static final int UNKNOWN       = -1;
    public static final int GOOGLE        = 0;
    public static final int FACEBOOK      = 4;
    public static final int TWITTER       = 5;

    public static final String[] SOCIAL_LABELS = {
            "Google",
            "Facebook",
            "Twitter"
    };

    public static final int[] SOCIAL_COLORS = {
            0xFFDD5555,
            0xFF336699,
            0xFF55AAEE
    };

    public static final int[] SOCIAL_ICONS = {
        R.drawable.ic_google_plus_grey600_24dp,
        R.drawable.ic_facebook_grey600_24dp,
        R.drawable.ic_twitter_grey600_24dp
    };

    public static final Class[] SOCIAL_FRAGMENTS = {
        de.afarber.mylogin.GoogleFragment.class,
        de.afarber.mylogin.FacebookFragment.class,
        de.afarber.mylogin.TwitterFragment.class
    };

    public static void showToast(final Context context, final String str) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,
                        str,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToast(final Context context, int res) {
        final String str = context.getString(res);
        showToast(context, str);
    }
}

