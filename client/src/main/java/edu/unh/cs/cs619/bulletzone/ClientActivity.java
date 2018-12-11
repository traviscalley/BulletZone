package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.database.GridRepo;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
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

    private long tankId = -1;
    private boolean isTank;

    @Bean
    GridRepo gridRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sensorHandler = new SensorHandler((SensorManager) getSystemService(Context.SENSOR_SERVICE), this::onButtonFire);

        //clear old replays
        gridRepo.deleteAll();

        // Select tank or ship
        Intent intent = getIntent();
        isTank = intent.getBooleanExtra("tankBoolean", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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
            tankId = restClient.join(isTank).getResult();
            gridPollTask.start();
            mGridAdapter.setPlayerID(tankId);
        } catch (Exception e) {
        }
    }

    public void updateGrid(GridWrapper gw) {
        mGridAdapter.updateList(gw.getGrid());
    }

    /*@Background(id = "grid_insert")
    public void storeState(GridWrapper gw){
            gridRepo.insert(gw);//new GridEntity(gw));//res, gw.getTimeStamp()) );
    }*/

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
        startActivity(new Intent(this, MainScreenActivity_.class));
    }

    @Background
    void leaveAsync(long tankId) {
        System.out.println("Leave called, tank ID: " + tankId);
        gridPollTask.end();
        restClient.leave(tankId);
    }
}
