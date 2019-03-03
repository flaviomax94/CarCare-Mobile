package com.example.flaviomassimo.carcare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flaviomassimo.carcare.R;

public class HowToActivity extends AppCompatActivity {
    private TextView HowTo,Explanation;
    private ImageView mark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);
        HowTo = (TextView) findViewById(R.id.how);
        Explanation=(TextView) findViewById(R.id.explanation);
        mark=(ImageView) findViewById(R.id.questMark);
    }
    public void onBackPressed(){
        Intent i = new Intent(HowToActivity.this,MainMenuActivity.class);
        startActivity(i);
        finish();
    }
}
