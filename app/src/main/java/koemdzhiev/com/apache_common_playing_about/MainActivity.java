package koemdzhiev.com.apache_common_playing_about;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import org.w3c.dom.Text;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import koemdzhiev.com.apache_common_playing_about.db.Statistics;

public class MainActivity extends AppCompatActivity {
    private TextView mOutput;
    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;
    private Realm realm;
    private RealmResults<Statistics> allStatistics;
    private RealmChangeListener realmListener;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        mOutput = (TextView)findViewById(R.id.output);

        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                Statistics st = allStatistics.last();

                mOutput.setText("MeanX:"+ st.getMeanX() + "\nSDX: " +st.getSdX());
            }
        });
        allStatistics = realm.where(Statistics.class).findAll(); // Create the "live" query result


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }
}
