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
package com.packpublishing.asynchronousandroid.chapter11;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MobileNetDetectionActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_detection_layout);
        TextView console = (TextView)findViewById(R.id.title);
        console.setText("Chapter 11 - Mobile Net Monitor");
    }

    @Subscribe
    public void onMobileNetDisconnectedEvent(MobileNetDisconnectedEvent event){
        String message = String.format("Mobile connection is not available ");
        appendToConsole(message);

    }

    @Subscribe
    public void onMobileNetConnectedEvent(MobileNetConnectedEvent event){
        String message = String.format("%d Mobile connection is available State - %s ",System.currentTimeMillis(),event.getDetailedState());
        appendToConsole(message);
    }

    void appendToConsole(String message){
        TextView console =  (TextView) findViewById(R.id.console);
        console.setText( console.getText() + message);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

}
