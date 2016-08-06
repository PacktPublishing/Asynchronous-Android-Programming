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
package com.packpublishing.asynchronousandroid.chapter4;

import android.content.Context;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class WhosOnlineLoader extends Loader<List<String>> {

    private final String mChatRoom;
    private List<String> mResult = null;
    private static int cur = 0;

    public WhosOnlineLoader(Context context, String chatRoom) {
        super(context);
        Log.i("WhoIsOnlineLoader", "Loader.new["
                + Integer.toHexString(hashCode()) + "]");
        this.mChatRoom = chatRoom;
    }

    @Override
    protected void onStartLoading() {
        Log.i("WhoIsOnlineLoader","Loader.onStarting["
                                   + Integer.toHexString(hashCode()) + "]");
        if ( mResult != null ) {
            deliverResult(mResult);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        Log.i("WhoIsOnlineLoader", "Loader.onStopping["
                                   + Integer.toHexString(hashCode()) + "]");
    }

    @Override
    public void deliverResult(List<String> data) {
        Log.i("WhoIsOnlineLoader", "Loader.deliverResult["
                                   + Integer.toHexString(hashCode()) + "]");
        mResult = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        Log.i("WhoIsOnlineLoader", "Loader.onReset["
                                   + Integer.toHexString(hashCode()) + "]");
        onStopLoading();
        mResult = null;
    }

    @Override
    protected void onForceLoad() {
        Log.i("WhoIsOnlineLoader", "Loader.onForceload["
                                   + Integer.toHexString(hashCode()) + "]");
        List<String> onlineUsers1 = Arrays.asList(
                                    "John Travolta", "Rudy Van Han", "Maria Startvosky");
        List<String> onlineUsers2 = Arrays.asList(
                                    "Pet Malkovich", "Jos Emptoja", "Andrey Vetod");
        deliverResult(cur % 2 > 0 ? onlineUsers1 : onlineUsers2);
        cur++;
    }
}
