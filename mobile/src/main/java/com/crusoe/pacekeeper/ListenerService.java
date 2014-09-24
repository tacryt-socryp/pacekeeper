package com.crusoe.pacekeeper;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(HELLO_WORLD_WEAR_PATH)) {

            byte[] message = messageEvent.getData();

            Intent startIntent = new Intent(this, MobileActivity.class);
            startIntent.putExtra("methodName", "changeSpeed");
            startIntent.putExtra("speedFactor", 1.3f);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }

    }

}