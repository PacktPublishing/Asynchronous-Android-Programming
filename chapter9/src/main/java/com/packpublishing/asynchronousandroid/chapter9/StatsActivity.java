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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

public class StatsActivity extends Activity {

    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("mylib");
    }

    public static final int MEM_RSS_REQUEST = 0;
    public static final int MEM_RSS_RESPONSE = 1;


    OnClickListener onRSSReqListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            synchronized (queueLock) {
                requests.add(MEM_RSS_REQUEST);
            }
        }
    };


    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEM_RSS_RESPONSE:
                    TextView tv = (TextView) findViewById(R.id.console);
                    JCPUStat stat = (JCPUStat) msg.obj;
                    tv.setText("Memory Consumption is " + stat.getRSSMemory());
                    stat.dispose();
                    break;
            }
        }
    };

    public Queue<Integer> requests = new LinkedList<Integer>();
    public Object queueLock = new Object();

    int getNextRequest() {
        return requests.size() > 0 ? requests.remove() : -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_stats_layout);
        Button RSSButton = (Button) findViewById(R.id.getRSSBut);
        RSSButton.setOnClickListener(onRSSReqListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startCPUStatThread();
    }


    public native void startCPUStatThread();

    public native void stopCPUStatThread();

    @Override
    protected void onStop() {
        super.onStop();
        stopCPUStatThread();
    }
}
