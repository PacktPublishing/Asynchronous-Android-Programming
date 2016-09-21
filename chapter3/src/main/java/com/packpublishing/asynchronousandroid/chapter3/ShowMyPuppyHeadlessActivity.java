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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.packpublishing.asynchronousandroid.chapter3.R;

public class ShowMyPuppyHeadlessActivity extends FragmentActivity
                                         implements DownloadImageHeadlessFragment.AsyncListener{

    private static final String DOWNLOAD_PHOTO_FRAG = "download_photo_as_fragment";

    private DownloadImageHeadlessFragment downloadFragment;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puppy_headless_activity);
        FragmentManager fm = getSupportFragmentManager();
        downloadFragment = (DownloadImageHeadlessFragment) fm.findFragmentByTag(DOWNLOAD_PHOTO_FRAG);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (downloadFragment == null) {
            downloadFragment = DownloadImageHeadlessFragment.
                                newInstance("http://img.allw.mn/content" +
                                            "/www/2009/03/april1.jpg");
            fm.beginTransaction().add(downloadFragment, DOWNLOAD_PHOTO_FRAG).commit();
        }
    }

    @Override
    public void onPreExecute() {
        if (progress == null)
            prepareProgressDialog();
    }

    void prepareProgressDialog(){
        progress = new ProgressDialog(this);
        progress.setTitle(R.string.downloading_image);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadFragment.cancel();
            }
        });
        progress.show();
    }

    @Override
    public void onProgressUpdate(Integer... value) {
        if (progress == null)
            prepareProgressDialog();

        progress.setProgress(value[0]);
    }

    @Override
    public void onPostExecute(Bitmap result) {
        if(result!=null) {
            ImageView iv = (ImageView) findViewById(
                    R.id.downloadedImage);
            iv.setImageBitmap(result);
        }
        cleanUp();
    }

    @Override
    public void onCancelled(Bitmap result) {
        cleanUp();
    }
    private void cleanUp() {
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentByTag(DOWNLOAD_PHOTO_FRAG);
        fm.beginTransaction().remove(frag).commit();
    }
}
