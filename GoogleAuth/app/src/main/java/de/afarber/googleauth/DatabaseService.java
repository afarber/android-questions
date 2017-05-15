package de.afarber.googleauth;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import static de.afarber.googleauth.DatabaseHelper.COLUMN_GIVEN;
import static de.afarber.googleauth.DatabaseHelper.COLUMN_PHOTO;

public class DatabaseService extends IntentService {
    public static final String TAG = DatabaseService.class.getName();

    public static final String ACTION_FIND_GOOGLE_USER = "de.afarber.action.find.google.user";
    public static final String ACTION_GOOGLE_USER_EXISTS = "de.afarber.action.google.user.exists";
    public static final String ACTION_GOOGLE_USER_MISSING = "de.afarber.action.google.user.missing";

    public static final String ACTION_FIND_NEWEST_USER = "de.afarber.action.find.newest.user";
    public static final String ACTION_NEWEST_USER_DATA = "de.afarber.action.newest.user.data";

    private LocalBroadcastManager mBroadcastManager;
    private DatabaseHelper mDatabaseHelper;

    public static void findGoogleUser(Context context) {
        Intent i = new Intent(context, DatabaseService.class);
        i.setAction(ACTION_FIND_GOOGLE_USER);
        context.startService(i);
    }

    public static void findNewestUser(Context context) {
        Intent i = new Intent(context, DatabaseService.class);
        i.setAction(ACTION_FIND_NEWEST_USER);
        context.startService(i);
    }

    public DatabaseService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent i) {
        if (i == null)
            return;

        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        mDatabaseHelper = new DatabaseHelper(this);

        final String action = i.getAction();
        if (ACTION_FIND_GOOGLE_USER.equals(action)) {
            handleFindGoogleUser();
        } else if (ACTION_FIND_NEWEST_USER.equals(action)) {
            handleFindNewestUser();
        }
    }

    public void onDestroy() {
        mDatabaseHelper.close();
        super.onDestroy();
    }

    private void handleFindGoogleUser() {
        Intent i = new Intent();
        i.setAction(mDatabaseHelper.findGoogleUser() ? ACTION_GOOGLE_USER_EXISTS : ACTION_GOOGLE_USER_MISSING);
        mBroadcastManager.sendBroadcast(i);
    }

    private void handleFindNewestUser() {
        Intent i = new Intent();
        i.setAction(ACTION_NEWEST_USER_DATA);
        User user = mDatabaseHelper.findNewestUser();
        i.putExtra(COLUMN_GIVEN, user.given);
        i.putExtra(COLUMN_PHOTO, user.photo);
        mBroadcastManager.sendBroadcast(i);
    }
}
