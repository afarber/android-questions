package de.afarber.googleauth;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.webkit.URLUtil;

public class User implements Parcelable {
    public static final int UNKNOWN       = 0;
    public static final int GOOGLE        = 1;
    public static final int APPLE         = 2;
    public static final int ODNOKLASSNIKI = 4;
    public static final int MAILRU        = 8;
    public static final int VKONTAKTE     = 16;
    public static final int FACEBOOK      = 32;

    public String sid;
    public int net;
    public String given;
    public String family;
    public String photo;
    public float lat;
    public float lng;
    public int stamp;


    public boolean isValid() {
        return (!TextUtils.isEmpty(given) &&
                !TextUtils.isEmpty(sid) &&
                net > UNKNOWN &&
                net <= FACEBOOK &&
                (TextUtils.isEmpty(photo) || URLUtil.isNetworkUrl(photo)));
    }



    public User() {
    }

    public User(Cursor cursor) {
        sid    = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SID));
        net    = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NET));
        given  = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GIVEN));
        family = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAMILY));
        photo  = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO));
        lat    = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LAT));
        lng    = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LNG));
        stamp  = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STAMP));

        if (!isValid())
            throw new IllegalArgumentException();
    }

    protected User(Parcel in) {
        sid    = in.readString();
        net    = in.readInt();
        given  = in.readString();
        family = in.readString();
        photo  = in.readString();
        lat    = in.readFloat();
        lng    = in.readFloat();
        stamp  = in.readInt();

        if (!isValid())
            throw new IllegalArgumentException();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sid);
        dest.writeInt(net);
        dest.writeString(given);
        dest.writeString(family);
        dest.writeString(photo);
        dest.writeFloat(lat);
        dest.writeFloat(lng);
        dest.writeInt(stamp);
    }

    @Override
    public String toString() {
        return User.class.getSimpleName() + ": " + sid + " " + given + " " + family;
    }
}
