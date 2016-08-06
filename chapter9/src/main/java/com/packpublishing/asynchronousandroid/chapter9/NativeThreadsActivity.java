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
package com.packpublishing.asynchronousandroid.chapter9;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NativeThreadsActivity extends Activity {

    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("mylib");
    }

    public static final int HEALTHCHECK = 0;

    boolean started = false;

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HEALTHCHECK:
                    TextView tv = (TextView) findViewById(R.id.console);
                    tv.setText((String) msg.obj + tv.getText());
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.native_threads_layout);
        Button startButton = (Button) findViewById(R.id.startBut);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    startNativeThreads(myHandler);
                    started = true;
                }
            }
        });

        Button stopButton = (Button) findViewById(R.id.stopBut);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started) {
                    stopNativeThreads();
                    started = false;
                }
            }
        });

    }

    public native void startNativeThreads(Handler handler);

    public native void stopNativeThreads();
}
