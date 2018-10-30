package de.afarber.topplayers;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "table_top")
public class TopEntity implements Parcelable {

    private final static String UID = "uid";
    private final static String ELO = "elo";
    private final static String AVG_TIME = "avg_time";
    private final static String AVG_SCORE = "avg_score";
    private final static String GIVEN = "given";
    private final static String PHOTO = "photo";

    @PrimaryKey
    public int uid;
    public int elo;
    @NonNull
    public String given;
    public String photo;
    public String avg_time;
    public Float avg_score;

    public TopEntity(int uid,
                     int elo,
                     @NonNull String given,
                     String photo,
                     String avg_time,
                     Float avg_score) {
        this.uid       = uid;
        this.elo       = elo;
        this.given     = given;
        this.photo     = photo;
        this.avg_time  = avg_time;
        this.avg_score = avg_score;
    }

    public TopEntity(JSONObject jsonObj) throws JSONException {
        this.uid = jsonObj.getInt(UID);
        this.elo = jsonObj.optInt(ELO);
        this.avg_score = (float) jsonObj.optDouble(AVG_SCORE);
        this.avg_time = jsonObj.getString(AVG_TIME);
        this.given = jsonObj.getString(GIVEN);
        this.photo = jsonObj.getString(PHOTO);
    }

    @Override
    public String toString() {
        return TopEntity.class.getSimpleName() +
                ": uid = " + uid +
                ", elo = " + elo +
                ", given = " + given +
                ", photo = " + photo +
                ", avg_time = " + avg_time +
                ", avg_score = " + avg_score;
    }

    public static final Creator<TopEntity> CREATOR = new Creator<TopEntity>() {
        @Override
        public TopEntity createFromParcel(Parcel in) {
            return new TopEntity(in);
        }

        @Override
        public TopEntity[] newArray(int size) {
            return new TopEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeInt(elo);
        dest.writeString(given);
        dest.writeString(photo);
        dest.writeString(avg_time);
        dest.writeFloat(avg_score == null ? Float.MAX_VALUE : avg_score);
    }

    private TopEntity(Parcel in) {
        uid       = in.readInt();
        elo       = in.readInt();
        given     = in.readString();
        photo     = in.readString();
        avg_time  = in.readString();
        avg_score = in.readFloat();

        if (avg_score == Float.MAX_VALUE) {
            avg_score = null;
        }
    }
}
