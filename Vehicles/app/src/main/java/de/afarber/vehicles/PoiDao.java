package de.afarber.vehicles;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public abstract class PoiDao {
    @Query("SELECT COUNT(*) FROM table_poi")
    public abstract int countPoi();
}
