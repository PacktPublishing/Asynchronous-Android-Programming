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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class GrayImageLoader extends AsyncTaskLoader<Result<Bitmap>> {

    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("mylib");
    }

    final String fileName;
    Bitmap grayImage;

    public GrayImageLoader(Context ctx, String fileName) {
        super(ctx);
        this.fileName = fileName;
    }

    @Override
    public Result<Bitmap> loadInBackground() {
        Result<Bitmap> result = new Result<Bitmap>();
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap b = BitmapFactory.decodeFile(this.fileName, options);
            Bitmap originalImage = BitmapFactory.decodeStream(getContext().getAssets().open(fileName));
            result.obj = convertImageToGray(originalImage);
        } catch (Exception e) {
            result.error = e;
        }
        return result;
    }


    private native Bitmap convertImageToGray(Bitmap original);

    @Override
    protected void onStartLoading() {

        Log.i("GrayImageLoader", "Starting the loader " + getId());
        if (grayImage != null) {
            Result<Bitmap> result = new Result<Bitmap>();
            result.obj = grayImage;
            deliverResult(result);
        }
        if (takeContentChanged() || grayImage == null) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        Log.i("GrayImageLoader", "Resetting the loader " + getId());
        if (grayImage != null) {
            grayImage.recycle();
            grayImage = null;
        }
    }

    @Override
    public void onCanceled(Result<Bitmap> result) {
        Log.i("GrayImageLoader", "Canceling the loader " + getId());
        if (result.obj != null) {
            result.obj.recycle();
            grayImage = null;
        }
    }

    @Override
    protected void onStopLoading() {
        Log.i("GrayImageLoader", "Stopping the loader " + getId());
        cancelLoad();
    }
}
