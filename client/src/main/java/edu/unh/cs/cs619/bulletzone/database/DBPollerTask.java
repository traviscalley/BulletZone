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
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

public class DBPollerTask {

/**
 * Created by simon on 10/3/14.
 */
    private static final String TAG = "PollDB";

    BusProvider busProvider;

    private int replaySpeed = 1;
    private List<GridEntity> events;

    public DBPollerTask(BusProvider bs)
    {
        busProvider = bs;
    }

    public void setSpeed(int speed)
    {
        replaySpeed = speed;
    }

    // TODO: disable trace
    // @Trace(tag="CustomTag", level=Log.WARN)
    //@Background(id = "db_poller_task")
        //busProvider = bus;
    public void initalizeDB(GridRepo gridRepo) {
        events = gridRepo.getAll();//(List<GridEntity> list) -> {handlePast(list);});
    }

    public void doPoll() {
        for(int i = 0; i < events.size(); i++) {
            if(Thread.currentThread().isInterrupted())
                return;
            GridEntity unconverted = events.get(i);

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
            if(i != events.size()-1) {
                int sleepDelay = (int)(events.get(i+1).getTimestamp() - unconverted.getTimestamp())/replaySpeed;
                SystemClock.sleep(sleepDelay);
            }
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
