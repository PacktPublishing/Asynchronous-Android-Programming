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
package com.packpublishing.asynchronousandroid.chapter6;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

public class RepeatingAlarmActivity  extends Activity{


    private OnClickListener refresh15mListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent("my_alarm");

            PendingIntent broadcast = PendingIntent.getBroadcast(
                    RepeatingAlarmActivity.this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            long start = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            Log.i("RepeatingALarm","Setting the 15 min alarm and starting at "+start);
            am.setRepeating(
                    AlarmManager.RTC_WAKEUP, start,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, broadcast);

        }
    };

    private OnClickListener refresh30mListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent("my_alarm");
            PendingIntent broadcast = PendingIntent.getBroadcast(
                    RepeatingAlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long start = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L);
            AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);
            Log.i("RepeatingALarm","Setting the 15 min alarm and starting at "+start);
            intent.putExtra("my_int",3);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP, start,
                    AlarmManager.INTERVAL_HALF_HOUR, broadcast);

        }
    };

    private OnClickListener cancelListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent("my_alarm");
            PendingIntent broadcast = PendingIntent.getBroadcast(
                    RepeatingAlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);
            Log.i("RepeatingALarm","Canceling the Repeating Alarm");
            am.cancel(broadcast);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeating_alarm_layout);

        Button refreshEvery15m = (Button) findViewById(R.id.refreshEvery15m);
        refreshEvery15m.setOnClickListener(refresh15mListener);

        Button refreshEvery30m = (Button) findViewById(R.id.refreshEvery30m);
        refreshEvery30m.setOnClickListener(refresh30mListener);

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(cancelListener);

    }
}
