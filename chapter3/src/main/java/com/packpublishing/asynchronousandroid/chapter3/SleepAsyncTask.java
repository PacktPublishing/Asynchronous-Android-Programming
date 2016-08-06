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
package com.packpublishing.asynchronousandroid.chapter3;

import android.os.AsyncTask;
import android.util.Log;

public class SleepAsyncTask  extends AsyncTask<Integer, Void, Void> {

    final int identifier;

    SleepAsyncTask(final int identifier){
        this.identifier = identifier;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        try {
            Log.i("SleepAsyncTask","Job task#" + identifier + " has started");
            Thread.sleep(params[0]);
            Log.i("SleepAsyncTask","Job task#" + identifier +" has finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
