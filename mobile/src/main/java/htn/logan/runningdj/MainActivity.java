package htn.logan.runningdj;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.fitness.*;


public class MainActivity extends Activity implements MessageApi.MessageListener {

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

