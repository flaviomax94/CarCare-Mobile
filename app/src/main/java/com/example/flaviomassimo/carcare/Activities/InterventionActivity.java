package com.example.flaviomassimo.carcare.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.flaviomassimo.carcare.DataBase.Intervention;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.flaviomassimo.carcare.DataBase.Car;

import com.example.flaviomassimo.carcare.R;

import java.util.ArrayList;

public class InterventionActivity extends AppCompatActivity implements View.OnClickListener{
    private DatabaseReference mRef;
    private Iterable<DataSnapshot> interventions;
    private Button addIntervention,updateIntervention,deleteIntervention;
    private TextView noIntervention, interventionInfo;
    String UID;
    FirebaseUser user;
    private Car car;
    private boolean deleting=false;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView list_interventions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervention);
        list_interventions=(ListView) findViewById(R.id.interventionsList);
        addIntervention= (Button)findViewById(R.id.addIntervention);
        addIntervention.setOnClickListener(this);
        updateIntervention=(Button) findViewById(R.id.update_intervention);
        updateIntervention.setOnClickListener(this);
        updateIntervention.setVisibility(View.GONE);
        deleteIntervention=(Button) findViewById(R.id.delete_intervention);
        deleteIntervention.setOnClickListener(this);
        deleteIntervention.setVisibility(View.GONE);

        noIntervention=(TextView) findViewById(R.id.noIntervention);
        interventionInfo=(TextView) findViewById(R.id.intervetion_info);
        interventionInfo.setVisibility(View.GONE);
        interventionInfo.setMovementMethod(new ScrollingMovementMethod());
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        car=SharingValues.getCar();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").hasChildren()){
                    noIntervention.setVisibility(View.GONE);
                    interventions=dataSnapshot.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").getChildren();

                        adapter = new ArrayAdapter<String>(InterventionActivity.this, android.R.layout.simple_list_item_1, listItems);
                        list_interventions.setAdapter(adapter);
                        list_interventions.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        list_interventions.setOnItemClickListener(
                                new AdapterView.OnItemClickListener(){
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        interventionInfo.setVisibility(View.VISIBLE);
                                        addIntervention.setVisibility(View.GONE);
                                        updateIntervention.setVisibility(View.VISIBLE);
                                        deleteIntervention.setVisibility(View.VISIBLE);
                                        System.out.println(listItems.get(position));
                                        String title=dataSnapshot.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").
                                                child(listItems.get(position)).child("Title").getValue().toString();
                                        String kilometers=dataSnapshot.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").
                                                child(listItems.get(position)).child("Km").getValue().toString();
                                        String descr=dataSnapshot.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").
                                                child(listItems.get(position)).child("Description").getValue().toString();
                                        Intervention intervention=new Intervention(title,car.getLICENSE_PLATE(),Double.parseDouble(kilometers));
                                        intervention.setDESCRIPTION(descr);
                                        SharingValues.setIntervention(intervention);
                                        String infos="Intervention: "+title+"\n"+"Km: "+kilometers+"\n"+"Description: "+descr;
                                        interventionInfo.setText(infos);

                                    }
                                });
                        adapter.clear();
                        listItems.clear();
                        while (interventions.iterator().hasNext()) {
                            DataSnapshot singleIntevention = interventions.iterator().next();
                            listItems.add(singleIntevention.getKey().toString());
                        }


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed(){
        //Intent intent=new Intent(this, MainMenuActivity.class); startActivity(intent);
        SharingValues.setIntervention(null);
        finish();
    }

    @Override
    public void onClick(View v) {
        int i =v.getId();
        if(i==R.id.delete_intervention){
            AlertDialog diaBox = AskOption();
            diaBox.show();

        }
        if(i==R.id.update_intervention){Intent intent= new Intent(this,AddIntervention.class); startActivity(intent);finish();}
        if(i==R.id.addIntervention){Intent intent= new Intent(this,AddIntervention.class); startActivity(intent); finish();}
    }




    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete this intervention?")
                .setIcon(R.drawable.icon_notification)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String key= SharingValues.getIntervention().getTITLE().toString()+" "+Integer.toString(SharingValues.getIntervention().getKM().intValue());
                        mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(key).removeValue();
                        SharingValues.setIntervention(null);
                        dialog.dismiss();
                        Intent intent=new Intent(InterventionActivity.this,InterventionActivity.class);
                        startActivity(intent);
                        finish();

                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}
