package edu.unh.cs.cs619.bulletzone.database;

import android.os.SystemClock;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.UiThreadExecutor;

import java.util.List;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

public class DBPollerTask {

/**
 * Created by simon on 10/3/14.
 */
    private static final String TAG = "PollDB";

    BusProvider busProvider;

    public DBPollerTask(BusProvider bs)
    {
        busProvider = bs;
    }

    // TODO: disable trace
    // @Trace(tag="CustomTag", level=Log.WARN)
    //@Background(id = "db_poller_task")
    public void doPoll(GridRepo gridRepo) {
        //busProvider = bus;
        List<GridEntity> idfk = gridRepo.getAll();//(List<GridEntity> list) -> {handlePast(list);});

        for(int i = 0; i < idfk.size(); i++) {
            GridEntity unconverted = idfk.get(i);

            //get an entry
            //make a grid wrapper
            int[][] grid = new int[16][16];
            //Log.d(null, unconverted.getGrid());
            String[] rows = unconverted.getGrid().split("\\],\\[");
            for (int r = 0; r < grid.length; r++) {
                //if(rows[r].charAt(0) == ',')
                //rows[r].replaceFirst(",","");
                String[] row = rows[r].split(",");
                //Log.d(null, rows[r]);
                for (int c = 0; c < grid.length; c++) {
                    //Log.d(null, row[c]);
                    //if(row[c] != "")
                    grid[r][c] = Integer.valueOf(row[c].replace("[", ""));
                }

            }
            GridWrapper next = new GridWrapper(grid);

            //update teh grid

            //hypothetically this should work, just for one frame though
            //eventually
            //busProvider.getEventBus().post(new GridUpdateEvent(next));
            //while (true) {


            onGridUpdate(next);//restClient.grid());

            // poll server every 100ms
            SystemClock.sleep(1000);
            //}
        }
    }

    //@UiThread
    public void onGridUpdate(GridWrapper gw) {
        UiThreadExecutor.runTask("", new Runnable() {

                    @Override
                    public void run() {
                        busProvider.getEventBus().post(new DBGetEvent(gw));
                    }
                }
                , 0L);
    }
}
