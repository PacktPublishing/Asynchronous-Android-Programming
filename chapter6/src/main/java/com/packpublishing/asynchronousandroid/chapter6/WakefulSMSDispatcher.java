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

public class WakefulSMSDispatcher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Pass right over to SMSDispatcherIntentService class, the wakeful receiver is
        // just needed in case the schedule is triggered while the device
        // is asleep otherwise the service may not have time to trigger the
        // alarm.
        intent.setClass(context, SMSDispatcherIntentService.class);
        WakefulBroadcastReceiver.startWakefulService(context, intent);
    }
}
