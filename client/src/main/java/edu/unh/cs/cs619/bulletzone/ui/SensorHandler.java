package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.androidannotations.api.BackgroundExecutor;

public class SensorHandler implements SensorEventListener {
    private Runnable todo;

    public SensorHandler(SensorManager sensorManager, Runnable doit){
        this.todo = doit;
        //mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > 2.7f) {
            BackgroundExecutor.execute(todo);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
