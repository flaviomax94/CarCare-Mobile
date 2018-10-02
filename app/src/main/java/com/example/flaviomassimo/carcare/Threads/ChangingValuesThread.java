package com.example.flaviomassimo.carcare.Threads;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

import com.example.flaviomassimo.carcare.Activities.Other.BluetoothSocketShare;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;

import java.io.IOException;

public class ChangingValuesThread extends Application implements Runnable {
    BluetoothSocket socket=BluetoothSocketShare.getBluetoothSocket();
    TextView rpm, consumption, speed;
    RPMCommand rpmComm=new RPMCommand();
    SpeedCommand speedComm=new SpeedCommand();

    public ChangingValuesThread(TextView r, TextView s) {

        rpm = r;
        speed = s;


    }

    public void run() {
        while (!Thread.currentThread().isInterrupted() && BluetoothSocketShare.getBluetoothSocket().isConnected()) {
            try {
                rpmComm.run(socket.getInputStream(), socket.getOutputStream());
                Thread.sleep(150);
                speedComm.run(socket.getInputStream(), socket.getOutputStream());
                Thread.sleep(150);


                if(socket.isConnected()){
                    String RPM = rpmComm.getCalculatedResult();
                    rpm.setText(RPM);
                    String SPEED = speedComm.getCalculatedResult();
                    speed.setText(SPEED);}
                else{
                    rpm.setText("0");
                    speed.setText("0");

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

