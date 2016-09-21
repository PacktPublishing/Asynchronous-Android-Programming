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

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UploadArtworkIntentService extends IntentService {

    ImageUploader mImageUploader;

    public UploadArtworkIntentService() {
        super("UploadIntentService");
    }

    public void onCreate() {
        super.onCreate();
        mImageUploader = new ImageUploader(getContentResolver());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri data = intent.getData();
        Log.i("Upload Service", "New  upload file "+data.toString());

        // unique id per upload, so each has its own notification
        int id = Integer.parseInt(data.getLastPathSegment());
        String msg = String.format("Uploading %s.jpg", id);

        ProgressNotificationCallback progress =
                new ProgressNotificationCallback(this, id, msg);
        // On Upload sucess
        if (mImageUploader.upload(data, progress)) {
            Log.i("Upload Service", "Upload complete for file "+data.toString());
            progress.onComplete(
                    String.format("Upload finished for %s.jpg", id));
        } else {
            Log.i("Upload Service", "Upload failed for file "+data.toString());
            progress.onComplete(
                    String.format("Upload failed %s.jpg", id));
        }
    }

    private class ProgressNotificationCallback
            implements ImageUploader.ProgressCallack {
        private NotificationCompat.Builder builder;
        private NotificationManager nm;
        private int id, prev;

        public ProgressNotificationCallback(
                Context ctx, int id, String msg) {
            this.id = id;
            prev = 0;
            builder = new NotificationCompat.Builder(ctx)
                    .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                    .setContentTitle("Uploading Artwork")
                    .setContentText(msg)
                    .setProgress(100, 0, false);
            nm = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(id, builder.build());
        }

        public void onProgress(int max, int progress) {
            int percent = (int) ((100f * progress) / max);
            if (percent > (prev + 5)) {
                builder.setProgress(100, percent, false);
                nm.notify(id, builder.build());
                prev = percent;
            }
        }

        public void onComplete(String msg) {
            builder.setProgress(0, 0, false);
            builder.setContentText(msg);
            nm.notify(id, builder.build());
        }
    }

    public static class ImageUploader {

        final ContentResolver mContentResolver;

        public ImageUploader(ContentResolver contentResolver) {
            this.mContentResolver = contentResolver;
        }

        interface ProgressCallack {
            void onProgress(int max, int progress);
        }

        private void pump(
                InputStream in, OutputStream out,
                ProgressCallack callback, int len)
                throws IOException {

            int length, i = 0, size = 1024;
            byte[] buffer = new byte[size]; // 1kb buffer
            while ((length = in.read(buffer)) > -1) {
                out.write(buffer, 0, length);
                out.flush();
                if (callback != null)
                    callback.onProgress(len, ++i * size);
            }
        }
        int getContentLength(Uri imageUrl) throws IOException {
            int size= mContentResolver.openInputStream(imageUrl).available();
            return size;
        }

        private static final String UPLOAD_URL = "http://192.168.1.10:4567/account_sync";

        public boolean upload(Uri data, ProgressCallack callback) {
            HttpURLConnection conn = null;
            try {
                URL url;
                URLConnection urlConn;
                DataOutputStream printout;
                DataInputStream input;
                url = new URL (UPLOAD_URL);
                urlConn = url.openConnection();
                urlConn.setDoInput (true);
                urlConn.setDoOutput (true);
                urlConn.setUseCaches (false);
                urlConn.setRequestProperty("Content-Type","application/json");
                urlConn.connect();
                //Create JSONObject here
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("ID", "25");
                jsonParam.put("description", "Real");
                jsonParam.put("enable", "true");
            } catch (Exception e) {
                Log.e("Upload Service", "upload failed", e);
                return false;
            } finally {
                if ( conn != null ) {
                    conn.disconnect();
                }
            }
            return true;
        }
    }
}
