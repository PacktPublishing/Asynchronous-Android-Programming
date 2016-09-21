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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Sha1BroadcastUnhActivity extends Activity {

    Sha1HashBroadCastUnhService mService;
    boolean mBound = false;

    private DigestReceiver mReceiver = new DigestReceiver();

    private static class DigestReceiver extends BroadcastReceiver {

        private TextView view;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (view != null) {
                String result = intent.getStringExtra(
                        Sha1HashBroadCastUnhService.RESULT);
                intent.putExtra(Sha1HashBroadCastUnhService.HANDLED, true);
                view.setText(result);
            } else {
                Log.i("Sha1HashService", " ignoring - we're detached");
            }
        }

        public void attach(TextView view) {
            this.view = view;
        }
        public void detach() {
            this.view = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sha1_activity_layout);
        Button queryButton = (Button)findViewById(R.id.hashIt);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.text);
                if ( mService != null ) {
                    mService.getSha1Digest(et.getText().toString());
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, Sha1HashBroadCastUnhService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mReceiver.attach((TextView)
                findViewById(R.id.hashResult));
        IntentFilter filter = new IntentFilter(
                Sha1HashBroadCastUnhService.DIGEST_BROADCAST);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(mReceiver);
        mReceiver.detach();
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Sha1HashBroadCastUnhService.LocalBinder binder = (Sha1HashBroadCastUnhService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mService= null;
        }
    };

}
