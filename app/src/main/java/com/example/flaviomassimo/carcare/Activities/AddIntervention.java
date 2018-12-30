package com.example.flaviomassimo.carcare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.flaviomassimo.carcare.DataBase.Car;
import com.example.flaviomassimo.carcare.DataBase.Intervention;
import com.example.flaviomassimo.carcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddIntervention extends AppCompatActivity implements View.OnClickListener{
    private EditText TITLE,PLATE,KM,DESCRIPTION;
    private Button addIntervention,updateIntervention;
    private DatabaseReference mRef;
    String UID;
    FirebaseUser user;
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_intervention);
        PLATE=(EditText) findViewById(R.id.Plate);
        TITLE=(EditText) findViewById(R.id.Title);
        DESCRIPTION=(EditText) findViewById(R.id.Description);
        KM=(EditText) findViewById(R.id.Km);
        addIntervention=(Button) findViewById(R.id.addIntervention);
        addIntervention.setOnClickListener(this);
        updateIntervention=(Button) findViewById(R.id.updateIntervention);
        updateIntervention.setOnClickListener(this);
        car=SharingValues.getCar();
        PLATE.setText(car.getLICENSE_PLATE());
        PLATE.setEnabled(false);
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        if(SharingValues.getIntervention()!=null){
            Intervention interv=SharingValues.getIntervention();
            TITLE.setText(interv.getTITLE());
            DESCRIPTION.setText(interv.getDESCRIPTION());
            int kilo=interv.getKM().intValue();
            KM.setText(Integer.toString(kilo));
            SharingValues.setIntervention(null);
            addIntervention.setVisibility(View.GONE);
            updateIntervention.setVisibility(View.VISIBLE); }
        else{
            addIntervention.setVisibility(View.VISIBLE);
            updateIntervention.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {
        int i=v.getId();
        if(i==R.id.addIntervention){ addIntervention();
        }
        if(i==R.id.updateIntervention){upgradeIntervention();
        }

    }
    public void addIntervention(){

        if(validateForm()){
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString()).
                    child("Title").setValue(TITLE.getText().toString());


            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString()).
                    child("Km").setValue(KM.getText().toString());

            if(DESCRIPTION.getText()!=null){
                mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString()).
                        child("Description").setValue(DESCRIPTION.getText().toString());
            }
            else{
                mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString()).
                        child("Description").setValue("");
            }
            Intent j =new Intent(this, InterventionActivity.class); startActivity(j);
            finish();

        }
    }
    public void upgradeIntervention(){
        if(validateForm()){
        mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString());
        mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString()).
                child("Title").setValue(TITLE.getText().toString());


        mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString()).
                child("Km").setValue(KM.getText().toString());

        if(DESCRIPTION.getText()!=null){
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString()).
                    child("Description").setValue(DESCRIPTION.getText().toString());
        }
        else{
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Interventions").child(TITLE.getText().toString()+" "+KM.getText().toString()).
                    child("Description").setValue("");
        }
            Intent j =new Intent(this, InterventionActivity.class); startActivity(j);
            finish();
        }

    }

    private boolean validateForm() {
        boolean valid = true;

        String title = TITLE.getText().toString();
        if (TextUtils.isEmpty(title)) {
            TITLE.setError("Required.");
            valid = false;
        } else {
            PLATE.setError(null);
        }

        String kilometers = KM.getText().toString();

        if (TextUtils.isEmpty(kilometers)) {
            KM.setError("Required.");
            valid = false;
        } else {
            KM.setError(null);
        }

        return valid;
    }
}
