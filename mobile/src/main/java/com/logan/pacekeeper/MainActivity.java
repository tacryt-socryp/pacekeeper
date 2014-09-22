package com.logan.pacekeeper;

import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

import com.google.android.gms.fitness.*;
import android.content.IntentSender;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {


    private static final int REQUEST_OAUTH = 1000;
    private static final int RESULT_OK = 2000;
    //private static boolean mPlayMobile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    private GoogleApiClient mClient = null;

    @Override
    protected void onStart() {
        super.onStart();
        if (mClient == null || !mClient.isConnected()) {
            connectFitness();
            createNotification("Runkeeper","Current BPM: 70");
        }
    }

    private void connectFitness() {
        Log.i("TAG", "Connecting...");

        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                // select the Fitness API
                .addApi(Fitness.API)
                        // specify the scopes of access
                .addScope(FitnessScopes.SCOPE_ACTIVITY_READ)
                .addScope(FitnessScopes.SCOPE_BODY_READ_WRITE)
                        // provide callbacks
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Connect the Google API client
        mClient.connect();
    }
    // Manage OAuth authentication
    @Override
    public void onConnectionFailed(ConnectionResult result) {

        // Error while connecting. Try to resolve using the pending intent returned.
        if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                // Request authentication
                result.startResolutionForResult(this, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Log.e("TAG", "Exception connecting to the fitness service", e);
            }
        } else {
            Log.d("TAG", result.toString());
            Log.e("TAG", "Unknown connection issue. Code = " + result.getErrorCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            System.out.println(resultCode);
            if (resultCode == RESULT_OK) {
                // If the user authenticated, try to connect again
                mClient.connect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // If your connection gets lost at some point,
        // you'll be able to determine the reason and react to it here.
        if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i("TAG", "Connection lost.  Cause: Network Lost.");
        } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i("TAG", "Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("TAG", "Connected!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void createNotification(String title, String text) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create builder for the main notification
        NotificationCompat.Builder firstPageBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title + " 2")
                        .setContentText(text);

        // Create second page notification
        Notification secondPageNotification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title + " 2")
                        .setContentText(text)
                        .build();

        // Add second page with wearable extender and extend the main notification
        Notification twoPageNotification =
                new WearableExtender()
                        .addPage(secondPageNotification)
                        .extend(firstPageBuilder)
                        .build();

        // Issue the notification
        notificationManager.notify(001, twoPageNotification);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return (id == R.id.action_settings) || super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final Internal player = new Internal();
            player.start();

            try {
                Thread.sleep(5000);
                player.changeSpeed(0.75);
                Thread.sleep(5000);
                player.changeSpeed(1.0);
                Thread.sleep(5000);
                player.changeSpeed(1.25);
            } catch(InterruptedException e) {}

            /*
            Button btnPlay = (Button) rootView.findViewById(R.id.btnPlay);

            btnPlay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mPlayMobile){
                        player.stop();
                        mPlayMobile = false;
                    } else {
                        player.start();
                        mPlayMobile = true;
                    }
                }
            });
            */

            return rootView;
        }
    }

}

