/*
 *  This code is part of "Asynchronous Android Programming".
 *
 *  Copyright (C) 2016 Helder Vasconcelos (heldervasc@bearstouch.com)
 *  Copyright (C) 2016 Packt Publishing
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *  
 */
package com.packpublishing.asynchronousandroid.chapter11;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LocationActivity extends Activity {

    public final String TAG = this.getClass().getSimpleName();

    private static final int MY_PERMISSIONS_GPS = 1;

    boolean isPermistionGranted = false;

    LocationManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_layout);
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Here, thisActivity is the current activity
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
             ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_GPS);

        }
        Button newSubs = (Button)findViewById(R.id.launch);
        newSubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Runnable() {
                    @Subscribe(sticky = true)
                    public void onLocationEvent(LocationEvent event) {
                        Log.i(TAG,String.format("Last known Location is Lat[%f] Long[%f] ",event.latitude,event.longitude));
                        TextView locationTv = (TextView)findViewById(R.id.location);
                        locationTv.setText(String.format("Lat[%f] Long[%f]", event.latitude, event.longitude));
                    }
                    @Override
                    public void run() {
                        EventBus.getDefault().register(this);
                        EventBus.getDefault().unregister(this);
                    }
                }.run();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_GPS: {
                // If request is cancelled, the result arrays are empty.
                if ( grantResults.length > 0 &&
                     grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    isPermistionGranted = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //EventBus.getDefault().register(this);
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"Permission not granted and finishing...");
            finish();
        }
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if ( location != null ){
            EventBus.getDefault().postSticky(
                    new LocationEvent(location.getLatitude(),
                            location.getLongitude()));
        }
        // Request a location update only if  device changed
        // must move before another update will be sent, in meters.
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600, 100, listener);
    }

    @Override
    public void onPause() {
        super.onPause();
       // EventBus.getDefault().unregister(this);
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
             ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"Permission not granted and finishing...");
            finish();
        }
        manager.removeUpdates(listener);
    }

    //Handle location callback events
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            EventBus.getDefault().postSticky(
                    new LocationEvent(location.getLatitude(),
                                      location.getLongitude()));
        }
        @Override
        public void onProviderDisabled(String provider) { }
        @Override
        public void onProviderEnabled(String provider) { }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    };
}
