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
package com.packpublishing.asynchronousandroid.chapter1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorActivity extends Activity {

    public class MyRunnable implements Runnable {
        public void run() {
            Log.d("Generic", "Running From Thread " + Thread.currentThread().getId());
            logOnConsole("Running From Thread " + Thread.currentThread().getId());
            // Your Long Running Computation Task
            try {
                // Sleeps for 200 ms
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startWorking(){
        Executor executor = Executors.newFixedThreadPool(5);
        for ( int i=0; i < 20; i++ ) {
            executor.execute(new MyRunnable());
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.executor_activity_layout);
        TextView console = (TextView)findViewById(R.id.title);
        console.setText("Chapter 1 - Executor");
        startWorking();
    }
}
