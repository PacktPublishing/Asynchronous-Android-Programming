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
import android.widget.TextView;

import com.packpublishing.chapter2.R;

public class SearchSynonymActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduling_work_layout);
        // Handler bound to the main Thread
        final Handler handler = new Handler();
        //runOnUiThread();
        // Creates an assync line of execution
        Thread thread = new Thread() {
            public void run() {
                final String result = searchSynomym("build");
                handler.post(new Runnable() {
                    public void run() {
                        TextView text = (TextView)
                                findViewById(R.id.text);
                        text.setText(result);
                    }
                });
            }
        };
        // Start the background thread with a lower priority
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    String searchSynomym(String toLook){
        return "fake ";
    }
}
