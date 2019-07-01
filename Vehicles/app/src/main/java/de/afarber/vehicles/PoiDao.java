package de.afarber.vehicles;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class PoiDao {
    @Query("SELECT * FROM table_poi")
    public abstract LiveData<List<Poi>> getVehicles();

    @Query("SELECT COUNT(*) FROM table_poi")
    public abstract int countPoi();

    @Query("DELETE FROM table_poi")
    abstract void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertVehicles(List<Poi> pois);

    @Transaction
    public void updateVehicles(List<Poi> pois) {
        deleteAll();
        insertVehicles(pois);
    }

}
