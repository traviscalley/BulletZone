package edu.unh.cs.cs619.bulletzone.database;

import android.os.SystemClock;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.api.UiThreadExecutor;

import java.util.List;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EBean
public class DBPollerTask {

    /**
     * Created by simon on 10/3/14.
     */
    private static final String TAG = "PollDB";

    @Bean
    BusProvider busProvider;

    private int replaySpeed = 1;
    private List<GridEntity> events;

    public void setSpeed(int speed)
    {
        replaySpeed = speed;
    }

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
            String[] rows = unconverted.getGrid().split("\\],\\[");
            for (int r = 0; r < grid.length; r++) {
                String[] row = rows[r].split(",");
                for (int c = 0; c < grid.length; c++) {
                    grid[r][c] = Integer.valueOf(row[c].replace("[", ""));
                }

            }
            GridWrapper next = new GridWrapper(grid);

            onGridUpdate(next);//restClient.grid());

            // poll server every 100ms
            if(i != events.size()-1) {
                int sleepDelay = (int)(events.get(i+1).getTimestamp() - unconverted.getTimestamp())/replaySpeed;
                SystemClock.sleep(sleepDelay);
            }
        }
    }

    //@UiThread
    public void onGridUpdate(GridWrapper gw) {
        UiThreadExecutor.runTask("", () -> busProvider.getEventBus().post(new DBGetEvent(gw))
                , 0L);
    }
}
