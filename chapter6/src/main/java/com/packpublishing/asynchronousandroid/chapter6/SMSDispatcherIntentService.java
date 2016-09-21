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

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsManager;
import android.util.Log;

public class SMSDispatcherIntentService extends IntentService {

    private static final String TAG = "SMSDispatcher";

    public static final String TO_KEY = "to";
    public static final String TEXT_KEY = "text";

    public SMSDispatcherIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String to = intent.getStringExtra(TO_KEY);
            String text = intent.getStringExtra(TEXT_KEY);
            Log.i("SMS Dispatcher", "Delivering message to " + text);
            SmsManager sms = SmsManager.getDefault();
            Intent deliveredIntent = new Intent("sms_delivered");
            deliveredIntent.putExtra(SMSDispatcher.TO_KEY, to);
            deliveredIntent.putExtra(SMSDispatcher.TEXT_KEY, text);
            sms.sendTextMessage(to, null, text, null, null);
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
