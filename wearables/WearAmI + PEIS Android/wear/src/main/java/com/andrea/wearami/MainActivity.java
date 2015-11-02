package com.andrea.wearami;

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
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity implements SensorEventListener {

    static{
        System.loadLibrary("Peiskernel");
    }

    private TextView mTextView;
    private TextView mButton;

    boolean writeEnabled=false;
    long timestamp = 0;
    float passitmp = 0;

    int inizioreg=1;

    String nodeId;

    int accelerometro = 0;
    int acc_lineare = 0;
    int gravita = 0;
    int giroscopio = 0;
    int cuore = 0;
    int step = 0;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGravity;
    private Sensor senLinear;
    private Sensor senGyro;
    private Sensor senHeart;
    private Sensor senStep;

    float[] acc ={0,0,0};
    float[] grav ={0,0,0};
    float[] lin ={0,0,0};
    float[] gyro ={0,0,0};

    float heart = 0;
    float passi = 0;

    ArrayList<String> toSendMov1 = new ArrayList<String>();
    ArrayList<String> toSendMov2 = new ArrayList<String>();
    ArrayList<String> toSendMov3 = new ArrayList<String>();
    ArrayList<String> toSendMov4 = new ArrayList<String>();
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
                mTextView = (TextView) stub.findViewById(R.id.textT);
                mButton = (Button) stub.findViewById(R.id.btnWearStart);

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        writeEnabled=true;
                        inizioreg=0;
                        timestamp = System.currentTimeMillis();
                        ((TextView) findViewById(R.id.textT)).setText("Pubblico");
                        Log.d("mess", "Start Write");
                    }
                });

                ((Button) stub.findViewById(R.id.btnWearStop)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        writeEnabled = false;
                        timestamp = 0;
                        toSendMov1.clear();
                        toSendMov2.clear();
                        toSendMov3.clear();
                        toSendMov4.clear();
                        toSendHeart.clear();
                        ((TextView)findViewById(R.id.textT)).setText("Non Pubblico");
                        Log.d("mess", "Stop Write");
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

        //retrieveDeviceNode();

        peis_init();
    }

    //Funzione che istanzia un oggetto GoogleApiClient con le API Wearable
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    //Funzione che rintraccia tutti i nodi raggiungibili e scelgo il migliore a cui mandare poi il pacchetto
    /*
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
    */


    //Funzione che manda il messaggio contenente il pacchetto di dati dei sensori
    /*
    private void sendMex(final String mess, final int type_mex, final int type_sensor) {
        final GoogleApiClient client = getGoogleApiClient(this);

        if(writeEnabled) {
            if (timestamp != 0) {

                if (type_sensor == 1) {
                    FileOutputStream fos;
                    File myFile = new File("/sdcard/" + timestamp + "A" + ".txt");
                    try {
                        FileOutputStream fOut = new FileOutputStream(myFile, true);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(mess);
                        myOutWriter.close();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("wear", "errore scrittura");
                    }
                }


                if (type_sensor == 2) {


                    FileOutputStream fos;
                    File myFile = new File("/sdcard/" + timestamp + "L" + ".txt");
                    try {
                        FileOutputStream fOut = new FileOutputStream(myFile, true);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(mess);
                        myOutWriter.close();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("wear", "errore scrittura");
                    }
                }

                if (type_sensor == 3) {


                    FileOutputStream fos;
                    File myFile = new File("/sdcard/" + timestamp + "G" + ".txt");
                    try {
                        FileOutputStream fOut = new FileOutputStream(myFile, true);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(mess);
                        myOutWriter.close();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("wear", "errore scrittura");
                    }
                }

                if (type_sensor == 4) {


                    FileOutputStream fos;
                    File myFile = new File("/sdcard/" + timestamp + "Y" + ".txt");
                    try {
                        FileOutputStream fOut = new FileOutputStream(myFile, true);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(mess);
                        myOutWriter.close();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("wear", "errore scrittura");
                    }
                }

                if (type_sensor == 6) {


                    FileOutputStream fos;
                    File myFile = new File("/sdcard/" + timestamp + "S" + ".txt");
                    try {
                        FileOutputStream fOut = new FileOutputStream(myFile, true);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(mess);
                        myOutWriter.close();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("wear", "errore scrittura");
                    }
                }

                if (type_sensor == 5) {


                    FileOutputStream fos;
                    File myFile = new File("/sdcard/" + timestamp + "H" + ".txt");
                    try {
                        FileOutputStream fOut = new FileOutputStream(myFile, true);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(mess);
                        myOutWriter.close();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("wear", "errore scrittura");
                    }
                }

            }
        }


        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(15000, TimeUnit.MILLISECONDS);
                    if (type_mex == 1) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/motion1", mess.getBytes());
                    }
                    if (type_mex == 2) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/motion2", mess.getBytes());
                    }
                    if (type_mex == 3) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/motion3", mess.getBytes());
                    }
                    if (type_mex == 4) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/motion4", mess.getBytes());
                    }
                    if (type_mex == 5) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/heart", mess.getBytes());
                    }
                    if (type_mex == 6) {
                        Wearable.MessageApi.sendMessage(client, nodeId, "/passi", mess.getBytes());
                    }
                    client.disconnect();

                }
            }).start();
        }

        new Thread(new Runnable(){
               @Override
               public void run(){
                   try {
                       Socket client1 = new Socket("192.168.1.9", 3491);

                       Log.d("Altt", "MESSAGGIOINVIATO");

                       ObjectOutputStream oos = new ObjectOutputStream(client1.getOutputStream());
                       oos.writeObject(mess);
                       oos.close();

                       client1.close();

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();
    }
    */

    private void PubblicaDati(final String mess, final int type_mex){
        if(writeEnabled) {
            if (type_mex == 1) {
                if (accelerometro < 20) {
                    peis_setTuple("1.A." + accelerometro, mess);
                    accelerometro++;
                } else {
                    accelerometro = 0;
                    peis_setTuple("1.A." + accelerometro, mess);
                    accelerometro++;
                }
            }

            if (type_mex == 2) {
                if (acc_lineare < 20) {
                    peis_setTuple("1.L." + acc_lineare, mess);
                    acc_lineare++;
                } else {
                    acc_lineare = 0;
                    peis_setTuple("1.L." + acc_lineare, mess);
                    acc_lineare++;
                }
            }

            if (type_mex == 3) {
                if (gravita < 20) {
                    peis_setTuple("1.G." + gravita, mess);
                    gravita++;
                } else {
                    gravita = 0;
                    peis_setTuple("1.G." + gravita, mess);
                    gravita++;
                }
            }

            if (type_mex == 4) {
                if (giroscopio < 20) {
                    peis_setTuple("1.Y." + giroscopio, mess);
                    giroscopio++;
                } else {
                    giroscopio = 0;
                    peis_setTuple("1.Y." + giroscopio, mess);
                    giroscopio++;
                }
            }

            if (type_mex == 5) {
                if (cuore < 20) {
                    peis_setTuple("1.H." + cuore, mess);
                    cuore++;
                } else {
                    cuore = 0;
                    peis_setTuple("1.H." + cuore, mess);
                    cuore++;
                }
            }

            if (type_mex == 6) {
                if (step < 20) {
                    peis_setTuple("1.S." + step, mess);
                    step++;
                } else {
                    step = 0;
                    peis_setTuple("1.S." + step, mess);
                    step++;
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        long nowD0 = 0;
        long nowD1 = 0;
        long nowD2 = 0;
        long nowD3 = 0;

        long nowD4 = 0;
        long nowD5 = 0;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            acc[0] = sensorEvent.values[0];
            acc[1] = sensorEvent.values[1];
            acc[2] = sensorEvent.values[2];
            nowD0 = sensorEvent.timestamp;

            String pacchetto1 = "";
            pacchetto1 = "a" + ";" + nowD0 + ";" + acc[0] + ";" + acc[1] + ";" + acc[2];
            toSendMov1.add(pacchetto1);

        }
        if (mySensor.getType() == Sensor.TYPE_GRAVITY) {
            grav[0] = sensorEvent.values[0];
            grav[1] = sensorEvent.values[1];
            grav[2] = sensorEvent.values[2];
            nowD1 = sensorEvent.timestamp;

            String pacchetto3 = "";
            pacchetto3 = "g" + ";" + nowD1+";"+grav[0]+";"+grav[1]+";"+grav[2];
            toSendMov3.add(pacchetto3);
        }
        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lin[0] = sensorEvent.values[0];
            lin[1] = sensorEvent.values[1];
            lin[2] = sensorEvent.values[2];
            nowD2 = sensorEvent.timestamp;

            String pacchetto2 = "";
            pacchetto2 = "l" + ";" + nowD2+";"+lin[0]+";"+lin[1]+";"+lin[2];
            toSendMov2.add(pacchetto2);
        }
        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyro[0] = sensorEvent.values[0];
            gyro[1] = sensorEvent.values[1];
            gyro[2] = sensorEvent.values[2];
            nowD3 = sensorEvent.timestamp;

            String pacchetto4 = "";
            pacchetto4 = "y" + ";" + nowD3+";"+gyro[0]+";"+gyro[1]+";"+gyro[2];
            toSendMov4.add(pacchetto4);
        }

        if (mySensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            passi = sensorEvent.values[0];
            nowD4 = sensorEvent.timestamp;

            if(inizioreg==0){
                passitmp=passi;
                inizioreg++;
            }


            String pac = "";
            pac = "s" + ";" + nowD4 + ";" + (passi-passitmp);
            toSendPassi.add(pac);


        }

        if (mySensor.getType() == Sensor.TYPE_HEART_RATE) {
            heart = sensorEvent.values[0];
            nowD5 = sensorEvent.timestamp;

            String pak = "";
            pak = "h" + ";" + nowD5 + ";" + heart;
            toSendHeart.add(pak);
        }

        if (toSendMov1.size() == 50) {
            String strSend = "";
            for (int i = 0; i < 10; i++)
                strSend += toSendMov1.get(i) + "\n";

            PubblicaDati(strSend,1);
            //sendMex(strSend,1, 1);
            toSendMov1.clear();
        }

        if(toSendMov2.size() == 10)
        {
            String strSend = "";
            for(int i=0; i<10; i++)
                strSend+=toSendMov2.get(i)+"\n";

            PubblicaDati(strSend,2);
            //sendMex(strSend,2, 2);
            toSendMov2.clear();
        }

        if(toSendMov3.size() == 10)
        {
            String strSend = "";
            for(int i=0; i<10; i++)
                strSend+=toSendMov3.get(i)+"\n";

            PubblicaDati(strSend,3);
            //sendMex(strSend,3, 3);
            toSendMov3.clear();
        }

        if(toSendMov4.size() == 10)
        {
            String strSend = "";
            for(int i=0; i<10; i++)
                strSend+=toSendMov4.get(i)+"\n";

            PubblicaDati(strSend,4);
            //sendMex(strSend,4, 4);
            toSendMov4.clear();
        }

        if(toSendPassi.size()== 1){
            String stringa = "";
            for(int l=0; l<1; l++){
                stringa+=toSendPassi.get(l)+"\n";
            }

            PubblicaDati(stringa,6);
            //sendMex(stringa,6, 6);
            toSendPassi.clear();
        }

        if(toSendHeart.size()== 1){
            String stringa = "";
            for(int l=0; l<1; l++){
                stringa+=toSendHeart.get(l)+"\n";
            }

            PubblicaDati(stringa,5);
            //sendMex(stringa,5, 5);
            toSendHeart.clear();
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public native void peis_init();
    public native void peis_setTuple(String key, String data);
    public native String peis_getTuple(int owner, String key);
    public native String peis_getTupleByAbstract(String key);

}
