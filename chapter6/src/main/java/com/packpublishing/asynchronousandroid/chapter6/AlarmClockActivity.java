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
import android.app.AlarmManager.AlarmClockInfo;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class AlarmClockActivity extends Activity {

    private View.OnClickListener setListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 22);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            Intent intent = new Intent("my_alarm");
            intent.putExtra("exactTime", calendar.getTimeInMillis());
            PendingIntent broadcast = PendingIntent.getBroadcast(
                    AlarmClockActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= 23) {

                AlarmClockInfo alarmInfo = new AlarmClockInfo(
                        calendar.getTimeInMillis(),
                        // Create
                        createShowDetailsPI());
                am.setAlarmClock(alarmInfo, broadcast);
            } else {

                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            }
        }
    };

    PendingIntent createShowDetailsPI() {

        Intent showIntent = new Intent(AlarmClockActivity.this, ShowAlarmActivity.class);
        showIntent.putExtra("description", "It's time to take the pill");

        return PendingIntent.getActivity(AlarmClockActivity.this, 0, showIntent,
                                         PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private View.OnClickListener cancelListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent("my_alarm");
            PendingIntent broadcast = PendingIntent.getBroadcast(
                    AlarmClockActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.cancel(broadcast);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_clock_activity);

        Button setBut = (Button) findViewById(R.id.setAlarmClock);
        setBut.setOnClickListener(setListener);

        Button cancelBut = (Button) findViewById(R.id.cancelAlarmClock);
        cancelBut.setOnClickListener(cancelListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
