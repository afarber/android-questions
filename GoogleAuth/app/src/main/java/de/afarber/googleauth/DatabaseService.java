package de.afarber.googleauth;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class DatabaseService extends IntentService {
    public static final String TAG = DatabaseService.class.getName();

    public static final String ACTION_FIND_GOOGLE_USER = "de.afarber.action.find.google.user";
    public static final String ACTION_GOOGLE_USER_EXISTS = "de.afarber.action.google.user.exists";
    public static final String ACTION_GOOGLE_USER_MISSING = "de.afarber.action.google.user.missing";

    public static final String ACTION_FIND_NEWEST_USER = "de.afarber.action.find.newest.user";
    public static final String ACTION_NEWEST_USER_DATA = "de.afarber.action.newest.user.data";

    public static final String ACTION_DELETE_ALL = "de.afarber.delete.all";
    public static final String ACTION_PRINT_ALL = "de.afarber.print.all";

    public static final String ACTION_UPDATE_USER = "de.afarber.action.update.user";
    public static final String EXTRA_USER = "de.afarber.extra.user";

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

    public static void updateUser(Context context, User user) {
        Intent i = new Intent(context, DatabaseService.class);
        i.setAction(ACTION_UPDATE_USER);
        i.putExtra(EXTRA_USER, user);
        context.startService(i);
    }

    public static void deleteAll(Context context) {
        Intent i = new Intent(context, DatabaseService.class);
        i.setAction(ACTION_DELETE_ALL);
        context.startService(i);
    }

    public static void printAll(Context context) {
        Intent i = new Intent(context, DatabaseService.class);
        i.setAction(ACTION_PRINT_ALL);
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
        } else if (ACTION_UPDATE_USER.equals(action)) {
            User user = i.getParcelableExtra(EXTRA_USER);
            handleUpdateUser(user);
        } else if (ACTION_DELETE_ALL.equals(action)) {
            handleDeleteAll();
        } else if (ACTION_PRINT_ALL.equals(action)) {
            handlePrintAll();
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
        User user = mDatabaseHelper.findNewestUser();
        Intent i = new Intent();
        i.setAction(ACTION_NEWEST_USER_DATA);
        i.putExtra(EXTRA_USER, user);
        mBroadcastManager.sendBroadcast(i);
    }

    private void handleUpdateUser(User user) {
        mDatabaseHelper.updateUser(user);
    }

    private void handleDeleteAll() {
        mDatabaseHelper.deleteAll();
    }

    private void handlePrintAll() {
        mDatabaseHelper.printAll();
    }
}
