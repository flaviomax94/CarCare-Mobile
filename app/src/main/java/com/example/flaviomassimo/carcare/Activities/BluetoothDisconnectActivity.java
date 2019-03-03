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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;

public class BluetoothDisconnectActivity extends AppCompatActivity implements Serializable {


    LocationManager locationManager;
    GPSListener locationListener;
    private StorageReference mStorageRef;
    private DatabaseReference mDataBase;
    String UID;
    FirebaseUser user;
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
    String NameFile;
    String[] val;

    TextView paired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth_disconnect);
        paired = (TextView) findViewById(R.id.pb);

        String name = BluetoothSocketShare.getBluetoothSocket().getRemoteDevice().getName();

        paired.setText("       Bluetooth\n       connected:\n          " + name);

        // nota bene questi thread dovrebbero partire appena la connessione è avvenuta, non quando entro qui
        // qui dovrebbero solo essere interrotti

       /* locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        */

    }


    public void buttonDisconnect(View view){

        Button btnDisc= (Button) findViewById(R.id.disconnect);
        btnDisc.setOnClickListener(new disconnectListener());

    }

    public class disconnectListener implements Button.OnClickListener {

        public void onClick(View v){
            BluetoothSocketShare.close();
            RPM_THREAD=SharingValues.getRpmThread();
            GUIDE_ALERT=SharingValues.getNotificationThread();

            if(RPM_THREAD.isAlive())
                RPM_THREAD.interrupt();
            if(GUIDE_ALERT.isAlive())
                GUIDE_ALERT.interrupt();
            /* qui interrompo i thread che collezionano i valori, quando li faccio ripartire(ossia riconnetto il bluetooth)
            * il file viene ricreato e perdo i valori precedenti, perciò devo caricare qui il file con un nome univoco
            * oppure la lista degli elementi che viene aggiornata nei thread
            * */
            mDataBase=FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
            mStorageRef = FirebaseStorage.getInstance().getReference();
            user= FirebaseAuth.getInstance().getCurrentUser();
            UID=user.getUid().toString();
            NameFile=BluetoothSocketShare.getFile().getName();
            val= NameFile.split("_");
            final String CarPlate=val[0];
            Uri file = Uri.fromFile(BluetoothSocketShare.getFile());
            final StorageReference FileRef = mStorageRef.child("paths/"+CarPlate+"/"+NameFile);

            FileRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content

                            String downloadURL=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                            FileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    System.out.println(uri.toString());
                                    System.out.println("TRYING TO INSERT URI IN FIREBASE DATABASE--------------------------");
                                    System.out.println(val[1]);
                                    String[] date=val[1].split("\\.");
                                    System.out.println(date[0]);
                                    Date data=new Date(Long.parseLong(date[0]));
                                    System.out.println(data);
                                    mDataBase.child("Users").child(UID).child("Cars").child(CarPlate).child("Paths").child(data.toString()).setValue(uri.toString());
                                    System.out.println("INSERTED VALUES IN FIREBASE DATABASE READY TO BE READ-------------------------");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast.makeText(getApplicationContext(),"Error in upload file in Firebase Storage",Toast.LENGTH_LONG).show();
                        }
                    });
            //TODO bisogna eliminare il file dallo storage interno
            File file1=BluetoothSocketShare.getFile();
            String filename=file1.getName();
            File file2=new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),filename);
            boolean b=file2.delete();
            BluetoothSocketShare.setFile(null);
            if(b){
            Intent intent = new Intent(BluetoothDisconnectActivity.this, MainMenuActivity.class);
            startActivity(intent);}

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
    public void onBackPressed(){
        Intent i=new Intent(BluetoothDisconnectActivity.this,MainMenuActivity.class);
        startActivity(i);
        finish();

    }

}
