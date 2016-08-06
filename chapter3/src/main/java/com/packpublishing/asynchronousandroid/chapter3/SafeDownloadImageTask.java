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
import android.app.ProgressDialog;
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


public class SafeDownloadImageTask extends AsyncTask<URL, Integer, Result<Bitmap>> {

    int downloadedBytes = 0;
    int totalBytes = 0;
    private final WeakReference<Activity> ctx;
    private ProgressDialog progress;

    private final WeakReference<ImageView> imageView;

    public SafeDownloadImageTask(Activity ctx, ImageView imageView) {
        this.imageView = new WeakReference<ImageView>(imageView);
        this.ctx = new WeakReference<Activity>(ctx);
    }

    @Override
    protected void onPreExecute() {
        if (ctx.get() != null) {
            progress = new ProgressDialog(ctx.get());
            progress.setTitle(R.string.downloading_image);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setProgress(0);
            progress.setMax(100);
            progress.setCancelable(true);
            progress.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            SafeDownloadImageTask.this.cancel(false);
                        }
                    });
            progress.show();
        }
    }

    @Override
    protected Result<Bitmap> doInBackground(URL... params) {
        URL url = params[0];
        ;
        return downloadBitmap(url);
    }

    private Result<Bitmap> downloadBitmap(URL url) {
        Result<Bitmap> result = new Result<Bitmap>();
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            if (isCancelled()) {
                return result;
            }
            publishProgress(0);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int responseCode = conn.getResponseCode();

            responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Unsuccessful HTTP Result code: " + responseCode);
            }
            totalBytes = conn.getContentLength();
            downloadedBytes = 0;

            is = conn.getInputStream();
            BufferedInputStream bif = new BufferedInputStream(is) {

                int progress = 0;

                public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
                    int readBytes = super.read(buffer, byteOffset, byteCount);
                    if (isCancelled()) {
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
            if (!isCancelled()) {
                bitmap = downloaded;
            }
            Log.i("DownloadProgress", "Image download finished");
            result.result = bitmap;
        } catch (Throwable e) {
            result.error = e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void loadDefaultImage(ImageView iv) {
        Bitmap bitmap = BitmapFactory.decodeResource(
                ctx.get().getResources(),
                R.drawable.default_photo
        );
        iv.setImageBitmap(bitmap);
    }


    @Override
    protected void onCancelled() {
        if (imageView.get() != null && ctx.get() != null) {
            loadDefaultImage(this.imageView.get());
        }
        if( progress!=null )
            progress.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.i("DownloadProgress", "Image download " + values[0]);
        progress.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Result<Bitmap> result) {
        super.onPostExecute(result);

        if( progress!=null )
            progress.dismiss();

        ImageView imageView = this.imageView.get();

        if (imageView != null) {
            if (result.error != null) {
                Log.e("SafeDownloadImageTask", "Failed to download image ", result.error);
                loadDefaultImage(imageView);
            } else {
                imageView.setImageBitmap(result.result);
            }
        }
    }
}
