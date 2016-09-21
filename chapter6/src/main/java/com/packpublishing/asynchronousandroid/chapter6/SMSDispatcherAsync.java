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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.telephony.SmsManager;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SMSDispatcherAsync extends BroadcastReceiver {

    public static final String TO_KEY = "to";
    public static final String TEXT_KEY = "text";

    private static final String DELIVERED_ACTION ="sms_delivered";
    private static final String DISPATCH_ACTION ="sms_dispatch";


    @Override
    public void onReceive(final Context context, final Intent intent) {

        if ( intent.getAction().equals(DELIVERED_ACTION) ){
            final PendingResult result = goAsync();
            AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        // ... do some work here, for up to 10 seconds
                        processDispatch(context, intent);
                    } finally {
                        result.setResultCode(Activity.RESULT_OK);
                        result.finish();
                    }
                    return null;
                }
            });

        } else if (intent.getAction().equals(DISPATCH_ACTION)){
            processDelivered(context,intent);
        }
    }

    void processDispatch(Context context, Intent intent){
        String to = intent.getStringExtra(TO_KEY);
        String text = intent.getStringExtra(TEXT_KEY);
        Log.i("SMS Dispatcher", "Delivering message to " + text);
        SmsManager sms = SmsManager.getDefault();
        Intent deliveredIntent = new Intent("sms_delivered");
        deliveredIntent.putExtra(SMSDispatcher.TO_KEY, to);
        deliveredIntent.putExtra(SMSDispatcher.TEXT_KEY, text);
        sms.sendTextMessage(to, null, text, PendingIntent.getBroadcast(
                context, DISPATCH_ACTION.hashCode(), deliveredIntent, 0),null);
    }

    void processDelivered(Context context, Intent intent){

        String to = intent.getStringExtra(TO_KEY);
        String text = intent.getStringExtra(TEXT_KEY);
        String title = null;

        switch(getResultCode()) {
            case Activity.RESULT_OK:
                title = "Message Delivered to " + to;
                break;
            default:
                title ="Message Delivery failed to " + to;
                break;
        }

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(context)
                                                .setContentTitle(title)
                                                .setContentText(text)
                                                .setSmallIcon(android.R.drawable.stat_notify_chat)
                                                .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText(text));

        NotificationManager nm = (NotificationManager)
                context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        nm.notify(intent.hashCode(), builder.build());

    }
}
