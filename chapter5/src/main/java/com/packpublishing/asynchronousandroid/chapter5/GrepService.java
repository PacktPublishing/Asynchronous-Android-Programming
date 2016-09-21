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

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class GrepService extends Service {

    static public final String  TEXT_KEY = "text";
    static public final String  GREP_STRING = "grep";
    static public final String  RESULT_STRING = "result";
    static public final int     FIND_STRING = 1;
    static public final int     GREP_RESULT = 2;

    private  class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case FIND_STRING:

                    Bundle data =(Bundle) msg.obj;
                    String inputString = data.getString(TEXT_KEY);
                    String grepString = data.getString(GREP_STRING);
                    Log.i("GrepService","Grepping: "+inputString+" "+"Finding:"+grepString);

                    String result = grepString(inputString,grepString);
                    Bundle bundle = new Bundle();
                    bundle.putString(RESULT_STRING,result);
                    Log.i("GrepService","Result: "+result);

                    try {
                        Messenger client = msg.replyTo;
                        client.send(Message.obtain(null, GREP_RESULT, 0, 0, bundle));
                    } catch (RemoteException e) {
                        Log.e("GrepService","Failed to send grep result",e);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }


    public String grepString(String text,String reg){
        String[] lines = text.split(" ");
        StringBuffer buffer= new StringBuffer();
        for(String line: lines){
            if(line.matches(".*"+reg+".*")){
                if ( buffer.length() > 0 ){
                    buffer.append(" ");
                }
                buffer.append(line);
            }
        }
        return buffer.toString();
    }
}
