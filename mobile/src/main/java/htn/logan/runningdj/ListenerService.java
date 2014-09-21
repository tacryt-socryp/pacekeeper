package htn.logan.runningdj;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService{

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("test", "onMessageReceived()");
        if(messageEvent.getPath().equals("/LAUNCH")) {
            final String message = new String(messageEvent.getData());

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            TextView textView = inflater.inflate(R.id.txtbpm, null);
            textView.setText(message);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}