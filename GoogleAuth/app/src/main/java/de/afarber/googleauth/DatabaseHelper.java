package de.afarber.googleauth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHelper extends SQLiteAssetHelper {
    private static final int DATABASE_VERSION   = 2;
    private static final String DATABASE_NAME   = "social.db";
    private static final String TABLE_SOCIAL    = "social";

    public static final String COLUMN_SID       = "sid";
    public static final String COLUMN_NET       = "net";
    public static final String COLUMN_GIVEN     = "given";
    public static final String COLUMN_FAMILY    = "family";
    public static final String COLUMN_PHOTO     = "photo";
    public static final String COLUMN_LAT       = "lat";
    public static final String COLUMN_LNG       = "lng";
    public static final String COLUMN_STAMP     = "stamp";

    public static final int UNKNOWN       = 0;
    public static final int GOOGLE        = 1;
    public static final int APPLE         = 2;
    public static final int ODNOKLASSNIKI = 4;
    public static final int MAILRU        = 8;
    public static final int VKONTAKTE     = 16;
    public static final int FACEBOOK      = 32;

    private static final String[] COLUMNS_SOCIAL = new String[] {
            COLUMN_SID,
            COLUMN_NET,
            COLUMN_GIVEN,
            COLUMN_FAMILY,
            COLUMN_PHOTO,
            COLUMN_LAT,
            COLUMN_LNG,
            COLUMN_STAMP
    };

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("pragma foreign_keys = on;");
        }
    }

    private boolean findUser(int net) {
        try (
            Cursor cursor = getReadableDatabase().query(TABLE_SOCIAL,
                    COLUMNS_SOCIAL,
                    // OR: "net=" + net,
                    "net=?",
                    new String[]{String.valueOf(net)},
                    null,
                    null,
                    null)) {

            return (cursor.getCount() > 0);
        }
    }

    public boolean findGoogleUser() {
        return findUser(GOOGLE);
    }

    public User findNewestUser() {
        // TODO fetch user data
        return null;
    }
}
