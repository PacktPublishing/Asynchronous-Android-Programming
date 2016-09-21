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

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SaveMyLocationActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.save_my_location_layout);
        TextView console = (TextView)findViewById(R.id.title);
        console.setText("Chapter 5 - Started Service");
        Button startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartServiceClick();
            }
        });
        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopServiceClick();
            }
        });
    }

    void onStartServiceClick(){
        Intent intent = new Intent(this,SaveMyLocationService.class);
        String location = getLocation();
        logOnConsole("Saving location - "+location);
        intent.putExtra(SaveMyLocationService.LOCATION_KEY,location);
        startService(intent);
    }

    void onStopServiceClick(){
        Intent intent =new Intent(SaveMyLocationActivity.this, SaveMyLocationService.class);
        logOnConsole("Stopping Service");
        stopService(intent);
    }

    static int count = 0;
    String getLocation(){
        String[] locations = {"St. Oleary Street","Henry Street","St. Patrich Church"};
        return locations[count++ % locations.length];
    }

    private void logOnConsole(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView console = (TextView) findViewById(R.id.consoleTv);
                console.setText(console.getText() + "\n" + message);
            }
        });
    }

}
