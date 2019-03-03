package com.example.flaviomassimo.carcare.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.flaviomassimo.carcare.Activities.Other.BluetoothSocketShare;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.example.flaviomassimo.carcare.DataBase.Car;
import com.example.flaviomassimo.carcare.R;
import com.example.flaviomassimo.carcare.Threads.NOTIFICATION_Thread;
import com.example.flaviomassimo.carcare.Threads.RPM_Thread;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BluetoothActivity extends AppCompatActivity {

    LocationManager locationManager;
    GPSListener locationListener;
    private NotificationChannel channel;
    private NotificationManager mNotificationManager ;
    private NotificationCompat.Builder mBuilder;
    private Intent intentNotification;
    private PendingIntent pi;
    BluetoothSocket socket = BluetoothSocketShare.getBluetoothSocket();
    InputStream in;
    OutputStream out;
    Thread RPM_THREAD,GUIDE_ALERT;
    BluetoothDevice device=null;
    Context context=BluetoothActivity.this;
    AlertDialog.Builder builderCars;
    static File output;
    FileOutputStream fOut ;
    PrintWriter pw;
    String FileName;
    private DatabaseReference mRef;
    String UID;
    FirebaseUser user;
    private Iterable<DataSnapshot> cars;
    ArrayList<String> carsList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(device==null)
            setContentView(R.layout.activity_bluetooth);

        // check if car is connected otherwise connect one of them
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        builderCars = new AlertDialog.Builder(this);
        builderCars.setTitle("Select the car that you are driving");
        mRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange ( final DataSnapshot dataSnapshot) {
                if(!((Activity) context).isFinishing()){
                    if(dataSnapshot.child("Users").child(UID).child("Cars").hasChildren()){
                cars = dataSnapshot.child("Users").child(UID).child("Cars").getChildren();

                while (cars.iterator().hasNext()) {
                    DataSnapshot singlecar = cars.iterator().next();
                    String plate = singlecar.getKey().toString();
                    System.out.println(plate);
                    carsList.add(plate);
                }
                final String[] values = carsList.toArray(new String[carsList.size()]);
                ArrayAdapter adapterCars = new ArrayAdapter(BluetoothActivity.this, android.R.layout.select_dialog_singlechoice, values);

                builderCars.setSingleChoiceItems(adapterCars, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        String selectedPlate = values[position];
                        if(selectedPlate.equals(null)) selectedPlate="GeneralVehicle";
                        FileName = selectedPlate + "_" + System.currentTimeMillis();
                        initFile();
                        SharingValues.setCar(new Car(selectedPlate));
                    }

                });
                builderCars.show();
            }
            else{
                        Toast.makeText(BluetoothActivity.this, "Please, first add at least a vehicle...", Toast.LENGTH_LONG).show();
                        Intent i =new Intent(BluetoothActivity.this,MainMenuActivity.class);
                        startActivity(i);
                        finish();
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });




    }

    public void searchButtonClick(View view){

        Button srcButton=(Button) findViewById(R.id.searchButton);
        srcButton.setOnClickListener(new SearchListener());
    }

    public class SearchListener implements Button.OnClickListener {

        public void onClick(View view) {

            ArrayList deviceStrs = new ArrayList();
            final ArrayList devices = new ArrayList();

            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices.size() > 0)
            {

                for (BluetoothDevice device : pairedDevices)
                {
                    deviceStrs.add(device.getName() + "\n" + device.getAddress());
                    devices.add(device.getAddress());
                }
            }

            // show list
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogStyle);

            ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.select_dialog_singlechoice,
                    deviceStrs.toArray(new String[deviceStrs.size()]));

            alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which)
                {

                    dialog.dismiss();
                    int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    String deviceAddress = devices.get(position).toString();

                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                    device = btAdapter.getRemoteDevice(deviceAddress);

                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


                    try {
                        BluetoothSocketShare.setBluetoothSocket(device.createInsecureRfcommSocketToServiceRecord(uuid));
                    } catch (IOException e) {
                        System.out.println("Error on createInsecureSockettoServiceRecord");
                    }

                    if(BluetoothSocketShare.getBluetoothSocket()==null){
                        Toast.makeText(BluetoothActivity.this,"Bluetooth not connected, retry.",Toast.LENGTH_SHORT).show();
                    }
                    else if(!BluetoothSocketShare.getBluetoothSocket().isConnected()){

                        Toast.makeText(BluetoothActivity.this,"Bluetooth not connected, retry.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(BluetoothSocketShare.getBluetoothSocket().isConnected()){
                            if(BluetoothSocketShare.getBluetoothSocket().getRemoteDevice().getName().contains("OBD")){
                                Toast.makeText(BluetoothActivity.this, "OBD Device found, setup connection...", Toast.LENGTH_SHORT).show();

                                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                locationListener = new GPSListener();
                                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, Looper.getMainLooper());

                                createChannel("Channel","Channel_OBD");
                                createNotification();
                                System.out.println("CHANNEL CREATO-------------------------------------");
                                GUIDE_ALERT= new Thread(new NOTIFICATION_Thread(channel,mNotificationManager,mBuilder,pi));
                                RPM_THREAD=new Thread(new RPM_Thread(locationManager,locationListener));
                                SharingValues.setNotificationThread(GUIDE_ALERT);
                                SharingValues.setRpmThread(RPM_THREAD);
                                if(BluetoothSocketShare.getBluetoothSocket().isConnected()){
                                    in=BluetoothSocketShare.getInputStream();
                                    out=BluetoothSocketShare.getOutputStream();

                                    RPM_THREAD.start();
                                    GUIDE_ALERT.start();

                                    System.out.println("THREADS CREATI E PARTITI---------------------------");
                                }


                            }
                                Intent intent = new Intent(BluetoothActivity.this, MainMenuActivity.class);
                            try {
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException e) {
                                System.out.println("ERROR IN WAIT FOR OBD SETUP COMMANDS---------");
                            }
                            startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(BluetoothActivity.this, "Warning! This device is not an OBD.Please connect an OBD Device",
                                        Toast.LENGTH_SHORT).show();
                                device = null;

                            }

                        }

                    }

            });

            alertDialog.setTitle("Choose Bluetooth device");
            alertDialog.show();
        }

    }


    private void initFile(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            output=BluetoothSocketShare.getFile();
            if(output==null){

                output= new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),FileName+".txt");
                try {
                    output.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(output);
                BluetoothSocketShare.setFile(output);
                System.out.println(BluetoothSocketShare.getFile());
                try {
                    fOut= new FileOutputStream(output);
                    BluetoothSocketShare.setOutputStream(fOut);
                    pw= new PrintWriter(fOut);
                    BluetoothSocketShare.setPrintWriter(pw);
                    //pw.println("RPM,Time,Speed,Latitude,Longitude");
                    //pw.flush();
                    //System.out.println("PRINTED");

                } catch (FileNotFoundException e) {
                    System.out.println("File non trovato sezione 1");
                }

            }

            if(!output.exists()) {
                output= new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),FileName+".txt");
                try {
                    output.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BluetoothSocketShare.setFile(output);
                try {
                    fOut= new FileOutputStream(output);
                    BluetoothSocketShare.setOutputStream(fOut);
                    pw= new PrintWriter(fOut);
                    BluetoothSocketShare.setPrintWriter(pw);
                    //pw.println("RPM,Time,Speed,Latitude,Longitude");
                    //pw.flush();
                } catch (FileNotFoundException e) {
                    System.out.println("File non trovato sezione 2");
                }
            }
        }

    }
    @Override
    public void onBackPressed(){
        Intent i=new Intent(BluetoothActivity.this,MainMenuActivity.class);
        startActivity(i);
        finish();

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
