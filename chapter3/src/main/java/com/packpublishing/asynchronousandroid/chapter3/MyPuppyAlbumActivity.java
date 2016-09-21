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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MyPuppyAlbumActivity extends FragmentActivity {

    List<AsyncTask> photoAsyncTasks=new ArrayList<AsyncTask>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel Pending Tasks
        for ( AsyncTask task :photoAsyncTasks) {
           if(task.getStatus()!=AsyncTask.Status.FINISHED){
               task.cancel(true);
           }
        }
        photoAsyncTasks.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_album_activity);
        Button showBut =  (Button)findViewById(R.id.showImageBut);
        showBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    URL url1 = new URL("http://img.allw.mn/content/www/2009/03/april1.jpg");
                    URL url2 = new URL("http://media.mydogspace.com.s3.amazonaws.com/wp-content/uploads/2013/08/puppy-500x350.jpg");
                    URL url3 = new URL("http://hunde-wiese.ch/article_images/stubenrein.jpg");
                    URL url4 = new URL("https://aussiedog.com.au/wp-content/uploads/2015/03/puppy-bite.jpg");

                    ImageView iv1 = (ImageView) findViewById(R.id.photo1);
                    ImageView iv2 = (ImageView) findViewById(R.id.photo2);
                    ImageView iv3 = (ImageView) findViewById(R.id.photo3);
                    ImageView iv4 = (ImageView) findViewById(R.id.photo4);

                    /*new SleepAsyncTask(1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1000);
                    new SleepAsyncTask(2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1000);
                    new SleepAsyncTask(3).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1000);
                    new SleepAsyncTask(4).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1000);*/

                    AsyncTask photo1AsyncTask= new DownloadImageTask(MyPuppyAlbumActivity.this, iv1)
                            .execute(url1);
                    photoAsyncTasks.add(photo1AsyncTask);
                    AsyncTask photo2AsyncTask = new DownloadImageTask(MyPuppyAlbumActivity.this, iv2)
                            .execute(url2);
                    photoAsyncTasks.add(photo2AsyncTask);
                    AsyncTask photo3AsyncTask=new DownloadImageTask(MyPuppyAlbumActivity.this, iv3)
                            .execute(url3);
                    photoAsyncTasks.add(photo3AsyncTask);
                    AsyncTask photo4AsyncTask = new DownloadImageTask(MyPuppyAlbumActivity.this, iv4)
                            .execute(url4);
                    photoAsyncTasks.add(photo4AsyncTask);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
