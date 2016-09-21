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
import android.os.SystemClock;
import android.widget.TextView;

import com.packpublishing.chapter2.R;

import java.util.concurrent.TimeUnit;

public class DeferWorkActivity  extends Activity {

    public class MyRunnable implements Runnable {

        @Override
        public void run() {
            final TextView myTextView = (TextView) findViewById(R.id.myTv);
            String result = processSomething();
            myTextView.setText(result);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduling_work_layout);
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new MyRunnable(), TimeUnit.SECONDS.toMillis(10));
        // Work to be run at a specific time
        handler.postAtTime(new MyRunnable(),
                SystemClock.uptimeMillis() +
                        TimeUnit.SECONDS.toMillis(10));


    }

    String processSomething(){
        return "Hello World ...";
    }
}
