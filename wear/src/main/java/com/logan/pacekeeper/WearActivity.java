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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class WearActivity extends Activity implements SensorEventListener {

    private static final String TAG = "androidWear";
    private TextView mTextViewStepCount;
    private TextView mTextViewStepDetect;
    private TextView mTextViewHeart;

    Node finalNode;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Keep the Wear screen always on (for testing only!)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);

        mGoogleApiClient = (new GoogleApiClient.Builder(this)).addApi(Wearable.API).build();
        mGoogleApiClient.connect();

        ResultCallback result = new ResultCallback() {

            public void onResult(Result result) {
                onResult((NodeApi.GetConnectedNodesResult) result);
            }

            public void onResult(NodeApi.GetConnectedNodesResult nodesResult) {
                for(Node node:nodesResult.getNodes()){
                    finalNode = node;
                }

            }

        };

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(result);

        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);

        pager.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {

            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                pager.setPageMargins(rowMargin, colMargin);
                getSensorData();

                return insets;
            }
        });

        pager.setAdapter(new GridPagerAdapter(this, getFragmentManager()));

        /*
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextViewStepCount = (TextView) stub.findViewById(R.id.step_count);
                mTextViewStepDetect = (TextView) stub.findViewById(R.id.step_detect);
                mTextViewHeart = (TextView) stub.findViewById(R.id.heart);
                getSensorData();
            }
        });
        */
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
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            mTextViewHeart.setText(msg);
            Log.d(TAG, msg);
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String msg = "Count: " + (int)event.values[0];
            mTextViewStepCount.setText(msg);
            Log.d(TAG, msg);
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            String msg = "Detected at " + currentTimeStr();
            mTextViewStepDetect.setText(msg);
            Log.d(TAG, msg);
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }

    private void sendMessage(final Node finalNode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] payload = {};

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