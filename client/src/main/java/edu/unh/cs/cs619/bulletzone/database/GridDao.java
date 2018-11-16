package edu.unh.cs.cs619.bulletzone.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GridDao {

    @Insert
    void insert(GridEntity ge);

    @Query("DELETE FROM gridTable")
    void deleteAll();

    @Query("SELECT * FROM gridTable ORDER BY time ASC")
    LiveData<List<GridEntity>> getAll();
}
