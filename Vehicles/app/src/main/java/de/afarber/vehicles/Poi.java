package de.afarber.vehicles;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_poi")
public class Poi {
    /*
        "id": 439670,
            "coordinate": {
            "latitude": 53.46036882190762,
            "longitude": 9.909716434648558
        },
        "fleetType": "POOLING",
        "heading": 344.19529122029735
    */

    @NonNull
    @PrimaryKey
    public int id;
    public float latitude;
    public float longitude;
    public String fleetType;
    public float heading;

    public Poi() {}

    @NonNull
    @Override
    public String toString() {
        return Poi.class.getSimpleName() +
                ": id = " + id +
                ", latitude = " + latitude +
                ", longitude = " + longitude +
                ", latitude = " + latitude +
                ", fleetType = " + fleetType +
                ", heading = " + heading;
    }
}
