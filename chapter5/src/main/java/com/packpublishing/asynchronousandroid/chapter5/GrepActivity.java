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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class GrepActivity extends Activity {

    /**
     * Messenger for communicating with service.
     */
    Messenger mServerMessenger = null;
    /**
     * Activity Messenger to receive replies
     */
    final Messenger mClientMessenger = new Messenger(new IncomingHandler());
    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mBound;

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GrepService.GREP_RESULT:
                    EditText resultEt = (EditText) findViewById(R.id.resultEt);
                    Bundle bundle = (Bundle) msg.obj;
                    String result = bundle.getString(GrepService.RESULT_STRING);
                    resultEt.setText(result);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grep_activity_layout);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText textEt = (EditText) findViewById(R.id.textEt);
                EditText filter = (EditText) findViewById(R.id.filter);
                String text = textEt.getText().toString();
                String reg = filter.getText().toString();

                if ( mServerMessenger != null ) {

                    Message msg = Message.obtain();
                    msg.what = GrepService.FIND_STRING;
                    Bundle bundle = new Bundle();
                    bundle.putString(GrepService.GREP_STRING, reg);
                    bundle.putString(GrepService.TEXT_KEY, text);

                    msg.obj = (bundle);
                    msg.replyTo = mClientMessenger;

                    try {
                        mServerMessenger.send(msg);
                    } catch (RemoteException e) {
                        Log.e("GrepService", "Failed to send grep request", e);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, GrepService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mServerMessenger = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mServerMessenger = null;
            mBound = false;
        }
    };
}
