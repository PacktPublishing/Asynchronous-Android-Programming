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
package com.packpublishing.asynchronousandroid.chapter5;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CountMsgsIntentService extends IntentService {

    public static final String  PENDING_RESULT = "pending_result";
    public static final String  RESULT = "result";
    public static final int     RESULT_CODE = "nth_prime".hashCode();
    public static final String  NUMBER_KEY= "number";

    public CountMsgsIntentService(){
        super("backup");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String phoneNumber = intent.getStringExtra(NUMBER_KEY);
        Cursor cursor = countMsgsFrom(phoneNumber);
        int numberOfMsgs = cursor.getCount();

        Log.i("CountMsgsIntentService", "Number of messages found : " + numberOfMsgs);

        notifyUser(phoneNumber,numberOfMsgs);

        try {
            Intent result = new Intent();
            result.putExtra(RESULT, numberOfMsgs);
            PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT);
            reply.send(this, RESULT_CODE, result);
        } catch (PendingIntent.CanceledException exc) {
            Log.e("CountMsgsIntentService", "reply cancelled", exc);
        }
    }

    private void notifyUser(String phoneNumber,int msgsCount) {

        String msg = String.format(
                "Found %d from the phone number %s", msgsCount,phoneNumber);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                                .setSmallIcon(R.drawable.ic_sms_counter_not)
                                                .setContentTitle("Inbox Counter")
                                                .setContentText(msg);

        // Gets an instance of the NotificationManager service
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an unique ID for this notification
        nm.notify(phoneNumber.hashCode(), builder.build());
    }

    private Cursor countMsgsFrom(String phoneNumber) {

        String[] select = {
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
        };
        String whereClause =
                Telephony.Sms.ADDRESS +" = '"+phoneNumber+"'";
        Uri quri = Uri.parse("content://sms/inbox");
        return getContentResolver().query(quri,
                select, // Columns to select
                whereClause, // Clause to filter results
                null, // Arguments for the whereClause
                null);
    }
}
