package edu.unh.cs.cs619.bulletzone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.api.BackgroundExecutor;

import edu.unh.cs.cs619.bulletzone.database.DBGetEvent;
import edu.unh.cs.cs619.bulletzone.database.DBPollerTask;
import edu.unh.cs.cs619.bulletzone.database.GridRepo;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EActivity
public class DBActivity extends AppCompatActivity {

    @Bean
    BusProvider busProvider;

    @Bean
    protected GridAdapter mGridAdapter;

    protected GridView gridView;

    DBPollerTask dbPollerTask;

    //private Object gridEventHandler = new Object()
    //{
    @Subscribe
    public void onUpdateGrid(DBGetEvent event) {
        updateGrid(event.gw);
    }
    //};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        busProvider = new BusProvider();
        gridView = findViewById(R.id.gridView);
        mGridAdapter = new GridAdapter(this);
        gridView.setAdapter(mGridAdapter);

        busProvider.getEventBus().register(this);

        startPoller();
    }

    /*@AfterInject
    void afterInject() {
        //busProvider = new BusProvider();
        gridView = findViewById(R.id.gridView);
        //mGridAdapter = new GridAdapter(this);
        //gridView.setAdapter(mGridAdapter);

        busProvider.getEventBus().register(this);

        startPoller();
    }

    @AfterViews
    protected void afterViewInjection() {
        gridView.setAdapter(mGridAdapter);
    }*/

    @Background(id = "gofuckyourself")
    protected void startPoller() {
        dbPollerTask = new DBPollerTask(busProvider);
        //dbPollerTask.doPoll(new GridRepo(this.getApplication()));
        BackgroundExecutor.execute(new BackgroundExecutor.Task("gofuckyourself", 0L, "") {

            @Override
            public void execute() {
                try {
                    dbPollerTask.doPoll(new GridRepo(getApplication()));
                    //GridPollerTask_.super.doPoll();
                } catch (final Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busProvider.getEventBus().unregister(this);
    }

    public void updateGrid(GridWrapper gw) {
        if(gw != null)
            mGridAdapter.updateList(gw.getGrid());
    }


}
