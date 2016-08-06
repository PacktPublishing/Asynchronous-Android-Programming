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
package com.packpublishing.asynchronousandroid.chapter9;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ImageView;

public class ConvertGrayScaleActivity
        extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<Result<Bitmap>> {

    public static final int IMAGE_LOADER = "image_loader".hashCode();

    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("mylib");
    }

    @Override
    public Loader<Result<Bitmap>> onCreateLoader(int id, Bundle args) {
        return new GrayImageLoader(this, "packt.png");
    }

    @Override
    public void onLoadFinished(Loader<Result<Bitmap>> loader, Result<Bitmap> data) {
        if (data.obj != null) {
            ImageView iv = (ImageView) findViewById(R.id.grayImage);
            iv.setImageBitmap(data.obj);
        } else {
            Log.e("ConvertGrayScale", data.error.getMessage(), data.error);
        }
    }

    @Override
    public void onLoaderReset(Loader<Result<Bitmap>> loader) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gray_layout);

        getSupportLoaderManager().initLoader(IMAGE_LOADER,
                null, ConvertGrayScaleActivity.this);
    }
}
