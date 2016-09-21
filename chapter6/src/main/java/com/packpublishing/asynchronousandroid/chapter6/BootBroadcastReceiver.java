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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.List;


public class BootBroadcastReceiver extends BroadcastReceiver {

    public static class SMSSchedule {
        int type;
        long time;
        String to;
        String msg;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<SMSSchedule> persistedAlarms = getStoredSchedules();
        // Set again the alarms
        // ...
    }

    List<SMSSchedule> getStoredSchedules(){
        return null;
    }
}
