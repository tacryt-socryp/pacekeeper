package com.logan.pacekeeper;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService{

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("message", "onMessageReceived()");

        if(messageEvent.getPath().equals("/LAUNCH")) {
            final String message = new String(messageEvent.getData());
            Log.d("message", message);

        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}