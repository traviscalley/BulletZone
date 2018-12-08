package edu.unh.cs.cs619.bulletzone.rest;

import android.os.SystemClock;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.rest.spring.api.RestClientHeaders;

import edu.unh.cs.cs619.bulletzone.ClientActivity;
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

    @Background(id = "grid_poller_task")
    // TODO: disable trace
    // @Trace(tag="CustomTag", level=Log.WARN)
    public void doPoll() {
        while (!Thread.currentThread().isInterrupted()) {
            //try {

            //Log.d(null, "poller still running");
            onGridUpdate(restClient.grid());

            // poll server every 100ms
            SystemClock.sleep(100);
            //}
            //catch (Exception e)
            //{break;}
        }
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
