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
package com.packpublishing.asynchronousandroid.chapter10;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class NotificationGCMHandler extends GcmListenerService {

    public final String TAG = this.getClass().getSimpleName();
    public static final int NOTIFICATION_ID ="GCMNotification".hashCode();
    public static final String FORUM_TOPIC = "/topics/forum";
    public static final String USERNAME_KEY = "username";
    public static final String TEXT_KEY = "text";
    public static final String MSG_DELIVERY = "asynchronousandroid.forum";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.i(TAG, "Received message from GCM "+data.toString()+" to "+from);

        if (from.equals(FORUM_TOPIC)) {
            Log.i(TAG, "Received message from forum topic");
            Intent intent = new Intent(MSG_DELIVERY);
            intent.putExtra(USERNAME_KEY, data.getString(USERNAME_KEY));
            intent.putExtra(TEXT_KEY, data.getString(TEXT_KEY));
            Log.i(TAG, "Sending broacast message");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        else if  (data.getString("type").startsWith("my_notifications")) {
            createNotification(data.getString("title"),
                    data.getString("body"));
        }
    }
    private void createNotification(String title, String body) {
        android.support.v4.app.NotificationCompat.Builder builder =  new android.support.v4.app.
                NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle()
                        .bigText(body));
        NotificationManager nm = (NotificationManager)
                this.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onMessageSent(String msgId) {
        super.onMessageSent(msgId);
        Log.i(TAG, "Message sent from forum topic " + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        super.onSendError(msgId, error);
        Log.e(TAG, "Message sent failed from forum topic " + msgId);
    }
}
