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
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AlarmActivity extends Activity {

    public static final int ALARM_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity_layout);
        Button fiveHourElapedBut = (Button) findViewById(R.id.fiveHourElapedBut);
        fiveHourElapedBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFiveHourElapsed();
            }
        });
        Button fiveMinRTCBut = (Button) findViewById(R.id.fiveMinRTCBut);
        fiveMinRTCBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFiveMinRTC();
            }
        });
        Button tomorrowAt10But = (Button) findViewById(R.id.tomorrowAt10But);
        tomorrowAt10But.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTomorrowAt10();
            }
        });
        Button fiveMinExactBut = (Button) findViewById(R.id.fiveMinExactBut);
        fiveMinExactBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFiveMinExact();
            }
        });
        Button fiveMinAllowIdle = (Button) findViewById(R.id.fiveMinAllowIdle);
        fiveMinAllowIdle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFiveMinAllowIdle();
            }
        });
        Button fiveMinWindow = (Button) findViewById(R.id.fiveMinWindow);
        fiveMinWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFiveMinWindow();
            }
        });

        Button oneHourAlarmBut = (Button) findViewById(R.id.oneHourAlarm);
        oneHourAlarmBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set1HourAlarm();
            }
        });
        Button cancelBut = (Button) findViewById(R.id.cancelAlarm);
        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel1HourAlarm();
            }
        });
    }

    void createFiveHourElapsed() {
        Intent intent = new Intent("my_alarm");
        long delay = TimeUnit.HOURS.toMillis(5L);
        long time = System.currentTimeMillis() + delay - SystemClock.elapsedRealtime();
        intent.putExtra("exactTime",time );
        PendingIntent pending = PendingIntent.getBroadcast(
                this, ALARM_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME, delay, pending);
    }

    void createFiveMinRTC() {
        Intent intent = new Intent("my_alarm");
        long delay = TimeUnit.MINUTES.toMillis(5L);
        long time = System.currentTimeMillis() + delay;
        intent.putExtra("exactTime", time);
        PendingIntent pending = PendingIntent.getBroadcast(
                this, ALARM_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, time, pending);
    }

    void createTomorrowAt10() {
        Intent intent = new Intent("my_alarm");
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 21) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        intent.putExtra("exactTime", calendar.getTimeInMillis());
        PendingIntent pending = PendingIntent.getBroadcast(
                this, ALARM_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, calendar.getTimeInMillis(), pending);
    }


    void createFiveMinExact() {
        Intent intent = new Intent("my_alarm");
        long delay = TimeUnit.MINUTES.toMillis(1L);
        long time = System.currentTimeMillis() + delay;
        intent.putExtra("exactTime", time);
        PendingIntent pending = PendingIntent.getBroadcast(
                this, ALARM_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            // Wakes up the device in Doze Mode
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
        } else if (Build.VERSION.SDK_INT >= 19) {
            // Wakes up the device in Idle Mode
            am.setExact(AlarmManager.RTC_WAKEUP, time, pending);
        } else {
            // Old APIs
            am.set(AlarmManager.RTC_WAKEUP, time, pending);
        }
    }

    void createFiveMinAllowIdle() {

        if (Build.VERSION.SDK_INT >= 23) {

            Intent intent = new Intent("my_alarm");
            long delay = TimeUnit.MINUTES.toMillis(5L);
            long time = System.currentTimeMillis() + delay;
            intent.putExtra("exactTime", time);
            PendingIntent pending = PendingIntent.getBroadcast(this, ALARM_CODE, intent,
                                                               PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
        }
    }

    void createFiveMinWindow() {

        if (Build.VERSION.SDK_INT >= 19) {
            Intent intent = new Intent("my_alarm");
            long delay = TimeUnit.MINUTES.toMillis(5L);
            long window = TimeUnit.MINUTES.toMillis(3L);
            long time = System.currentTimeMillis() + delay;
            intent.putExtra("exactTime", time);
            PendingIntent pending = PendingIntent.getBroadcast(this, ALARM_CODE, intent,
                                                               PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setWindow(AlarmManager.RTC_WAKEUP, time, window, pending);
        }

    }

    void set1HourAlarm(){
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // extras don't affect matching
        am.set(AlarmManager.RTC, in1HourTime(), createPendingIntent(in1HourTime()));
    }

    void cancel1HourAlarm(){
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Remove any alarms with a matching
        am.cancel(createPendingIntent(in1HourTime()));
    }

    PendingIntent createPendingIntent(long time){
        Intent intent = new Intent("my_alarm");
        PendingIntent pending = PendingIntent.getBroadcast(
                this, ALARM_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        intent.putExtra("exactTime", time);
        return pending;
    }

    long in1HourTime(){
        long delay = TimeUnit.MINUTES.toMillis(5L);
        long time = System.currentTimeMillis() + delay;
        return time;
    }

}
