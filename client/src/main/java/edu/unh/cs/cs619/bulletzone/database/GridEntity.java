package edu.unh.cs.cs619.bulletzone.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "gridTable")
public class GridEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "time")
    private long timestamp;

    @ColumnInfo(name = "grid")
    private String grid;

    public GridEntity(@NonNull String grid){
        this.grid = grid;
        this.timestamp = System.currentTimeMillis();
    }

    public String getGrid(){
        return this.grid;
    }

    public long getTimestamp(){
        return this.timestamp;
    }
}
