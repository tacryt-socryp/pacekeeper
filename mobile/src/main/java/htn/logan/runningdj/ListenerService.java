package htn.logan.runningdj;

import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.gms.wearable.MessageEvent;
import android.widget.Toast;

public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println(messageEvent.getData());
        showToast(messageEvent.getPath());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}