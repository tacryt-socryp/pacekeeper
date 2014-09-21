package htn.logan.runningdj;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.view.GridViewPager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
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
    private static int updatePhone = 0;
    private static double startSteps = 0;
    private static double deltaSteps = 0;
    Node finalNode;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        mGoogleApiClient = (new GoogleApiClient.Builder(this)).addApi(Wearable.API).build();
        mGoogleApiClient.connect();
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback() {

            public void onResult(Result result) {
                onResult((NodeApi.GetConnectedNodesResult) result);
            }

            public void onResult(NodeApi.GetConnectedNodesResult nodesResult) {
                for(Node node:nodesResult.getNodes()){
                    finalNode = node;
                    //sendMessage(finalNode);
                }

            }

        });

        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        sensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        Log.d("beat", sensorManager.toString());

        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor ds : deviceSensors) {
            if (ds.getName().equals("Step Counter Sensor")) {
                walkSensor = sensorManager.getDefaultSensor(ds.getType());
            }
            Log.d("beat", ds.toString());
        }

        register(sensorManager, walkSensor, 500);

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

    private void sendMessage(final Node finalNode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] payload = Double.toString(deltaSteps).getBytes();

                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, finalNode.getId(), "/LAUNCH", payload).await();
                if (!result.getStatus().isSuccess()) {
                    Log.e(getPackageName(), "ERROR: failed to send Message: " + result.getStatus());
                } else {
                    Log.d(getPackageName(), "SUCCESS: send Message: " + result.getStatus());
                }
            }
        }).start();
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


    public void onSensorChanged(SensorEvent event) {
        updatePhone++;
        if (startSteps == 0) {
            for (float val : event.values) {
                if (val > 0) {
                    startSteps = val;
                }
            }
        } else {
            for (float val : event.values) {
                if (val > 0) {
                    deltaSteps = deltaSteps + Math.abs(val - startSteps);
                    startSteps = val;
                }
            }

        }

        if (updatePhone == 10) {
            deltaSteps = deltaSteps / 20;
            updatePhone = 0;
            sendMessage(finalNode);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
