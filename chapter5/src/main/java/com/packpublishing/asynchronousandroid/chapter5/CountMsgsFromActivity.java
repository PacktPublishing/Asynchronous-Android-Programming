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
package com.packpublishing.asynchronousandroid.chapter5;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CountMsgsFromActivity extends Activity {

    private static final int REQUEST_CODE = 0;
    private static final int MY_PERMISSIONS_READ_SMS = 1;


    protected void onActivityResult(int req, int res, Intent data) {

        if (req == REQUEST_CODE &&
            res == CountMsgsIntentService.RESULT_CODE) {

            // Retrieve the result from result Intent
            int result = data.getIntExtra(CountMsgsIntentService.RESULT,-1);
            // Update UI with the result
            TextView msgCountBut = (TextView)findViewById(R.id.msgCountTv);
            msgCountBut.setText(Integer.toString(result));
        }
        super.onActivityResult(req, res, data);
    }

    void initUI(){

        Button queryButton = (Button)findViewById(R.id.countBut);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText)findViewById(R.id.phoneNumber);
                String phone = et.getText().toString();
                triggerIntentService(phone);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_msgs_activity);

        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_READ_SMS);

        } else{
            initUI();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initUI();
                }
                return;
            }
        }
    }

    private void triggerIntentService(String phone) {
        PendingIntent pending = createPendingResult(
                REQUEST_CODE, new Intent(), 0);
        Intent intent = new Intent(this, CountMsgsIntentService.class);
        intent.putExtra(CountMsgsIntentService.NUMBER_KEY, phone);
        intent.putExtra(
                CountMsgsIntentService.PENDING_RESULT, pending);
        startService(intent);
    }
}
