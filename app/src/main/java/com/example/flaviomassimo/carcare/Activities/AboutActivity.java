package com.example.flaviomassimo.carcare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flaviomassimo.carcare.R;

public class AboutActivity extends AppCompatActivity {
    private TextView text;
    private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        text= (TextView) findViewById(R.id.text);
        logo=(ImageView) findViewById(R.id.logo);
        TextView link = (TextView) findViewById(R.id.link);
        String linkText = "Visit the <a href='https://www.linkedin.com/in/flavio-massimo-falesiedi-37b61b163/'>LinkedIn developer</a> web page.";
        link.setText(Html.fromHtml(linkText));
        link.setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(AboutActivity.this,MainMenuActivity.class);
        startActivity(i);
        finish();
    }
}
