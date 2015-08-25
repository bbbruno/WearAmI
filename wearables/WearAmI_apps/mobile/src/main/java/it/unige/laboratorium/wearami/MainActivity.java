package it.unige.laboratorium.wearami;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.ObjectOutputStream;




public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    ArrayList<String> toRec = new ArrayList<String>();
    ArrayList<String> toHeart = new ArrayList<String>();
    ArrayList<String> toWalk = new ArrayList<String>();

    String lastMessageMov = "";
    String lastMessageHeart = "";
    String lastMessageWalk = "";

    int count  =0;

    long time_mov = 0;
    long time_heart = 0;
    long time_walk = 0;

    float[] valori = {0,0,0,0,0,0,0,0,0,0,0,0};
    float bpm = 0;
    float passi = 0;

    String type_mov = "";
    String type_heart = "";
    String type_walk = "";

    int i = 0;
    int l = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener((SensorEventListener) this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();

        Wearable.MessageApi.addListener(googleApiClient, new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {

            }
        });

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String message = intent.getStringExtra("message");

            new Thread(new Runnable(){
                @Override
                public void run(){
                    try {

                        Socket client1 = new Socket("192.168.80.136", 8888);
                        ObjectOutputStream oos = new ObjectOutputStream(client1.getOutputStream());
                        oos.writeObject(message);
                        oos.close();
                        client1.close();
                        


                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();

            if(intent.getStringExtra("type")=="1") {

                StringTokenizer info = new StringTokenizer(message, "\n");

                while (info.hasMoreTokens()) {
                    toRec.add(info.nextToken());
                }

                count++;

                //In toRec ho tutti i 50 pacchetti e qui seleziono l'ultimo
                lastMessageMov = toRec.get(49);

                StringTokenizer value = new StringTokenizer(lastMessageMov, ";");

                //Dentro a type heart c'è la m che mi distingue il pacchetto
                type_mov = value.nextToken();

                time_mov = Long.valueOf(value.nextToken());



                for (i = 0; i < 12; i++) {
                    valori[i] = Float.valueOf(value.nextToken());
                }

                ((TextView) findViewById(R.id.cont)).setText("Acquisizione n°: " + count);

                ((TextView) findViewById(R.id.time)).setText("Timestamp: " + time_mov);

                //Mostro solo i valori dell'accelerometro, ma posso mostrare quello che voglio (in valori[] ho tutti i dati)
                ((TextView) findViewById(R.id.acc)).setText("x: " + valori[0] + "\n" + "y: " + valori[1] + "\n" + "z: " + valori[2]);

                for (l = 0; l < 12; l++) {
                    valori[l] = 0;
                }
                toRec.clear();
            }

            if(intent.getStringExtra("type")=="2"){

                StringTokenizer heart = new StringTokenizer(message, "\n");

                while (heart.hasMoreTokens()) {
                    toHeart.add(heart.nextToken());
                }

                lastMessageHeart = toHeart.get(4);

                StringTokenizer value = new StringTokenizer(lastMessageHeart, ";");

                //Dentro a type heart c'è la h che mi distingue il pacchetto
                type_heart = value.nextToken();

                time_heart = Long.valueOf(value.nextToken());

                Log.d("Mes", "h" + time_heart);

                bpm =  Float.valueOf(value.nextToken());

                ((TextView) findViewById(R.id.heart)).setText("BPM: " + bpm);

                toHeart.clear();
            }

            if(intent.getStringExtra("type")=="3"){

                StringTokenizer walk = new StringTokenizer(message, "\n");

                while (walk.hasMoreTokens()) {
                    toWalk.add(walk.nextToken());
                }

                lastMessageWalk = toWalk.get(4);

                StringTokenizer value = new StringTokenizer(lastMessageWalk, ";");

                //Dentro a type heart c'è la s che mi distingue il pacchetto
                type_walk = value.nextToken();

                time_walk = Long.valueOf(value.nextToken());

                passi =  Float.valueOf(value.nextToken());

                ((TextView) findViewById(R.id.walk)).setText("Passi: " + passi);

                toWalk.clear();
            }
        }
    }
}
