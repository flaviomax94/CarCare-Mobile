package com.example.flaviomassimo.carcare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.flaviomassimo.carcare.R;

public class InterventionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervention);
    }
    @Override
    public void onBackPressed(){
        Intent intent=new Intent(this, MainMenuActivity.class); startActivity(intent);
    }
}
