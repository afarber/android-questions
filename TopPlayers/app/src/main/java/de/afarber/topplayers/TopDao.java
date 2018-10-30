package de.afarber.topplayers;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TopDao {
    @Query("SELECT * FROM table_top")
    LiveData<List<TopEntity>> fetchTops();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTops(List<TopEntity> tops);

    @Query("DELETE FROM table_top")
    void deleteTops();
}