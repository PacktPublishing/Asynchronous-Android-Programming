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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;

public class AlbumArtworkLoader extends AsyncTaskLoader<Bitmap> {

    private int mAlbumId = -1;
    Bitmap mData = null;

    public AlbumArtworkLoader(Context context) {
        super(context);
    }

    public AlbumArtworkLoader(Context context, int albumId) {
        super(context);
        this.mAlbumId = albumId;
    }

    public void setAlbumId(int newAlbumId) {

        if (  isDifferentMedia(newAlbumId) || mData == null ){
            Log.i("AlbumListActivity", "Album id changed from " + mAlbumId + " to "+ newAlbumId);
            this.mAlbumId = newAlbumId;
            onContentChanged();
            // Same AlBum
         } else if (!isDifferentMedia(newAlbumId) ) {
            deliverResult(mData);
        }
    }

    private boolean isDifferentMedia(int newMediaId){
      return mAlbumId !=newMediaId;
    }


    @Override
    public Bitmap loadInBackground() {
        Log.i("AlbumListActivity","Loading Album Art for album "+mAlbumId);
        Bitmap bitmap = null;
        ContentResolver res = getContext().getContentResolver();

        if (mAlbumId != -1) {
            try {
                Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, mAlbumId);
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContext().getContentResolver(), albumArtUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 180, 180, true);
            } catch (IOException e) {
                Log.e("AlbumListActivity","Failed to load",e);
            }
            return bitmap;
        }
        return bitmap;
    }

    @Override
    protected void onStartLoading() {
        Log.i("AlbumListActivity", "Starting the loader " +getId());
        if (mData != null) {
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null){
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        Log.i("AlbumListActivity","Resetting the loader "+getId());
        if (mData != null) {
            mData.recycle();
            mData = null;
        }
    }

    @Override
    public void onCanceled(Bitmap data) {
        Log.i("AlbumListActivity","Canceling the loader "+getId());
        if (data != null) {
            data.recycle();
            mData = null;
        }
    }

    @Override
    protected void onStopLoading() {
        Log.i("AlbumListActivity","Stopping the loader "+getId());
        cancelLoad();
    }


}
