package com.logan.pacekeeper;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    @Override
    public void onPeerConnected(Node peer) {
        Log.d("com.logan.pacekeeper", "Peer Connected: " + peer.getDisplayName());
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("com.logan.pacekeeper", "onMessageReceived()");

        final String message = new String(messageEvent.getData());
        Log.d("messageReceived", message);

        //if(messageEvent.getPath().equals("/LAUNCH")) {
        //    Log.d("message", message);
        //}
    }
}