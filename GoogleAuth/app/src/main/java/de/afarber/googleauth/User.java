package de.afarber.googleauth;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String sid;
    public int net;
    public String given;
    public String family;
    public String photo;
    public float lat;
    public float lng;
    public int stamp;

    public User() {

    }

    protected User(Parcel in) {
        sid = in.readString();
        net = in.readInt();
        given = in.readString();
        family = in.readString();
        photo = in.readString();
        lat = in.readFloat();
        lng = in.readFloat();
        stamp = in.readInt();
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
}
