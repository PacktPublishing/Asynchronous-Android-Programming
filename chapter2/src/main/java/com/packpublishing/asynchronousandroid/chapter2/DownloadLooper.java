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

import android.os.Handler;
import android.os.Looper;

public class DownloadLooper  extends Thread {

    final Handler callbackHandler;
    private Handler myHandler ;
    boolean started;
    Object startMonitor =  new Object();

    public DownloadLooper(Handler callbackHandler){
        super();
        setPriority(MIN_PRIORITY);
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("background");
        Looper.prepare();
        myHandler = buildHandler(Looper.myLooper());
        synchronized (startMonitor){
            started = true;
            startMonitor.notifyAll();
        }
        Looper.loop();
    }

    public Looper getLooper(){
        return Looper.myLooper();
    }

    public void waitforStart(){
        synchronized (startMonitor){
            while (!started){
                try {
                    startMonitor.wait(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Handler getHandler() {
        return myHandler;
    }

    protected Handler buildHandler(Looper looper) {
        return null;
    }
}
