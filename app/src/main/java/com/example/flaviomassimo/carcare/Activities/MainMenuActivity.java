package com.example.flaviomassimo.carcare.Activities;

import android.annotation.SuppressLint;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.flaviomassimo.carcare.Activities.Other.BluetoothSocketShare;
import com.example.flaviomassimo.carcare.Fragment.*;
import com.example.flaviomassimo.carcare.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BluetoothAlertFragment.OnFragmentInteractionListener,NotificationFragment.OnFragmentInteractionListener,
        CarFragment.OnFragmentInteractionListener,View.OnClickListener,PathsFragment.OnFragmentInteractionListener {
    BluetoothSocket socket = BluetoothSocketShare.getBluetoothSocket();
    private DatabaseReference mRef;
    private FirebaseAuth fireBaseAuth;
    private EditText first,last;
    private TextView text;
    private Button addName;
    String last_name,first_name;
    String UID;
    FirebaseUser user;
    GoogleSignInAccount account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.NameUser);
        final TextView txtProfileEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.MailUser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text=(TextView) findViewById(R.id.welcome);
        first=(EditText) findViewById(R.id.First);
        last=(EditText) findViewById(R.id.Last);
        findViewById(R.id.buttonNameSurname).setOnClickListener(this);
        addName=(Button) findViewById(R.id.buttonNameSurname);
        setVisibility(false);
        user=FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("User "+user);
        UID=user.getUid().toString();
        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        mRef.child("Users").child(UID).child("Email").setValue(user.getEmail().toString());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(UID).hasChild("Name"))setVisibility(true);
                else{
                    String o=dataSnapshot.child("Users").child(UID).child("Name").getValue().toString();
                    String temp="Hey "+o+"!\n"+text.getText().toString();
                    text.setText(temp);
                    txtProfileName.setText(o);
                    txtProfileEmail.setText(dataSnapshot.child("Users").child(UID).child("Email").getValue().toString());
                }
                    //showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


    }

    private void setVisibility(Boolean bool){
        if(bool){

            addName.setVisibility(View.VISIBLE);
            first.setVisibility(View.VISIBLE);
            last.setVisibility(View.VISIBLE);
        }
        else{addName.setVisibility(View.GONE);
            first.setVisibility(View.GONE);
            last.setVisibility(View.GONE);}


    }
    private void onClickInsertion(){

        if(validateForm()){
                 mRef.child("Users").child(UID).child("Name").setValue(first_name+" "+last_name);
                 SharingValues.setDBUser(user.getEmail().toString(),first_name+" "+last_name);
            Intent j =new Intent(MainMenuActivity.this,MainMenuActivity.class);
            startActivity(j);
            finish();

        }


    }
    private void showData(DataSnapshot dataSnapshot) {

        for(DataSnapshot ds: dataSnapshot.getChildren()) {

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceType")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_garage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CarFragment()).addToBackStack("Garage").commit();
            setVisibility(false);
        }


        else if (id == R.id.nav_home) {
            Intent i = new Intent(MainMenuActivity.this,MainMenuActivity.class);
             startActivity(i);
             finish();
        }


        else if (id == R.id.nav_paths) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PathsFragment()).addToBackStack("Garage").commit();
            setVisibility(false);
           // Intent i = new Intent(MainMenuActivity.this,PreviousPathsActivity.class);
            //startActivity(i);
            //finish();
        }


        else if (id == R.id.nav_graph) {
            if(socket==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BluetoothAlertFragment()).addToBackStack("Bluetooth").commit();
                setVisibility(false);
            }
            else if(!socket.isConnected()){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BluetoothAlertFragment()).addToBackStack("Bluetooth").commit();
                setVisibility(false);
            }
            else if(socket.isConnected()){
                if(BluetoothSocketShare.getBluetoothSocket().getRemoteDevice().getName().contains("OBD")){
                        Intent i = new Intent(MainMenuActivity.this,GraphLineActivity.class);
                        startActivity(i);

                    }
                else{
                        setVisibility(false);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BluetoothAlertFragment()).addToBackStack("Bluetooth").commit();
                    }
                 }
            }


        else if (id == R.id.nav_notifications) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).addToBackStack("Notification").commit();
            setVisibility(false);
        }


        else if (id == R.id.nav_bluetooth) {
            if(socket==null){
                Intent i = new Intent(MainMenuActivity.this,BluetoothActivity.class);
                 startActivity(i);}
            else if(!socket.isConnected()){
                Intent i = new Intent(MainMenuActivity.this,BluetoothActivity.class);
                startActivity(i);}
            else if(socket.isConnected()){
                Intent i = new Intent(MainMenuActivity.this,BluetoothDisconnectActivity.class);
                startActivity(i);}
        }
        else if(id==R.id.nav_logout){
            account = GoogleSignIn.getLastSignedInAccount(this);
            if(SharingValues.getGoogleSignInClient()!=null) {signOut();FirebaseAuth.getInstance().signOut();}
            SharingValues.setLogOut(true);
            Intent i = new Intent(MainMenuActivity.this,ActivitySignIn.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private boolean validateForm() {
        boolean valid = true;

        first_name = first.getText().toString();
        if (TextUtils.isEmpty(first_name)) {
            first.setError("Required.");
            valid = false;
        } else {
            first.setError(null);
        }

        last_name = last.getText().toString();
        if (TextUtils.isEmpty(last_name)) {
            last.setError("Required.");
            valid = false;
        } else {
            last.setError(null);
        }

        return valid;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonNameSurname) {
            onClickInsertion();

        }
    }
    private void signOut() {
        GoogleSignInClient gsc=SharingValues.getGoogleSignInClient();
        gsc.signOut();
        SharingValues.setGoogleSignInClient(null);
    }
}
