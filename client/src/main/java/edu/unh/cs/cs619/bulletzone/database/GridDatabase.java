package edu.unh.cs.cs619.bulletzone.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {GridEntity.class}, version = 1, exportSchema = false)
public abstract class GridDatabase extends RoomDatabase {
    public abstract GridDao gridDao();

    private static volatile GridDatabase _instance;

    public static GridDatabase getDatabase(final Context context) {
        if (_instance == null) {
            synchronized (GridDatabase.class) {
                if (_instance == null)
                    _instance = Room.databaseBuilder(context.getApplicationContext(),
                            GridDatabase.class, "gridDatabase")
                            .build();
            }
        }
        return _instance;
    }
}
