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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Random;


public class MessagingActivity extends Activity {

    public final String TAG = this.getClass().getSimpleName();

    public static final String SENT_TOKEN_TO_SERVER = "sent2Server";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static abstract class AsyncJob extends AsyncTask<Void,Void,Result<Void> > {

        @Override
        protected Result<Void> doInBackground(Void ...args) {
            Result<Void> result = new Result<Void>();
            try {
                runOnBackground();
            } catch (Throwable e) {
                result.error = e;
            }
            return result;
        }
        @Override
        protected void onPostExecute(Result<Void> result) {
            if ( result.error!=null ){
                onFailure(result.error);
            } else {
                onSuccess();
            }
        }
        abstract void runOnBackground() throws Exception;
        abstract void onFailure(Throwable e);
        abstract void onSuccess();
    }

    View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            TextView msgText = (TextView) findViewById(R.id.msg);
            final String msgToSend = msgText.getText().toString();
            msgText.setText("");

            new AsyncJob(){
                @Override
                void runOnBackground() throws Exception{
                    Bundle data = new Bundle();
                    data.putString(NotificationGCMHandler.USERNAME_KEY, "Helder");
                    data.putString(NotificationGCMHandler.TEXT_KEY,msgToSend);
                    data.putString("topic",NotificationGCMHandler.FORUM_TOPIC);
                    String id = Integer.toString(new Random().nextInt());
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MessagingActivity.this);
                    gcm.send(getString(R.string.gcm_SenderId) + "@gcm.googleapis.com", id, data);
                }
                @Override
                void onFailure(Throwable e) {
                    Log.e(TAG,"Failed to send upstream message to " + NotificationGCMHandler.FORUM_TOPIC,e);
                }
                @Override
                void onSuccess() {
                    Log.i(TAG,"Upstream message sent to "+NotificationGCMHandler.FORUM_TOPIC+" with success");
                }
            }.execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        // If Google Play Services is up to date, we'll want to register GCM. If it is not, we'll
        // skip the registration and this device will not receive any downstream messages from
        // our GCM Playground server.
        if (checkPlayServices()) {

            Log.i(TAG, "Registering to GCM");
            // Because this is the initial creation of the app, we'll want to be certain we have
            // a token. If we do not, then we will start the IntentService that will register this
            // application with GCM.
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);

            if (!sentToken) {
               //...Error Handling
            }
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);

            Button button = (Button)findViewById(R.id.sendButton);
            button.setOnClickListener(sendListener);
        }
    }


    @Override
    protected void onResume() {
            super.onResume();
            Log.i(TAG, "Registering Chat Activity to Local Intent " + NotificationGCMHandler.MSG_DELIVERY);
            IntentFilter filter= new IntentFilter(NotificationGCMHandler.MSG_DELIVERY);
            LocalBroadcastManager.getInstance(this).registerReceiver(onMessageReceiver, filter);
        }

    @Override
   protected void onPause() {
        super.onPause();
                Log.i(TAG, "Unregistering from Local Intent " + NotificationGCMHandler.MSG_DELIVERY);
                LocalBroadcastManager.getInstance(this).unregisterReceiver(onMessageReceiver);

    }

    BroadcastReceiver onMessageReceiver = new  BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            TextView chatText = (TextView)findViewById(R.id.chatWindow);
            Log.i(TAG, "Received message from forum topic");
            String username= intent.getStringExtra(NotificationGCMHandler.USERNAME_KEY);
            String bodyText = intent.getStringExtra(NotificationGCMHandler.TEXT_KEY);
            String line = String.format("%s : %s%n", username,bodyText);
            // Prepend the message
            chatText.setText( line + chatText.getText().toString());
        }
    };


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int rc = apiAvailability.isGooglePlayServicesAvailable(this);
        if (rc != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(rc)) {
                apiAvailability.getErrorDialog(this, rc,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
