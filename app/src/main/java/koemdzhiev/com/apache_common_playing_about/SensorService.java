package koemdzhiev.com.apache_common_playing_about;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import koemdzhiev.com.apache_common_playing_about.db.Statistics;

public class SensorService extends Service implements SensorEventListener {
    private static final long WINDOW_LENGTH = 10000;
    private SensorManager mSensorManager;
    private Sensor mAccSensor;
    private long windowBegTime = -1;
    private DescriptiveStatistics xValuesDS;
    private DescriptiveStatistics yValuesDS;
    private DescriptiveStatistics zValuesDS;
    private Realm realm = Realm.getDefaultInstance();

    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        xValuesDS = new DescriptiveStatistics();
        yValuesDS = new DescriptiveStatistics();
        zValuesDS = new DescriptiveStatistics();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("koemdzhiev.com.apache_common_playing_about.RestartSensor");
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        final float alpha = 0.8f;
        final double[] gravity = new double[3];
        final double[] linear_acc = new double[3];
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acc[0] = event.values[0] - gravity[0];
        linear_acc[1] = event.values[1] - gravity[1];
        linear_acc[2] = event.values[2] - gravity[2];

        xValuesDS.addValue(linear_acc[0]);
        yValuesDS.addValue(linear_acc[1]);
        zValuesDS.addValue(linear_acc[2]);

        if (System.currentTimeMillis() - windowBegTime > WINDOW_LENGTH) {
            if (windowBegTime > 0) {
                // calculate statistics here
                realm.beginTransaction();
                Statistics statistics = realm.createObject(Statistics.class);
                statistics.setMeanX(xValuesDS.getMean());
                statistics.setMeanY(yValuesDS.getMean());
                statistics.setMeanZ(zValuesDS.getMean());

                statistics.setSdX(xValuesDS.getStandardDeviation());
                statistics.setSdY(yValuesDS.getStandardDeviation());
                statistics.setSdZ(zValuesDS.getStandardDeviation());

                realm.commitTransaction();

                clearValues();
            }

            windowBegTime = System.currentTimeMillis();

        }
    }

    private void clearValues() {
        this.xValuesDS.clear();
        this.yValuesDS.clear();
        this.zValuesDS.clear();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
