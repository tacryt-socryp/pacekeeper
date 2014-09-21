package htn.logan.runningdj;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.view.GridViewPager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.List;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import android.content.Context;

public class WearActivity extends Activity implements SensorEventListener {

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final String MESSAGE = "Hello Wear!";
    private boolean mPlayWear;
    private GoogleApiClient client;
    private String nodeId;
    private SensorManager sensorManager;
    private Sensor walkSensor;
    private Sensor accelSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        sensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        Log.d("beat", sensorManager.toString());

        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor ds : deviceSensors) {
            if (ds.getName().equals("Step Counter Sensor")) {
                walkSensor = sensorManager.getDefaultSensor(ds.getType());
            }
            if (ds.getName().equals("Linear Acceleration Sensor")) {
                accelSensor = sensorManager.getDefaultSensor(ds.getType());
            }
            Log.d("beat", ds.toString());
        }

        initApi();
        register(sensorManager, walkSensor, SensorManager.SENSOR_DELAY_GAME);
        register(sensorManager, accelSensor, SensorManager.SENSOR_DELAY_GAME);

        pager.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                pager.setPageMargins(rowMargin, colMargin);

                return insets;
            }
        });
        pager.setAdapter(new GridPagerAdapter(this, getFragmentManager()));
    }

    void register(SensorManager sm, Sensor hs, int delay) {
        sm.registerListener(this, hs, delay);
    }

    void unregister(SensorManager sm) {
        sm.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }


    public void onSensorChanged(SensorEvent event) {
        for (float val : event.values) {
            Log.d("beat", Float.toString(val));
        }
        Log.d("beat", event.sensor.getName());
        Log.d("beat", Integer.toString(event.values.length));
        Log.d("beat", Integer.toString(event.accuracy));
        Log.d("beat", Long.toString(event.timestamp));
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
