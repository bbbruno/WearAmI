package com.andrea.example.prova;

        import android.content.Intent;
        import android.support.v4.content.LocalBroadcastManager;

        import com.google.android.gms.wearable.MessageEvent;
        import com.google.android.gms.wearable.WearableListenerService;


public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        final String message = new String(messageEvent.getData());
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("message", message);

        if(messageEvent.getPath().equals("/motion1")) {
            messageIntent.putExtra("type", "1");
        }
        if(messageEvent.getPath().equals("/motion2")) {
            messageIntent.putExtra("type", "2");
        }
        if(messageEvent.getPath().equals("/motion3")) {
            messageIntent.putExtra("type", "3");
        }
        if(messageEvent.getPath().equals("/motion4")) {
            messageIntent.putExtra("type", "4");
        }
        if(messageEvent.getPath().equals("/heart")) {
            messageIntent.putExtra("type", "5");
        }
        if(messageEvent.getPath().equals("/passi")) {
            messageIntent.putExtra("type", "6");
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }
}
