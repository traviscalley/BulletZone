package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.rest.spring.api.RestClientHeaders;
import org.androidannotations.api.BackgroundExecutor;

import java.io.IOException;
import java.util.List;

import edu.unh.cs.cs619.bulletzone.database.GridEntity;
import edu.unh.cs.cs619.bulletzone.database.GridRepo;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.ui.SensorHandler;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EActivity(R.layout.activity_client)
public class ClientActivity extends Activity {

    private static final String TAG = "ClientActivity";

    @Bean
    GridAdapter mGridAdapter;

    @ViewById
    protected GridView gridView;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;

    @RestService
    BulletZoneRestClient restClient;

    @Bean
    BZRestErrorhandler bzRestErrorhandler;

    SensorHandler sensorHandler;

    /**
     * Remote tank identifier
     */
    private long tankId = -1;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Bean
    GridRepo gridRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Accelerometer shake handling
        /*mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, mSensorManager.SENSOR_DELAY_NORMAL);*/

        sensorHandler = new SensorHandler((SensorManager) getSystemService(Context.SENSOR_SERVICE), this::onButtonFire);

        //mGridAdapter = new GridAdapter(this);
        //gridRepo = new GridRepo(this.getApplication());


        //clear old gameplays
        gridRepo.deleteAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //busProvider.getEventBus().unregister(gridEventHandler);
    }

    /**
     * Otto has a limitation (as per design) that it will only find
     * methods on the immediate class type. As a result, if at runtime this instance
     * actually points to a subclass implementation, the methods registered in this class will
     * not be found. This immediately becomes a problem when using the AndroidAnnotations
     * framework as it always produces a subclass of annotated classes.
     *
     * To get around the class hierarchy limitation, one can use a separate anonymous class to
     * handle the events.
     */
    /*private Object gridEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            //updateGrid(event.gw);
            //move store state to gridrepo
            //storeState(event.gw);
        }
    };*/


    @AfterViews
    protected void afterViewInjection() {
        joinAsync();
        SystemClock.sleep(500);
        gridView.setAdapter(mGridAdapter);
    }

    @AfterInject
    void afterInject() {
        restClient.setRestErrorHandler(bzRestErrorhandler);
        //busProvider.getEventBus().register(gridEventHandler);
    }

    @Background
    void joinAsync() {
        try {
            tankId = restClient.join().getResult();
            gridPollTask.start();
            mGridAdapter.setPlayerID(tankId);
        } catch (Exception e) {
        }
    }

    public void updateGrid(GridWrapper gw) {
        mGridAdapter.updateList(gw.getGrid());
    }

    @Background(id = "grid_insert")
    public void storeState(GridWrapper gw){
        gridRepo.insert(gw);//new GridEntity(gw));//res, gw.getTimeStamp()) );
    }

    /**
     * Value of direction causes either case of a "buttonUp" press
     * or a "buttonDown" press and moves the tank accordingly
     * @param view view 
     */
    @Click({R.id.buttonUp, R.id.buttonDown})
    protected void onButtonMove(View view) {
        final int viewId = view.getId();
        byte direction = 0;

        switch (viewId) {
            case R.id.buttonUp:
                direction = 0;
                break;
            case R.id.buttonDown:
                direction = 4;
                break;
            default:
                Log.e(TAG, "Unknown movement button id: " + viewId);
                break;
        }
        this.moveAsync(tankId, direction);
    }

    /**
     * Allows the soldier to eject from tank.
     * @param view View
     */
    @Background
    @Click(R.id.buttonEject)
    protected void onButtonEject(View view) {
        restClient.eject(tankId);
    }

    /**
     * Allows the soldier to eject from tank.
     * @param view View
     */
    @Background
    @Click(R.id.buttonEjectP)
    protected void onButtonEjectPower(View view) {
        restClient.ejectP(tankId);
    }

    /**
     * Value of direction causes either case of a "buttonLeft" press
     * or a "buttonRight" press and moves the tank accordingly
     * @param view view
     */
    @Click({R.id.buttonLeft, R.id.buttonRight})
    protected void onButtonTurn(View view) {
        final int viewId = view.getId();
        byte direction = 0;
        switch (viewId) {
            case R.id.buttonLeft:
                direction = 6;
                break;
            case R.id.buttonRight:
                direction = 2;
                break;
            default:
                Log.e(TAG, "Unknown movement button id: " + viewId);
                break;
        }
        this.turnAsync(tankId, direction);
    }

    @Background
    @Click(R.id.buttonReplay)
    protected void onButtonReplay(View view){
        //leave async
        //not sure if this is what to do
        //leaveAsync(tankId);
        leaveGame();
        //try {
        //BackgroundExecutor.cancelAll("grid_poller_task", true);
        //}catch(Exception e){}
        startActivity(new Intent(this, DBActivity_.class));

        /*List<GridEntity> idfk = gridRepo.getAll();//(List<GridEntity> list) -> {handlePast(list);});

        GridEntity unconverted = idfk.get(0);

        //get an entry
        //make a grid wrapper
        GridWrapper next = new GridWrapper();
        int[][] grid = new int[16][16];
        Log.d(null, unconverted.getGrid());
        String[] rows = unconverted.getGrid().split("\\],\\[");
        for(int r = 0; r < grid.length; r++)
        {
            //if(rows[r].charAt(0) == ',')
                //rows[r].replaceFirst(",","");
            String[] row = rows[r].split(",");
            //Log.d(null, rows[r]);
            for(int c = 0; c < grid.length; c++) {
                //Log.d(null, row[c]);
                //if(row[c] != "")
                grid[r][c] = Integer.valueOf(row[c].replace("[", ""));
            }

        }
        next.setGrid(grid);

        //update teh grid

        //hypothetically this should work, just for one frame though
        //eventually
        //busProvider.getEventBus().post(new GridUpdateEvent(next));
*/

    }


    @Background
    void moveAsync(long tankId, byte direction) {
        restClient.move(tankId, direction);
    }

    @Background
    void turnAsync(long tankId, byte direction) {
        restClient.turn(tankId, direction);
    }

    @Click(R.id.buttonFire)
    @Background
    protected void onButtonFire() {
        restClient.fire(tankId);
    }

    @Click(R.id.buttonLeave)
    @Background
    void leaveGame() {
        System.out.println("leaveGame() called, tank ID: "+tankId);
        gridPollTask.end();
        restClient.leave(tankId);
    }

    @Background
    void leaveAsync(long tankId) {
        System.out.println("Leave called, tank ID: " + tankId);
        gridPollTask.end();
        restClient.leave(tankId);
    }

}
