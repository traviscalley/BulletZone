package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import edu.unh.cs.cs619.bulletzone.database.GridRepo;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EActivity(R.layout.activity_replay)
public class ReplayActivity extends Activity {

    @Bean
    BusProvider busProvider;

    @Bean
    protected GridAdapter mGridAdapter;

    private GridRepo gridRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Accelerometer shake handling
        //mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mAccelerometer, mSensorManager.SENSOR_DELAY_NORMAL);

        gridRepo = new GridRepo(this.getApplication());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busProvider.getEventBus().unregister(gridEventHandler);
    }

    private Object gridEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            updateGrid(event.gw);
        }
    };

    public void updateGrid(GridWrapper gw) {
        mGridAdapter.updateList(gw.getGrid());
    }


}
