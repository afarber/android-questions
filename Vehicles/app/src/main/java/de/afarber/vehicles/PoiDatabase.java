package de.afarber.vehicles;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executors;

@Database(entities = {
        Poi.class
}, version = 2)
public abstract class PoiDatabase extends RoomDatabase {
    private final static String DB_FILENAME = "poi.db";

    private static PoiDatabase sInstance;

    public abstract PoiDao poiDao();

    @NonNull
    public static PoiDao getDao() {
        return sInstance.poiDao();
    }

    public static void init(Context ctx) {
        sInstance = Room.databaseBuilder(ctx, PoiDatabase.class, DB_FILENAME)
                //.allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .setQueryExecutor(Executors.newSingleThreadScheduledExecutor())
                .build();
    }
}
