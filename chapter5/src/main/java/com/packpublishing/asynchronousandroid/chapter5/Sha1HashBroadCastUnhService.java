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

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.security.MessageDigest;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Sha1HashBroadCastUnhService extends Service {


    public static final String DIGEST_BROADCAST = "asynchronousandroid.chapter5.DIGEST_BROADCAST";
    public static final String RESULT = "digest";
    public static final String HANDLED = "intent_handled";

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    //
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final int MAX_QUEUE_SIZE = 16;

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r,"Sha1HashBroadcastService #" + mCount.getAndIncrement());
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    };

    private ThreadPoolExecutor mExecutor;

    public class LocalBinder extends Binder {
        Sha1HashBroadCastUnhService getService() {
            // Return this instance of LocalService so clients can call public methods
            return Sha1HashBroadCastUnhService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void broadcastResult(final String text,final String digest) {

        Looper mainLooper = Looper.getMainLooper();
        Handler handler =  new Handler(mainLooper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(DIGEST_BROADCAST);
                intent.putExtra(RESULT, digest);
                LocalBroadcastManager.getInstance(Sha1HashBroadCastUnhService.this).
                        sendBroadcastSync(intent);
                boolean handled = intent.getBooleanExtra(HANDLED, false);
                if(!handled){
                    notifyUser(text, digest);
                }
            }
        });

    }

    private void notifyUser(final String text,final String digest) {
        String msg = String.format(
                "The Hash for %s is %s", text,digest);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_sms_counter_not)
                .setContentTitle("Hashing Result")
                .setContentText(msg);
        // Gets an instance of the NotificationManager service
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an unique ID for this notification
        nm.notify(text.hashCode(), builder.build());
    }

    void getSha1Digest(final String text) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i("Sha1HashService", "Hashing text " + text + " on Thread " +
                        Thread.currentThread().getName());
                try {
                    // Execute the Long Running Computation
                    final String digest = SHA1(text);
                    Log.i("Sha1HashService", "Hash result for "+text+" is "+digest);
                    // Execute the Runnable on UI Thread
                    broadcastResult(text,digest);
                } catch (Exception e){
                    Log.e("Sha1HashService", "Hash failed", e);
                }
            }
        };
        // Submit the Runnable on the ThreadPool
        mExecutor.execute(runnable);
    }


    @Override
    public void onCreate() {

        Log.i("Sha1HashService", "Starting Hashing Service");
        super.onCreate();
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 5,
                TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        mExecutor.prestartAllCoreThreads();

    }

    @Override
    public void onDestroy() {
        Log.i("Sha1HashService", "Stopping Hashing Service");
        super.onDestroy();
        mExecutor.shutdown();
    }

    public  String SHA1(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
    private  String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) :
                                                                (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
