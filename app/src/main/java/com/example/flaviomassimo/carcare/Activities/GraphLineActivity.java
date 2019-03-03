package com.example.flaviomassimo.carcare.Activities;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.flaviomassimo.carcare.Activities.Other.BluetoothSocketShare;
import com.example.flaviomassimo.carcare.DataBase.Rpm;
import com.example.flaviomassimo.carcare.R;
import com.example.flaviomassimo.carcare.Threads.ChangingValuesThread;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class GraphLineActivity extends AppCompatActivity implements View.OnClickListener {
    LinkedList<Rpm> rpmLinkedList= SharingValues.getRpmList();
    private LineChart mChart;
    public static LineDataSet dataset=null;
    private Thread changingValues;
    DatabaseReference mRef;
    String UID;
    String fuelDB="";
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_line);
        mChart= findViewById(R.id.linechart);
        mChart.setBackgroundColor(Color.WHITE);
        BluetoothSocket socket= BluetoothSocketShare.getBluetoothSocket();
        TextView RPMtext = (TextView) findViewById(R.id.insertRPM);
        TextView SPEEDtext = (TextView) findViewById(R.id.insertKM);
        ImageButton refresh=(ImageButton)findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        if(socket!=null){
            changingValues= new Thread((new ChangingValuesThread(RPMtext,SPEEDtext)));
            changingValues.start();
            System.out.println("CHANGING VALUES THREAD STARTED............................");
        }
        else{
            RPMtext.setText("------");
            SPEEDtext.setText("------km/h");

        }
        LinkedList<Entry> entries= new LinkedList<>();
        int i=0,j=0;
        if(rpmLinkedList.isEmpty()) popolateGaph();
        for(Rpm r: rpmLinkedList){
            j++;
            i++;
            float val=Float.parseFloat(r.getRpmValue());
            if(j==5){
                entries.add(new Entry(i,val));
                j=0;
            }
        }


        dataset= new LineDataSet(entries,"RPM Graph values");

        LineData data= new LineData(dataset);
        Description d= new Description();
        Rpm first= rpmLinkedList.getFirst();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fuelDB=dataSnapshot.child("Users").child(UID).child("Cars").child(SharingValues.getCar().getLICENSE_PLATE()).child("Fuel").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        d.setText(fuelDB);
        d.setTextSize(15);
        mChart.setData(data);
        mChart.setDescription(d);
        mChart.setPinchZoom(true);
        mChart.setDragEnabled(true);
        mChart.setDoubleTapToZoomEnabled(true);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        mChart.setDrawBorders(true);

    }

    @Override
    public void onBackPressed(){
        Intent i=new Intent(GraphLineActivity.this,MainMenuActivity.class);
        startActivity(i);

    }

    public static void addValuetoGraph(Entry entry){
        if(!entry.equalTo(null)){
            dataset.addEntry(entry);

        }

    }

    private void popolateGaph(){

        Rpm val1= new Rpm();
        Rpm val2= new Rpm();
        Rpm val3= new Rpm();
        Rpm val4= new Rpm();
        Rpm val5= new Rpm();
        Rpm val6= new Rpm();
        Rpm val7= new Rpm();
        Rpm val8= new Rpm();
        String fuel;
        if(BluetoothSocketShare.getFuelType()!=null)
            fuel=BluetoothSocketShare.getFuelType();
        else fuel="NULL";
        val1.setFuelType(fuel);

        LinkedList<Rpm> l= new LinkedList<>();
        val1.setId(Calendar.getInstance().getTime().getTime());
        val1.setRpmValue("2500");

        l.add(val1);

        val2.setId(Calendar.getInstance().getTime().getTime()+1);
        val2.setRpmValue("3000");

        l.add(val2);

        val3.setId(Calendar.getInstance().getTime().getTime()+2);
        val3.setRpmValue("2000");
        l.add(val3);

        val4.setId(Calendar.getInstance().getTime().getTime()+3);
        val4.setRpmValue("4000");
        l.add(val4);

        val5.setId(Calendar.getInstance().getTime().getTime()+4);
        val5.setRpmValue("5000");
        l.add(val5);

        val6.setId(Calendar.getInstance().getTime().getTime()+5);
        val6.setRpmValue("2500");
        l.add(val6);

        val7.setId(Calendar.getInstance().getTime().getTime()+6);
        val7.setRpmValue("2500");
        l.add(val7);

        val8.setId(Calendar.getInstance().getTime().getTime()+7);
        val8.setRpmValue("1000");
        l.add(val8);

        rpmLinkedList=l;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.refresh){
            Intent in =new Intent(GraphLineActivity.this, GraphLineActivity.class);
            startActivity(in);
            finish();
        }
    }
}
