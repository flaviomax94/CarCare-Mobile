package com.example.flaviomassimo.carcare.Activities;

import android.app.Application;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPSListener extends Application implements LocationListener {
    private static String position="";



    public String getLocation(){
        return position;
    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude=location.getLatitude();
        double longitude=location.getLongitude();
        position= latitude+","+longitude;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
