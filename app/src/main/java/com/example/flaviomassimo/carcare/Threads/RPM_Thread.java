package com.example.flaviomassimo.carcare.Threads;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.location.Location;
import android.location.LocationManager;

import com.example.flaviomassimo.carcare.Activities.GPSListener;
import com.example.flaviomassimo.carcare.Activities.Other.BluetoothSocketShare;
import com.example.flaviomassimo.carcare.Activities.SharingValues;
import com.example.flaviomassimo.carcare.DataBase.Rpm;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.HeadersOffCommand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import com.google.firebase.storage.*;
public class RPM_Thread extends Application implements Runnable {
    BluetoothSocket socket;
    LinkedList<Rpm> rpmLinkedList= SharingValues.getRpmList();
    static File output;
    FileOutputStream fOut ;
    PrintWriter pw;
    GPSListener locationListener;
    LocationManager locationManager;
    InputStream in;
    OutputStream out;
    String ris;

    //FirebaseStorage mFirebaseStorage=FirebaseStorage.get;
    public RPM_Thread(LocationManager locM, GPSListener locL) {

        socket = BluetoothSocketShare.getBluetoothSocket();
        in=BluetoothSocketShare.getInputStream();
        out= BluetoothSocketShare.getOutputStream();
        locationListener=locL;
        locationManager= locM;
        output=BluetoothSocketShare.getFile();
        fOut=BluetoothSocketShare.getFileOutputStream();
        pw=BluetoothSocketShare.getPrintWriter();


    }
    // TODO verificare che le MissingPermission non diano problemi
    @SuppressLint("MissingPermission")

    public void run() {
        int counter=0;


        try {

            initObd();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RPMCommand rpmComm = new RPMCommand();
        SpeedCommand speed=new SpeedCommand();

        while (!Thread.currentThread().isInterrupted() && BluetoothSocketShare.getBluetoothSocket().isConnected()) {
            try {
                counter++;
                rpmComm.run(socket.getInputStream(), socket.getOutputStream());
                Thread.sleep(50);
                speed.run(socket.getInputStream(), socket.getOutputStream());
                Thread.sleep(50);

                String RPM=""+rpmComm.getRPM();
                String fuel;
                if(BluetoothSocketShare.getFuelType()!=null)
                    fuel = BluetoothSocketShare.getFuelType();
                else fuel="NULL";
                String speedValue=speed.getCalculatedResult();


                Rpm rpm = new Rpm();
                Date currentTime = Calendar.getInstance().getTime();

                rpm.setDate(currentTime.toString());
                rpm.setId(currentTime.getTime());
                rpm.setRpmValue(RPM);
                rpm.setFuelType(fuel);
                rpm.setSpeed(speedValue);
                //String result="\n Date: "+rpm.getDate().toString()+"   "+"Rpm: "+rpm.getRpmValue()+"   "+"Millis: "+rpm.getId()+"   "+
                //"Speed: "+rpm.getSpeed()+"   "+"Fuel: "+rpm.getFuelType()+"   "+"Position: ";
                String result=rpm.getRpmValue()+" "+rpm.getId()+" "+rpm.getSpeed();

                if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                    Location loc=getLastKnownLocation();
                    locationListener.onLocationChanged(loc);
                    rpm.setPosition(locationListener.getLocation());
                    result=result+" "+(rpm.getPosition().toString());

                }
                else {
                    result=result+" Unknown";
                }

                System.out.println(result);
                pw.println(result);
                System.out.println(pw);
                System.out.println(output);
                pw.flush();
                pw.println("\n");
                pw.flush();

               rpmLinkedList.add(rpm);



            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("RPM THREAD INTERRUPTED----------------------");
            }


        }


        try {
            pw.close();
            fOut.close();
        } catch (IOException e) {
            System.out.println("Error in closing file writer");
            e.printStackTrace();

        }
    }

    public void stop(){

        Thread.currentThread().interrupt();
    }
    private void initObd() throws  IOException,InterruptedException{

        out.write(("AT D" + "\r").getBytes());
        out.flush();
        //Set all to defaults
        System.out.println("AT D sended");
        ris=BluetoothSocketShare.readResponse(in);
        System.out.println(ris);

        out.write(("AT Z" + "\r").getBytes());
        out.flush();
        //Reset obd
        System.out.println("AT Z sended");
        ris=BluetoothSocketShare.readResponse(in);
        System.out.println(ris);

        out.write(("AT E0" + "\r").getBytes());
        out.flush();
        //Echo off
        System.out.println("AT E0 sended");
        ris=BluetoothSocketShare.readResponse(in);
        System.out.println(ris);

        out.write(("AT L0" + "\r").getBytes());
        out.flush();
        //Line feed off
        System.out.println("AT L0 sended");
        ris= BluetoothSocketShare.readResponse(in);
        System.out.println(ris);

        new EchoOffCommand().run(in,out);
        System.out.println("Echocommand fatto");
        new HeadersOffCommand().run(in,out);
        System.out.println("Headeroff fatto");
        Thread.sleep(2000);


        out.write(("AT S0" + "\r").getBytes());
        out.flush();
        //Space off
        System.out.println("AT S0 sended");
        ris=BluetoothSocketShare.readResponse(in);
        System.out.println(ris);

        out.write(("AT H0" + "\r").getBytes());
        out.flush();
        //Headers off
        System.out.println("AT H0 sended");
        ris=BluetoothSocketShare.readResponse(in);
        System.out.println(ris);


        out.write(("AT SP 0" + "\r").getBytes());
        out.flush();
        //Set Protocol to 0 "Auto", search all protocols and connect it with proper protocol for that obd
        System.out.println("AT SP 0 sended");
        ris=BluetoothSocketShare.readResponse(in);
        System.out.println(ris);
    }
    private Location getLastKnownLocation(){

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission")
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;

    }
}