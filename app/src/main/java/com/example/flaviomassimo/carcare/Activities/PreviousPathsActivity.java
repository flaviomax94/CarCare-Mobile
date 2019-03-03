package com.example.flaviomassimo.carcare.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.flaviomassimo.carcare.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PreviousPathsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener {


    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    LocationManager mLocationManager;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    ArrayList<String> listCoordinates=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_paths);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mGoogleMap.setOnMyLocationButtonClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();

            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }


        File path=SharingValues.getPath();
        if(path != null)
            setPath(path);
        else{
            Toast.makeText(this,"File not found try another file",Toast.LENGTH_LONG);
            Intent i=new Intent(PreviousPathsActivity.this,MainMenuActivity.class);
            startActivity(i);
            finish();

        }


    }


    public void setPath(File path){
        listCoordinates=readFile(path);
        List<LatLng> routeArray = new ArrayList<LatLng>();
        for(String elem:listCoordinates){
            String arr[]=elem.split(" ");
            LatLng pos=new LatLng(Double.parseDouble(arr[0]),Double.parseDouble(arr[1]));
            System.out.println(pos.toString());
            routeArray.add(pos);
        }
        if (routeArray == null) {
            Log.e("Draw Line", "got null as parameters");
            return;
        }

        Polyline line = mGoogleMap.addPolyline(new PolylineOptions().width(3).color(Color.RED));
        line.setPoints(routeArray);
        if(routeArray!=null){
            LatLng first=routeArray.get(0);
            mGoogleMap.addMarker(new MarkerOptions().position(first).title("Start"));
            LatLng last=routeArray.get(routeArray.size()-1);
            mGoogleMap.addMarker(new MarkerOptions().position(last).title("Finish"));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first,14));
        }
        /*int i=0;
        for(LatLng l:routeArray){
            i++;
            mGoogleMap.addMarker(new MarkerOptions().position(l).title("step "+i));
            if(i==1){
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l,14));
            }
        }
        System.out.println("SETTED POINTS------------------");*/
    }

    public ArrayList<String> readFile(File f) {
        ArrayList<String> ris = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line=br.readLine();

            while (line != null) {
               String[] pieces= line.split(" ");
              if(pieces.length>1){
                  if (!pieces[3].equals("Unknown")){
                    String[] pos=pieces[3].split(",");
                    String latlong=pos[0]+" "+pos[1];

                    if (!ris.contains(latlong)){
                        ris.add(latlong);
                   }

               }
              }
              line=br.readLine();
            }
            br.close();
        }
        catch (IOException e) {

        }

        return ris;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    };

    public static final int PERMISSIONS = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(PreviousPathsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS );
            }
        }
    }
    @Override
    public void onBackPressed(){
        //TODO eliminare file in memoria
        Intent intent = new Intent(this,MainMenuActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onMyLocationButtonClick() {
        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        if(mGoogleMap.isMyLocationEnabled()){
            System.out.println(mLocationManager);


            // Getting Current Location
            @SuppressLint("MissingPermission") Location location = getLastKnownLocation();
            if (location != null) {
                // Getting latitude of the current location
                double latitude = location.getLatitude();

                // Getting longitude of the current location
                double longitude = location.getLongitude();

                // Creating a LatLng object for the current location
                LatLng myPosition = new LatLng(latitude, longitude);

                mGoogleMap.addMarker(new MarkerOptions().position(myPosition).title("Hi! You are here."));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,14));

            }
        }
        return false;
    }
    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        Intent intent = new Intent(PreviousPathsActivity.this, PreviousPathsActivity.class);
                        startActivity(intent);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
