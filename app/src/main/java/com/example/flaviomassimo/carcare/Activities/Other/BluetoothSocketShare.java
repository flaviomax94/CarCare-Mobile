package com.example.flaviomassimo.carcare.Activities.Other;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class BluetoothSocketShare extends Application {

    private static BluetoothSocket SOCKET;
    private static InputStream in;
    private static OutputStream out;
    private static File file;
    private static FileOutputStream fileOut;
    private static PrintWriter print;
    private static String FUEL_TYPE;
    public static void setBluetoothSocket(BluetoothSocket bts) throws IOException {
        if(bts!= null){

            SOCKET=bts;
            BluetoothSocketShare.connect();
            in=SOCKET.getInputStream();
            out=SOCKET.getOutputStream();

        }
        else {

            //Intent intent = new Intent(, BluetoothDisconnectActivity.class);
            //startActivity(intent);
        }
    }
    public static BluetoothSocket getBluetoothSocket(){

        return SOCKET;
    }

    private static void connect(){


        try {
            System.out.println("Prova connessione socket");
            SOCKET.connect();
            System.out.println("Socket connessa");
            System.out.println(SOCKET);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in BluetoothSocket connection");
        }
    }
    public static void close(){

        try {
            System.out.println("Prova chiusura socket");
            SOCKET.close();
            System.out.println("Socket chiusa");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in closing BluetoothSocket ");
        }
    }

    public static InputStream getInputStream() {
        return in;
    }
    public static OutputStream getOutputStream(){
        return out;
    }

    public static String readResponse (InputStream in) throws IOException {


        StringBuilder res = new StringBuilder();

        byte b;
        while((b = (byte)in.read()) > -1) {
            char c = (char)b;
            if (c == '>') {
                break;
            }

            res.append(c);
        }
        return res.toString();
    }

    public static void setFile(File f){
        file=f;

    }
    public static void setOutputStream(FileOutputStream fOut){
        fileOut=fOut;
    }
    public static void setPrintWriter(PrintWriter pw){
        print=pw;
    }

    public static File getFile(){return file;}
    public static FileOutputStream getFileOutputStream(){return fileOut;}
    public static PrintWriter getPrintWriter(){return print;}


    public static void setFuelType(String fuel){
        FUEL_TYPE=fuel;
    }

    public static String getFuelType(){
        return FUEL_TYPE;
    }

}
