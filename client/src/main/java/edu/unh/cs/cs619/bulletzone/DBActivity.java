package edu.unh.cs.cs619.bulletzone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
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

    @ViewById
    protected GridView gridView;

    @Bean
    DBPollerTask dbPollerTask;

    private Object gridEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateGrid(DBGetEvent event) {
            updateGrid(event.gw);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
    }

    public void onButtonReplay(View view){
        BackgroundExecutor.cancelAll("dbpoller", true);
        BackgroundExecutor.execute(new BackgroundExecutor.Task("dbpoller", 0L, "") {
            @Override
            public void execute() {
                try {
                    dbPollerTask.doPoll();
                } catch (final Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }
            }
        });
    }

    public void onButtonPause(View view) {
        //idk
        //dbPollerTask.setSpeed(0);
        BackgroundExecutor.cancelAll("dbpoller", true);
    }

    public void onButton2(View view) {
        speedChange(2);
    }

    public void onButton3(View view) {
        speedChange(3);
    }

    public void onButton4(View view) {
        speedChange(4);
    }

    protected void speedChange(int i) {
        //maybe shutdown thread?
        dbPollerTask.setSpeed(i);
    }

    @AfterInject
    void afterInject() {
        busProvider.getEventBus().register(gridEventHandler);

        startPoller();
    }

    @AfterViews
    protected void afterViewInjection() {
        gridView.setAdapter(mGridAdapter);
    }

    @Background(id = "dbpoller")
    protected void startPoller() {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("dbpoller", 0L, "") {

            @Override
            public void execute() {
                try {
                    dbPollerTask.initalizeDB(new GridRepo(getApplication()));
                    dbPollerTask.doPoll();
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
