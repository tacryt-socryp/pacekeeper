package htn.logan.runningdj;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.fitness.*;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.fitness.*;

import java.sql.Connection;


public class MainActivity extends Activity
                            implements ConnectionCallbacks, OnConnectionFailedListener {

    private static boolean mPlayMobile = false;
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
            } catch (SendIntentException e) {
                Log.e("TAG", "Exception connecting to the fitness service", e);
            }
        } else {
            Log.e("TAG", "Unknown connection issue. Code = " + result.getErrorCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
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

        // Now you can make calls to the Fitness APIs.
        invokeFitnessAPIs();
    }

    public void invokeFitnessAPIs() {
        // Call the Fitness APIs here
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            final Internal player = new Internal(rootView);
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

            return rootView;
        }
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Log.d("message", "onMessageReceived: " + event.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Change album art, song title, and pause / playing based on type of event.
            }
        });
    }

}

