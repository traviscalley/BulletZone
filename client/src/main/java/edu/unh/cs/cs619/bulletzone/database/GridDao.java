package edu.unh.cs.cs619.bulletzone.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GridDao {

    @Insert
    void insert(GridEntity ge);

    //delete everything over a day old
    @Query("DELETE FROM gridTable WHERE time < CURRENT_TIMESTAMP*1000 - 86400000")
    void deleteAll();

    @Query("SELECT * FROM gridTable ORDER BY time ASC")
    List<GridEntity> getAll();
}
