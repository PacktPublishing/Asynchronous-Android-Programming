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
package com.packpublishing.asynchronousandroid.chapter2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packpublishing.chapter2.R;

public class SchedulingWorkActivity extends Activity {

    View.OnClickListener postUIListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final TextView myTextView = (TextView) findViewById(R.id.myTv);
            Handler handler = new Handler(getMainLooper());

            handler.post(new Runnable() {
                public void run() {
                    String result = processSomething();
                    myTextView.setText(result);
                    logOnConsole("Updating the UI from Runnable after processing...");
                }
            });
        }
    };

    View.OnClickListener postListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable(){
                public void run() {
                    TextView text = (TextView) findViewById(R.id.text);
                    text.setText("Runnable updated the UI thread");
                    logOnConsole("Updating the UI from Runnable");
                }
            });
        }
    };

    View.OnClickListener postAtFrontListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Handler handler = new Handler(getMainLooper());
            handler.postAtFrontOfQueue(new Runnable() {
                public void run() {
                    Log.i("SchedulingWorkActivity","Post at Front");
                    logOnConsole("Posting Runnable at Queue front");
                }
            });
        }
    };

    private void logOnConsole(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView console = (TextView) findViewById(R.id.consoleTv);
                console.setText(console.getText() + "\n" + message);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduling_work_layout);

        Button postUIBut = (Button)findViewById(R.id.postUI);
        Button postBut = (Button)findViewById(R.id.post);
        Button postAtFrontBut = (Button)findViewById(R.id.postAtFront);

        postUIBut.setOnClickListener(postUIListener);
        postBut.setOnClickListener(postListener);
        postAtFrontBut.setOnClickListener(postAtFrontListener);

        TextView console = (TextView)findViewById(R.id.title);
        console.setText("Chapter 2 - Scheduling Work");
    }

    String processSomething(){
        return "Hello World";
    }
}
