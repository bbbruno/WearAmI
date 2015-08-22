package it.unige.laboratorium.wearami;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        final String message = new String(messageEvent.getData());
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("message", message);

        if(messageEvent.getPath().equals("/motion")) {
            messageIntent.putExtra("type", "1");
        }
        if(messageEvent.getPath().equals("/heart")) {
            messageIntent.putExtra("type", "2");
        }
        if(messageEvent.getPath().equals("/passi")) {
            messageIntent.putExtra("type", "3");
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }
}
