package com.logan.pacekeeper;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import android.view.WindowManager;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class WearActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "androidWear";
    private TextView mTextViewStepCount;
    private TextView mTextViewStepDetect;
    private TextView mTextViewHeart;

    float[] values;

    Node finalNode;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_wear);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);

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
        getSensorData();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    finalNode = node;
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Improve your code
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Improve your code
    }

    private void getSensorData() {
        SensorManager mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private String currentTimeStr() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(c.getTime());
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                String msg = "" + (int) event.values[0];
                //mTextViewHeart.setText(msg);
                Log.d(TAG, msg);
            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                String msg = "Count: " + (int) event.values[0];
                //mTextViewStepCount.setText(msg);
                Log.d(TAG, msg);
            } else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                String msg = "Detected at " + currentTimeStr();
                //mTextViewStepDetect.setText(msg);
                Log.d(TAG, msg);
            } else {
                Log.d(TAG, "Unknown sensor type");
            }

            values = event.values;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && finalNode != null) {
                sendMessage(finalNode);
            }
        }
    }

    public static byte[] FloatArray2ByteArray(float[] values){
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values){
            buffer.putFloat(value);
        }

        return buffer.array();
    }

    private void sendMessage(final Node finalNode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] payload = FloatArray2ByteArray(values);

                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, finalNode.getId(), "/LAUNCH", payload).await();
                if (!result.getStatus().isSuccess()) {
                    Log.e(getPackageName(), "ERROR: failed to send Message: " + result.getStatus());
                } else {
                    Log.d(getPackageName(), "SUCCESS: send Message: " + result.getStatus());
                }

            }
        }).start();
    }
}