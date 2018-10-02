package com.example.flaviomassimo.carcare.Activities;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flaviomassimo.carcare.Activities.Other.BluetoothSocketShare;
import com.example.flaviomassimo.carcare.R;
import com.example.flaviomassimo.carcare.Threads.NOTIFICATION_Thread;
import com.example.flaviomassimo.carcare.Threads.RPM_Thread;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class BluetoothDisconnectActivity extends AppCompatActivity implements Serializable {


    LocationManager locationManager;
    GPSListener locationListener;
    private NotificationChannel channel;
    private NotificationManager mNotificationManager ;
    private NotificationCompat.Builder mBuilder;
    private Intent intentNotification;
    private PendingIntent pi;
    BluetoothSocket socket = BluetoothSocketShare.getBluetoothSocket();
    Thread RPM_THREAD,GUIDE_ALERT;
    InputStream in;
    OutputStream out;
    String ris;
    //TODO Una volta creato il dataset con Pandas disattivare i commenti e attivare il thread delle notifiche

    TextView paired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth_disconnect);
        paired = (TextView) findViewById(R.id.pb);

        String name = BluetoothSocketShare.getBluetoothSocket().getRemoteDevice().getName();

        paired.setText("       Bluetooth\n       connected:\n          " + name);
        if(name.contains("OBD")){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, Looper.getMainLooper());

        createChannel("Channel","Channel_OBD");
        createNotification();
        GUIDE_ALERT= new Thread(new NOTIFICATION_Thread(channel,mNotificationManager,mBuilder,pi));
        RPM_THREAD=new Thread(new RPM_Thread(locationManager,locationListener));

        if(socket.isConnected()){
            in=BluetoothSocketShare.getInputStream();
            out=BluetoothSocketShare.getOutputStream();

            RPM_THREAD.start();
            GUIDE_ALERT.start();
        }
        }
        else{
            Toast.makeText(BluetoothDisconnectActivity.this, "Warning this device is not an OBD.Please connect an OBD Device",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void buttonDisconnect(View view){

        Button btnDisc= (Button) findViewById(R.id.disconnect);
        btnDisc.setOnClickListener(new disconnectListener());

    }

    public class disconnectListener implements Button.OnClickListener {

        public void onClick(View v){
            BluetoothSocketShare.close();
            if(RPM_THREAD.isAlive())
                RPM_THREAD.interrupt();
            if(GUIDE_ALERT.isAlive())
                GUIDE_ALERT.interrupt();
            /*TODO qui interrompo i thread che collezionano i valori, quando li faccio ripartire(ossia riconnetto il bluetooth)
            * il file viene ricreato e perdo i valori precedenti, perciò devo caricare qui il file con un nome univoco
            * oppure la lista degli elementi che viene aggiornata nei thread
            * */

            Intent intent = new Intent(BluetoothDisconnectActivity.this, BluetoothActivity.class);
            startActivity(intent);

        }
    }
    public void homeButtonClick(View view){

        ImageButton homeButton=(ImageButton) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new homeBtnListener());
    }

    public class homeBtnListener implements ImageButton.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(BluetoothDisconnectActivity.this, MainMenuActivity.class);
            startActivity(intent);
        }
    }


    public void createChannel(String title, String content) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        channel = new NotificationChannel("01", "OBD_CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("CHANNEL FOR OBD NOTIFICATION");
        mNotificationManager.createNotificationChannel(channel);
        System.out.println("Canale creato");
    }


    public void createNotification(){


        System.out.println("ENTRATO NEL BUILDER");
        mBuilder = new NotificationCompat.Builder(this,channel.getId() );
        intentNotification = new Intent(getApplicationContext(), MainMenuActivity.class);
        pi = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);



    }

}
