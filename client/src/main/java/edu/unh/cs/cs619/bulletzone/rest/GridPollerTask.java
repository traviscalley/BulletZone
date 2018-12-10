package edu.unh.cs.cs619.bulletzone.rest;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.rest.spring.annotations.RestService;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

/**
 * Created by simon on 10/3/14.
 */
@EBean
public class GridPollerTask {
    private static final String TAG = "PollServer";

    // Injected object
    @Bean
    BusProvider busProvider;

    @RestService
    BulletZoneRestClient restClient;

    Timer t = new Timer();

    //need a clock!

    @Background(id = "grid_poller_task")
    // TODO: disable trace
    // @Trace(tag="CustomTag", level=Log.WARN)
    public void doPoll() {
            onGridUpdate(restClient.grid());
    }

    @Background
    public void start(){
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doPoll();
            }
        }, 0, 100);
    }

    @Background
    public void end(){
        t.cancel();
    }

    @UiThread
    public void onGridUpdate(GridWrapper gw) {
        busProvider.getEventBus().post(new GridUpdateEvent(gw));
    }

    @Background
    public void stopPoll(){
        BackgroundExecutor.cancelAll("grid_poller_task", false);
    }
}
