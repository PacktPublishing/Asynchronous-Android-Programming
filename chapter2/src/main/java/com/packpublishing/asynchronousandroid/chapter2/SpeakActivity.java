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
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packpublishing.chapter2.R;

import java.util.concurrent.TimeUnit;

public class SpeakActivity extends Activity {

    private Handler handler = new SpeakHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speak_layout);
        TextView console = (TextView)findViewById(R.id.title);
        console.setText("Chapter 2 - Speak Handler");

        Button helloBut = (Button)findViewById(R.id.helloBut);
        helloBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(SpeakHandler.SAY_HELLO);
            }
        });


        Button byeBut = (Button)findViewById(R.id.byeBut);
        byeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(SpeakHandler.SAY_BYE);
            }
        });


        Button wordBut = (Button)findViewById(R.id.wordBut);
        wordBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain(handler,
                        SpeakHandler.SAY_WORD, "Welcome!");
                handler.sendMessage(msg);
                Message msg2 = Message.obtain(handler,
                        SpeakHandler.SAY_WORD, "Welcome2!");
                handler.sendMessageAtFrontOfQueue(msg2);
            }
        });

        Button wordDelay = (Button)findViewById(R.id.wordDelay);
        wordDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain(handler,
                        SpeakHandler.SAY_WORD, "Welcome!");
                long time = SystemClock.uptimeMillis() +
                        TimeUnit.SECONDS.toMillis(10);
                int what = SpeakHandler.SAY_BYE;
                int delay = 10000;
                handler.sendMessageAtTime(msg, time);
                Message msg2 = Message.obtain(handler,
                        SpeakHandler.SAY_WORD, "Welcome2!");
                handler.sendMessageDelayed(msg2, delay);
                handler.sendEmptyMessageAtTime(what, time);
                handler.sendEmptyMessageDelayed(what, delay);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        String myWord = "Do it now!";
        handler.removeMessages(SpeakHandler.SAY_BYE);
        handler.removeMessages(SpeakHandler.SAY_WORD, myWord);
    }
}
