package com.andrea.example.prova;

        import android.app.Activity;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.hardware.Sensor;
        import android.hardware.SensorEventListener;
        import android.os.Bundle;
        import android.os.Environment;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.hardware.SensorManager;
        import android.hardware.SensorEvent;
        import android.widget.TextView;

        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.wearable.MessageApi;
        import com.google.android.gms.wearable.MessageEvent;
        import com.google.android.gms.wearable.Wearable;

        import java.io.IOException;
        import java.net.Socket;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.StringTokenizer;
        import java.net.UnknownHostException;

        import java.io.*;

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

    float[] valori = {0,0,0};
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

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/wearami/log");
        dir.mkdirs();
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

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/wearami/log");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd_hh");
            String date = sdf.format(new Date());

            String filename = date+".txt";
            File file = new File(dir, filename);

            try {
                FileOutputStream f = new FileOutputStream(file,true);
                f.write(message.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Socket client = new Socket("130.251.13.242", 8888); // Spirit_Ubuntu
                        //Socket client = new Socket("192.168.1.4", 8888);

                        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                        oos.writeObject(message);

                        oos.close();
                        client.close();


                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();*/

            //((TextView) findViewById(R.id.dato)).setText(message);


            if(intent.getStringExtra("type")=="1") {

                StringTokenizer info = new StringTokenizer(message, "\n");

                while (info.hasMoreTokens()) {
                    toRec.add(info.nextToken());
                }

                count++;

                lastMessageMov = toRec.get(49);

                StringTokenizer value = new StringTokenizer(lastMessageMov, ";");

                type_mov = value.nextToken();

                time_mov = Long.valueOf(value.nextToken());



                for (i = 0; i < 3; i++) {
                    valori[i] = Float.valueOf(value.nextToken());
                }

                //Mostro solo i valori dell'accelerometro, ma posso mostrare quello che voglio (in valori[] ho tutti i dati)
                ((TextView) findViewById(R.id.dato)).setText("x: " + valori[0] + "\n" + "y: " + valori[1] + "\n" + "z: " + valori[2]);

                for (l = 0; l < 3; l++) {
                    valori[l] = 0;
                }
                toRec.clear();
            }
            /*
            if(intent.getStringExtra("type")=="5"){

                StringTokenizer heart = new StringTokenizer(message, "\n");

                while (heart.hasMoreTokens()) {
                    toHeart.add(heart.nextToken());
                }

                lastMessageHeart = toHeart.get(4);

                StringTokenizer value = new StringTokenizer(lastMessageHeart, ";");

                type_heart = value.nextToken();

                time_heart = Long.valueOf(value.nextToken());

                Log.d("Mes", "h" + time_heart);

                bpm =  Float.valueOf(value.nextToken());

                ((TextView) findViewById(R.id.heart)).setText("BPM: " + bpm);

                toHeart.clear();
            }

            if(intent.getStringExtra("type")=="6"){

                StringTokenizer walk = new StringTokenizer(message, "\n");

                while (walk.hasMoreTokens()) {
                    toWalk.add(walk.nextToken());
                }

                lastMessageWalk = toWalk.get(4);

                StringTokenizer value = new StringTokenizer(lastMessageWalk, ";");

                type_walk = value.nextToken();

                time_walk = Long.valueOf(value.nextToken());

                passi =  Float.valueOf(value.nextToken());

                ((TextView) findViewById(R.id.walk)).setText("Passi: " + passi);

                toWalk.clear();
            }
            */
        }
    }
}
