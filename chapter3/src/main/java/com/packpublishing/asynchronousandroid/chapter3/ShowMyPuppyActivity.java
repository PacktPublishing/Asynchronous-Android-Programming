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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;


public class ShowMyPuppyActivity extends Activity {

    AsyncTask photoAsyncTask = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel Pending Tasks
        if(photoAsyncTask !=null && photoAsyncTask.getStatus()!=AsyncTask.Status.FINISHED){
            photoAsyncTask.cancel(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_activity);
        Button showBut = (Button) findViewById(R.id.showImageBut);
        showBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                // My Puppie Image URL
                URL url = new URL("http://img.allw.mn" +
                        "/content/www/2009/03/april1.jpg");
                ImageView iv = (ImageView) findViewById(
                        R.id.downloadedImage);
                photoAsyncTask = new SafeDownloadImageTask(ShowMyPuppyActivity.this, iv)
                        .execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            }
        });

    }
}
