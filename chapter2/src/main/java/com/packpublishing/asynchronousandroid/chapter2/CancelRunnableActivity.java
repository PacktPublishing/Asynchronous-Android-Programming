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
import java.util.concurrent.TimeUnit;

public class CancelRunnableActivity  extends Activity {


    final Runnable runnable = new Runnable(){
        public void run() {
            // ... do some work
            Log.i("CancelRunnableActivity","Running the job");
            logOnConsole("Running the job");
        }
    };

    final Handler handler = new Handler();

    View.OnClickListener postListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logOnConsole("Posting a new job");
            handler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(10));
        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button cancel = (Button) findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    logOnConsole("Cancelling the job");
                    handler.removeCallbacks(runnable);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_work_layout);

        Button postBut = (Button)findViewById(R.id.post);
        Button cancelBut = (Button)findViewById(R.id.cancel);
        postBut.setOnClickListener(postListener);
        cancelBut.setOnClickListener(cancelListener);
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
