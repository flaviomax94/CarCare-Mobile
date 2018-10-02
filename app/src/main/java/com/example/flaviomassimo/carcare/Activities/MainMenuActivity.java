package com.example.flaviomassimo.carcare.Activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.flaviomassimo.carcare.Activities.Other.BluetoothSocketShare;
import com.example.flaviomassimo.carcare.Fragment.*;
import com.example.flaviomassimo.carcare.R;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BluetoothAlertFragment.OnFragmentInteractionListener,NotificationFragment.OnFragmentInteractionListener,
        CarFragment.OnFragmentInteractionListener{
    BluetoothSocket socket = BluetoothSocketShare.getBluetoothSocket();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CarFragment()).commit();
        }

        else if (id == R.id.nav_paths) {}
        else if (id == R.id.nav_graph) {
            if(socket==null)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BluetoothAlertFragment()).commit();
            else if(!socket.isConnected())
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BluetoothAlertFragment()).commit();
            else if(socket.isConnected()){
                if(BluetoothSocketShare.getBluetoothSocket().getRemoteDevice().getName().contains("OBD")){
            Intent i = new Intent(MainMenuActivity.this,GraphLineActivity.class);
            startActivity(i);}
            else
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BluetoothAlertFragment()).commit();
            }
        }
        else if (id == R.id.nav_notifications) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
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

            SharingValues.setLogOut(true);
            Intent i = new Intent(MainMenuActivity.this,LoginActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
