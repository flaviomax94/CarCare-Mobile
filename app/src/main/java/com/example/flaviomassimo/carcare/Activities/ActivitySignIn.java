package com.example.flaviomassimo.carcare.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.example.flaviomassimo.carcare.R;
public class ActivitySignIn extends AppCompatActivity implements View.OnClickListener{
    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mButton=(Button) findViewById(R.id.login_register);
        findViewById(R.id.login_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_register){
            Intent intent = new Intent(ActivitySignIn.this,LoginActivity.class);
            startActivity(intent);
            finish();

        }
    }
}
