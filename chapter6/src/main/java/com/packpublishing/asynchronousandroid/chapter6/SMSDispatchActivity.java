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
package com.packpublishing.asynchronousandroid.chapter6;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.concurrent.TimeUnit;

public class SMSDispatchActivity extends Activity {

    private static final int MY_PERMISSIONS_SEND_SMS = 1;

    void launchService(String phoneMumber,String text,long time){

        Intent intent = new Intent(this,SMSDispatcherIntentService.class);
        intent.putExtra(SMSDispatcherIntentService.TO_KEY, phoneMumber);
        intent.putExtra(SMSDispatcherIntentService.TEXT_KEY, text);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent service = PendingIntent.getService(
                getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, time, service);
    }

    private OnClickListener mSubmit = new OnClickListener() {

        @Override
        public void onClick(View v) {

            EditText hoursEt = (EditText) findViewById(R.id.hoursTv);
            int hours =  Integer.parseInt(hoursEt.getText().toString());
            EditText toEt = (EditText) findViewById(R.id.phoneNumberTv);
            String  phoneMumber =  toEt.getText().toString();
            EditText textEt = (EditText) findViewById(R.id.messageTv);
            String  text =  textEt.getText().toString();
            Intent intent = new Intent("sms_dispatch");

            long delay = TimeUnit.HOURS.toMillis(hours);
            long time = System.currentTimeMillis() + delay;
            intent.putExtra(SMSDispatcher.TO_KEY, phoneMumber);
            intent.putExtra(SMSDispatcher.TEXT_KEY, text);

            PendingIntent broadcast = PendingIntent.getBroadcast(
                                        getBaseContext(),
                                        0,
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

             AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            Log.i("SMSDispatchActivity","Scheduling delivery to "+time+" now="+System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= 23) {
                // Wakes up the device in Doze Mode
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, broadcast);
            } else if (Build.VERSION.SDK_INT >= 19) {
                // Wakes up the device in Idle Mode
                am.setExact(AlarmManager.RTC_WAKEUP, time, broadcast);
            } else {
                // Old APIs
                am.set(AlarmManager.RTC_WAKEUP, time, broadcast);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_dispatch_layout);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(SMSDispatchActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_SEND_SMS);
                // MY_PERMISSIONS_SEND_SMS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else {
           initUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initUI();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void initUI(){

        LinearLayout permLL =(LinearLayout)findViewById(R.id.ownPermissionsLL);
        LinearLayout noPermLL =(LinearLayout)findViewById(R.id.noPermissionsLL);
        permLL.setVisibility(View.VISIBLE);
        noPermLL.setVisibility(View.GONE);
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(mSubmit);
    }
}
