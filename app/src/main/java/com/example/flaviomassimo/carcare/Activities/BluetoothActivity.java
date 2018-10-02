package com.example.flaviomassimo.carcare.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import com.example.flaviomassimo.carcare.R;
public class BluetoothActivity extends AppCompatActivity {
    BluetoothDevice device=null;

    BluetoothSocketShare socket;

    Context context=BluetoothActivity.this;

    static File output;
    FileOutputStream fOut ;
    PrintWriter pw;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(device==null)
            setContentView(R.layout.activity_bluetooth);
        System.out.println(output);
        initFile();

        CharSequence fuel[] = new CharSequence[] {"Gasoline", "Petrol", "GPL", "Other"};

        builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
        builder.setTitle("Select your car's fuel type");
        builder.setItems(fuel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fueltype="";
                if(which==0) fueltype="Gasoline";
                if(which==1) fueltype="Petrol";
                if(which==2) fueltype="GPL";
                if(which==3) fueltype="Other";
                BluetoothSocketShare.setFuelType(fueltype);
            }
        });
        if(BluetoothSocketShare.getFuelType()==null)
            builder.show();

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
                        //Intent intent = new Intent(BluetoothActivity.this, BluetoothDisconnectActivity.class);
                        //startActivity(intent);
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
                File directory = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Output_File_OBD");
                System.out.println(directory);
                output= new File(directory,"OBD_FILE.csv");
                System.out.println(output);
                BluetoothSocketShare.setFile(output);
                try {
                    fOut= new FileOutputStream(output);
                    BluetoothSocketShare.setOutputStream(fOut);
                    pw= new PrintWriter(fOut);
                    BluetoothSocketShare.setPrintWriter(pw);
                    pw.println("RPM,Time,Speed,Latitude,Longitude");
                    pw.flush();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            if(!output.exists()) {
                File directory = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Output_File_OBD");
                output= new File(directory,"OBD_FILE.csv");
                BluetoothSocketShare.setFile(output);
                try {
                    fOut= new FileOutputStream(output);
                    BluetoothSocketShare.setOutputStream(fOut);
                    pw= new PrintWriter(fOut);
                    BluetoothSocketShare.setPrintWriter(pw);
                    pw.println("RPM,Time,Speed,Latitude,Longitude");
                    pw.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }



}
