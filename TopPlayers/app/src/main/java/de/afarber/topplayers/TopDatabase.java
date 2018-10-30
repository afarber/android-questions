package de.afarber.topplayers;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {TopEntity.class}, version = 1)
public abstract class TopDatabase extends RoomDatabase {
    private final static String DB_FILENAME = "room.db";

    private static TopDatabase sInstance;

    public abstract TopDao topDao();

    public static synchronized TopDatabase getInstance(final Context ctx) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(ctx, TopDatabase.class, DB_FILENAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sInstance;
    }
}