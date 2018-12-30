package com.example.flaviomassimo.carcare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.flaviomassimo.carcare.DataBase.Car;
import com.example.flaviomassimo.carcare.Fragment.CarFragment;
import com.example.flaviomassimo.carcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCarActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{
    private ListView list_cars;
    private Iterable<DataSnapshot> cars;
    private boolean existingCars=false;
    private CarFragment.OnFragmentInteractionListener mListener;
    private Spinner showMenu;
    private EditText license,make,model,km;
    private Button addCar,updateCar;
    private Car car=new Car();
    private DatabaseReference mRef;
    private String fuel_temp="";
    String UID;
    FirebaseUser user;
    private static final String[] items = {"Gasoline", "Petrol", "GPL", "Other"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        showMenu=(Spinner) findViewById(R.id.show_dropdown_menu);
        license=(EditText) findViewById(R.id.LicensePlate);
        make=(EditText) findViewById(R.id.Make);
        model=(EditText) findViewById(R.id.Model);
        km=(EditText) findViewById(R.id.Km);
        addCar=(Button) findViewById(R.id.addButton);
        addCar.setOnClickListener(this);
        updateCar=(Button) findViewById(R.id.updateCar);
        updateCar.setOnClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        showMenu.setAdapter(adapter);
        showMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        car.setFUEL_TYPE("Gasoline");
                        fuel_temp="Gasoline";
                        break;
                    case 1:
                        car.setFUEL_TYPE("Petrol");
                        fuel_temp="Petrol";
                        break;
                    case 2:
                        car.setFUEL_TYPE("GPL");
                        fuel_temp="GPL";
                        break;
                    case 3:
                        car.setFUEL_TYPE("Other");
                        fuel_temp="Other";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Car oldCar=SharingValues.getCar();
                if(oldCar!=null){
                    addCar.setVisibility(View.GONE);
                    updateCar.setVisibility(View.VISIBLE);
                    license.setText(oldCar.getLICENSE_PLATE());
                    license.setEnabled(false);
                    make.setText(dataSnapshot.child("Users").child(UID).child("Cars").child(oldCar.getLICENSE_PLATE()).child("Make").getValue().toString());
                    model.setText(dataSnapshot.child("Users").child(UID).child("Cars").child(oldCar.getLICENSE_PLATE()).child("Model").getValue().toString());
                    km.setText(dataSnapshot.child("Users").child(UID).child("Cars").child(oldCar.getLICENSE_PLATE()).child("Km").getValue().toString());
                    String fuel=dataSnapshot.child("Users").child(UID).child("Cars").child(oldCar.getLICENSE_PLATE()).child("Fuel").getValue().toString();
                    fuel_temp=fuel;
                    int temp=0;
                    if(fuel.equals("Gasoline")) temp=0;
                    else if (fuel.equals("Petrol")) temp=1;
                        else if(fuel.equals("GPL")) temp=2;
                            else temp=3;
                    showMenu.setSelection(temp);

                    }
                else{
                    updateCar.setVisibility(View.GONE);
                    addCar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.addButton){ createCar(); }
        if(i==R.id.updateCar){
            updateCar();SharingValues.setCar(null);
        }
    }


private void updateCar() {
    if (validateForm()) {
        String plate=license.getText().toString();
        String make_temp="",model_temp="";

        mRef.child("Users").child(UID).child("Cars").child(plate).child("Fuel").setValue(fuel_temp);

        if (make.getText() != null) {
            make_temp=make.getText().toString();
        }
        if (model.getText() != null) {
            model_temp=model.getText().toString();
        }
        mRef.child("Users").child(UID).child("Cars").child(plate).child("Make").setValue(make_temp);
        mRef.child("Users").child(UID).child("Cars").child(plate).child("Model").setValue(model_temp);
        mRef.child("Users").child(UID).child("Cars").child(plate).child("Km").setValue(km.getText().toString());

        Intent j = new Intent(this, MainMenuActivity.class);
        startActivity(j);
        finish();
    }

}

    private void createCar(){
        if(validateForm()){
            car.setLICENSE_PLATE(license.getText().toString());
            car.setKM(Double.parseDouble(km.getText().toString()));
            if(make.getText()!=null){
                car.setMAKE(make.getText().toString());
            }
            if(model.getText()!=null){
                car.setMODEL(model.getText().toString());
            }
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Make").setValue(car.getMAKE());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Model").setValue(car.getMODEL());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Km").setValue(car.getKM());
            mRef.child("Users").child(UID).child("Cars").child(car.getLICENSE_PLATE()).child("Fuel").setValue(car.getFUEL_TYPE());
            System.out.println(car.getKM()+" "+car.getLICENSE_PLATE()+" "+car.getMAKE()+" "+car.getMODEL()+" "+car.getFUEL_TYPE());
            Intent j =new Intent(this, MainMenuActivity.class); startActivity(j);
            finish();
        }

    }
    private boolean validateForm() {
        boolean valid = true;

        String plate = license.getText().toString();
        if (TextUtils.isEmpty(plate)) {
            license.setError("Required.");
            valid = false;
        } else {
            license.setError(null);
        }

        String kilometers = km.getText().toString();


        if (TextUtils.isEmpty(kilometers)) {
            km.setError("Required.");
            valid = false;
        }
        else {
            km.setError(null);
        }

        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onBackPressed(){
        Intent intent =new Intent(this,MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
