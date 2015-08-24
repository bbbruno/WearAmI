package it.unige.laboratorium.wearami;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private TextView mButton;

    String nodeId;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGravity;
    private Sensor senLinear;
    private Sensor senGyro;
    private Sensor senHeart;
    private Sensor senStep;

    private long prevD;

    boolean[] received = {false, false, false, false, false, false};

    float[] acc ={0,0,0};
    float[] grav ={0,0,0};
    float[] lin ={0,0,0};
    float[] gyro ={0,0,0};

    float heart = 0;
    float passi = 0;

    ArrayList<String> toSendMov = new ArrayList<String>();
    ArrayList<String> toSendHeart = new ArrayList<String>();
    ArrayList<String> toSendPassi = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mButton = (Button) stub.findViewById(R.id.btnWear);

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //sendMessage("/motion_data", "motion data");
                        Log.d("mess", "send btn mess");
                    }
                });
            }
        });

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener((SensorEventListener) this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        senSensorManager.registerListener((SensorEventListener) this, senGravity, SensorManager.SENSOR_DELAY_GAME);
        senLinear = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener((SensorEventListener) this, senLinear, SensorManager.SENSOR_DELAY_GAME);
        senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        senSensorManager.registerListener((SensorEventListener) this, senGyro, SensorManager.SENSOR_DELAY_GAME);
        senStep = senSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        senSensorManager.registerListener((SensorEventListener) this, senStep, SensorManager.SENSOR_DELAY_NORMAL);
        senHeart = senSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        senSensorManager.registerListener((SensorEventListener) this, senHeart, SensorManager.SENSOR_DELAY_NORMAL);

        retrieveDeviceNode();
    }

    //Funzione che istanzia un oggetto GoogleApiClient con le API Wearable
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    //Funzione che rintraccia tutti i nodi raggiungibili e scelgo il migliore a cui mandare poi il pacchetto
    private void retrieveDeviceNode() {
        final GoogleApiClient client = getGoogleApiClient(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(5000, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    for(Node node : nodes) {
                        if (node.isNearby())
                            nodeId = node.getId();
                    }
                }

                client.disconnect();
            }
        }).start();
    }


    //Funzione che manda il messaggio contenente il pacchetto di dati dei sensori
    private void sendMex(String mess, int type) {
        final GoogleApiClient client = getGoogleApiClient(this);
        final String strSend=mess;
        final int tipo = type;

        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(15000, TimeUnit.MILLISECONDS);
                    if (tipo == 1) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/motion", strSend.getBytes());
                    }
                    if (tipo == 2) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/heart", strSend.getBytes());
                    }
                    if (tipo == 3) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/passi", strSend.getBytes());
                    }
                    client.disconnect();

                    Log.d("mess", "send mess");
                }
            }).start();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        Date d = new Date();

        long nowD0 = 0;
        long nowD1 = 0;
        long nowD2 = 0;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            acc[0] = sensorEvent.values[0];
            acc[1] = sensorEvent.values[1];
            acc[2] = sensorEvent.values[2];
            nowD0 = d.getTime();
            received[0]=true;

        }
        if (mySensor.getType() == Sensor.TYPE_GRAVITY) {
            grav[0] = sensorEvent.values[0];
            grav[1] = sensorEvent.values[1];
            grav[2] = sensorEvent.values[2];
            nowD0 = d.getTime();
            received[1]=true;
        }
        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lin[0] = sensorEvent.values[0];
            lin[1] = sensorEvent.values[1];
            lin[2] = sensorEvent.values[2];
            nowD0 = d.getTime();
            received[2]=true;

        }
        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyro[0] = sensorEvent.values[0];
            gyro[1] = sensorEvent.values[1];
            gyro[2] = sensorEvent.values[2];
            nowD0 = d.getTime();
            received[3]=true;
        }

        if (mySensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            passi = sensorEvent.values[0];
            nowD1 = d.getTime();
            received[4]=true;
        }

        if (mySensor.getType() == Sensor.TYPE_HEART_RATE) {
            heart = sensorEvent.values[0];
            nowD2= d.getTime();
            received[5]=true;
        }

        //Il controllo sui received serve per vedere se ho rilevato nuovi dati da tutti i sensori
        if(received[0] && received[1] && received[2] && received[3])
        {
            String pacchetto = "";
            pacchetto = "m" + ";" + nowD0+";"+acc[0]+";"+acc[1]+";"+acc[2]+";"+
                    grav[0]+";"+grav[1]+";"+grav[2]+";"+
                    lin[0]+";"+lin[1]+";"+lin[2]+";"+
                    gyro[0]+";"+gyro[1]+";"+gyro[2];

            toSendMov.add(pacchetto);

            received[0] = received[1] = received[2] = received[3] = false;

            if(toSendMov.size() == 50)
            {
                String strSend = "";
                for(int i=0; i<50; i++)
                    strSend+=toSendMov.get(i)+"\n";

                //Metto 1 come tipo di pacchetto per sensori di movimento
                sendMex(strSend, 1);
                toSendMov.clear();
            }

        }

        if (received[4]) {

            String pac = "";
            pac = "s" + ";" + nowD1 + ";" + passi ;

            toSendPassi.add(pac);

            received[4]= false;

            if(toSendPassi.size()== 5){
                String stringa = "";
                for(int l=0; l<5; l++){
                    stringa+=toSendPassi.get(l)+"\n";
                }

                sendMex(stringa, 3);
                toSendPassi.clear();
            }

        }

        if (received[5]) {

            String pak = "";
            pak = "h" + ";" + nowD2 + ";" + heart ;

            toSendHeart.add(pak);

            received[5]= false;

            if(toSendHeart.size()== 5){
                String stringa = "";
                for(int l=0; l<5; l++){
                    stringa+=toSendHeart.get(l)+"\n";
                }

                sendMex(stringa, 2);
                toSendHeart.clear();
            }

        }

        prevD=nowD0;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
