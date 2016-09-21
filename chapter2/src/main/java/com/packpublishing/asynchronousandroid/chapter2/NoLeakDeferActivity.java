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
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.packpublishing.chapter2.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class NoLeakDeferActivity extends Activity {

    static class MyRunnable implements Runnable {

        private WeakReference<View> view;

        public MyRunnable(View view) {
            this.view = new WeakReference<View>(view);
        }
        public void run() {
            TextView v = (TextView)view.get(); // might return null
            if (v != null) {
                String result = processSomething();
                v.setText(result);
            }
        }
        String processSomething(){
            return "Hello World ...";
        }

    }

    private static class MyHandler extends Handler {
        private TextView view;
        public void attach(TextView view) {
            this.view = view;
        }
        public void detach() {
            view = null;
        }
        @Override
        public void handleMessage(Message msg) {
            // handle message
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduling_work_layout);
        Handler handler = new Handler(getMainLooper());
        final TextView myTextView = (TextView) findViewById(R.id.myTv);
        handler.postDelayed(new MyRunnable(myTextView), TimeUnit.SECONDS.toMillis(10));
        //handler.removeCallbacks();
    }

    MyHandler myHandler= new MyHandler();

    @Override
    protected void onResume() {
        super.onResume();
        final TextView myTextView = (TextView) findViewById(R.id.myTv);
        myHandler.attach(myTextView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myHandler.detach();
    }
}
