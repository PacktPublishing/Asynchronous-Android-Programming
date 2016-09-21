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
package com.packpublishing.asynchronousandroid.chapter2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.PrintWriterPrinter;

import java.io.PrintWriter;

public class CancelMessagesActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Handler handler = new SpeakHandler();

        String stringRef1 = new String("Welcome!");
        String stringRef2 = new String("Welcome Home!");
        Message msg1 =  Message.obtain(handler,
                SpeakHandler.SAY_WORD,stringRef1);
        Message msg2 =  Message.obtain(handler,
                SpeakHandler.SAY_WORD, stringRef2);

        // Enqueue the messages to be processed later
        handler.sendMessageDelayed(msg1,600000);
        handler.sendMessageDelayed(msg2, 600000);

        // try to remove the messages
        handler.removeMessages(SpeakHandler.SAY_WORD,
                stringRef1);
        handler.removeMessages(SpeakHandler.SAY_WORD,
                new String("Welcome Home!"));

        PrintWriterPrinter out= new PrintWriterPrinter(new PrintWriter(System.out,true));

        if ( handler.hasMessages(SpeakHandler.SAY_WORD,stringRef2) ) {
            Log.i("RefComparison", "Sorry, we failed to " +
                    "remove the 'Welcome Home' message!!");
        }
        handler.getLooper().dump(out,">>> Looper Queue Dump ");
    }
}
