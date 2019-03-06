package com.example.flaviomassimo.carcare.Threads;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothSocket;
import android.support.v4.app.NotificationCompat;

import com.example.flaviomassimo.carcare.Activities.Other.BluetoothSocketShare;
import com.example.flaviomassimo.carcare.Activities.SharingValues;
import com.example.flaviomassimo.carcare.DataBase.Rpm;
import com.example.flaviomassimo.carcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NOTIFICATION_Thread extends Application implements Runnable {
    BluetoothSocket socket;
    private boolean sendedRPM=false,sendedSpeed=false;
    int covariance_soil=0,soil_counter=0;
    private NotificationChannel channel;
    private NotificationManager mNotificationManager ;
    private NotificationCompat.Builder mBuilder;
    private PendingIntent pendingIntent;
    private DatabaseReference mDataBase;
    String UID;
    FirebaseUser user;

    public NOTIFICATION_Thread (NotificationChannel chan, NotificationManager man, NotificationCompat.Builder build,PendingIntent pending) {

        socket = BluetoothSocketShare.getBluetoothSocket();
        channel=chan;
        mNotificationManager=man;
        mBuilder=build;
        pendingIntent=pending;
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mDataBase= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
    }


    public void run(){

        while (!Thread.currentThread().isInterrupted() && BluetoothSocketShare.getBluetoothSocket().isConnected()) {

            try {
                Thread.currentThread().sleep(5000);
                LinkedList<Rpm> list= SharingValues.getRpmList();
                for (Rpm r:list) {
                    int speedValue = Integer.parseInt(r.getSpeed());
                    if (speedValue != 0) {
                        if (speedValue > 140 && !sendedSpeed) {
                            Date currentTime = Calendar.getInstance().getTime();
                            mDataBase.child("Users").child(UID).child("Notifications").child(currentTime.toString()).setValue("High Speed Notification");
                            createNotification("BE CAREFUL!", "You're over the maximum speed limit!", R.drawable.rpm_nero);
                            mNotificationManager.notify(0, mBuilder.build());
                            sendedSpeed=true;

                        }
                    }
                }
                if(list.size()>50){
                    double[][] Matrix=slidingWindow(list,50);
                    RealMatrix mx= MatrixUtils.createRealMatrix(Matrix);
                    RealMatrix cov=new Covariance(mx).getCovarianceMatrix();
                    scanMatrix(cov);
                }
                if((covariance_soil>600000|| soil_counter>100) && !sendedRPM){
                    covariance_soil=0;
                    soil_counter=0;
                    Date currentTime = Calendar.getInstance().getTime();
                    mDataBase.child("Users").child(UID).child("Notifications").child(currentTime.toString()).setValue("Reckless Guide Notification");
                    createNotification("WARNING!","You have a reckless driving style!",R.drawable.icona_auto);
                    mNotificationManager.notify(0, mBuilder.build());
                    sendedRPM=true;


                }





            }catch(ConcurrentModificationException x){
                System.out.println("CONCURRENT MODIFICATION EXCEPTION");
            } catch (InterruptedException e) {
                System.out.println("NOTIFICATION THREAD INTERRUPTED----------------------------");
            }

        }
    }


    public void stop(){

        Thread.currentThread().interrupt();
    }


    public void createNotification(String title, String text,int Icon){


        System.out.println("ENTRATO NELLA CREAZIONE DELLA NOTIFICA");
        mBuilder.setSmallIcon(Icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        System.out.println("notifica creata");
        mBuilder.setContentIntent(pendingIntent);


    }



    private void scanMatrix(RealMatrix m){
        soil_counter=0;
        double max=0;
        int i,j;
        double[][] matrice=m.getData();
        for(i=0;i<matrice.length;i++){
            for(j=0;j<matrice[0].length;j++){
                if((int)matrice[i][j]>550000) soil_counter++;
                if(matrice[i][j]>max){
                    max=matrice[i][j];
                }
            }
        }
        covariance_soil=(int)max;

    }
    private static double[][] slidingWindow(LinkedList<Rpm> list,int size){
        int[] arr=ListToArray(list);
        double[][] ris= new double[arr.length-size+1][size];
        double[] temp=new double[size];
        int i=0,j,k=0;
        for(i=0;i<arr.length-size+1;i++){
            for(j=i;j<i+size;j++){
                temp[k]=(double)arr[j];
                k++;
            }
            k=0;
            ris[i]=temp;
            temp= new double[size];

        }
        return ris;
    }



    public static int[] ListToArray(List<Rpm> list){
        Iterator it = list.iterator();
        int i=0;
        int[] arr= new int[list.size()];
        while(it.hasNext()){
            Rpm r=(Rpm)it.next();
            int x=Integer.parseInt(r.getRpmValue());
            arr[i]=x;
            i++;
        }
        return arr;
    }
}
