package edu.unh.cs.cs619.bulletzone.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@Entity(tableName = "gridTable")
public class GridEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "time")
    private long timestamp;

    @ColumnInfo(name = "grid")
    private String grid;

    public GridEntity(GridWrapper gw){

        //maybe put this not in main thread??
        int[][] rawData = gw.getGrid();
        StringBuffer res = new StringBuffer("[");
        for(int[] i : rawData)
        {
            res.append("[");
            for(int j : i)
                res.append(Integer.toString(j) + ",");
            res.append(",]");
        }
        res.append("]");
        this.grid = res.toString();
        Log.d(null, this.grid);// jsut to see
        this.timestamp = gw.getTimeStamp();
    }

    public GridEntity(@NonNull String grid, long timestamp){
        this.grid = grid;
        this.timestamp = timestamp;//System.currentTimeMillis();
    }

    public String getGrid(){
        return this.grid;
    }

    public long getTimestamp(){
        return this.timestamp;
    }
}
