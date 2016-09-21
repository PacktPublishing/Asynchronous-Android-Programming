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
import android.widget.TextView;

import com.packpublishing.chapter2.R;
import java.lang.ref.WeakReference;

public class UIRunnable implements Runnable {

    WeakReference<Activity> activity;

    UIRunnable(Activity activity){
        this.activity= new  WeakReference<Activity>(activity);
    }

    @Override
    public void run() {
        if (activity.get() != null ){
            TextView text = (TextView) activity.get().findViewById(R.id.text);
            text.setText("updated on the UI thread");
        }
    }
};
