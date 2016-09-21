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
package com.packpublishing.asynchronousandroid.chapter1;

import android.util.Log;

import java.util.LinkedList;

public class Logger {

    LinkedList<String> queue = new LinkedList<String>();
    private final int MAX_QUEUE_SIZE = 20;

    public void start() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String message = pullMessage();
                    Log.d(Thread.currentThread().getName(), message);
                }
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(task).start();
        }
    }

    synchronized String pullMessage() {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        return queue.pop();
    }

    public synchronized void pushMessage(String logMsg) {
        if (queue.size() < MAX_QUEUE_SIZE) {
            queue.push(logMsg);
            notifyAll();
        }

    }
}
