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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import com.packpublishing.asynchronousandroid.chapter3.R;

public class DownloadImageTask extends AsyncTask<URL, Integer, Bitmap> {

    int downloadedBytes = 0;
    int totalBytes = 0;
    private final WeakReference<Context> ctx;
    private ProgressDialog progress;

    private final WeakReference<ImageView> imageView;
    public DownloadImageTask(Context ctx, ImageView imageView) {
        this.imageView = new WeakReference<ImageView>(imageView);
        this.ctx = new WeakReference<Context>(ctx);
    }

    @Override
    protected void onPreExecute() {
        if(ctx.get()!=null) {
            progress = new ProgressDialog(ctx.get());
            progress.setTitle(R.string.downloading_image);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setProgress(0);
            progress.setMax(100);
            progress.setCancelable(true);
            progress.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            DownloadImageTask.this.cancel(false);
                        }
                    });
            progress.show();
        }
    }

    @Override
    protected Bitmap doInBackground(URL... params) {
        URL url = params[0];;
        return downloadBitmap(url);
    }


    private Bitmap downloadBitmap(URL url) {
        Bitmap bitmap =null;
        InputStream is = null;
        try {
            if (isCancelled()) {
                return null;
            }
            publishProgress(0);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK){
                throw new Exception("Unsucesfull Result code");
            }
            totalBytes = conn.getContentLength();
            downloadedBytes = 0;

            is = conn.getInputStream();
            BufferedInputStream bif = new BufferedInputStream(is) {

                int progress = 0;

                public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
                    int readBytes = super.read(buffer, byteOffset, byteCount);
                    if ( isCancelled() ){
                        // Returning -1 means that there is no more data because the
                        // end of the stream has been reached.
                        return -1;
                    }
                    if (readBytes > 0) {
                        downloadedBytes += readBytes;
                        // int percent = (int) ((((float) downloadedBytes) / ((float) totalBytes)) * 100);
                        int percent = (int) ((downloadedBytes * 100f) / totalBytes);
                        if (percent > progress) {
                            publishProgress(percent);
                            progress = percent;
                        }
                    }
                    return readBytes;
                }
            };
            Bitmap downloaded = BitmapFactory.decodeStream(bif);
            if ( !isCancelled() ){
                bitmap = downloaded;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    @Override
    protected void onCancelled() {
        if ( imageView.get() != null && ctx.get() != null ) {
            Bitmap bitmap = BitmapFactory.decodeResource(
                    ctx.get().getResources(),
                    R.drawable.default_photo
            );
            this.imageView.get().setImageBitmap(bitmap);
        }
        progress.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.i("DownloadProgress","Image download "+values[0]);
        progress.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if( progress!=null )
            progress.dismiss();
        ImageView imageView = this.imageView.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
