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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.packpublishing.asynchronousandroid.chapter3.R;

public class DownloadImageHeadlessFragment extends Fragment {

    public interface AsyncListener {
        void onPreExecute();
        void onProgressUpdate(Integer... progress);
        void onPostExecute(Bitmap result);
        void onCancelled(Bitmap result);
    }

    private AsyncListener listener;
    private DownloadImageTask task;

    public static DownloadImageHeadlessFragment newInstance(String url) {
        DownloadImageHeadlessFragment myFragment = new DownloadImageHeadlessFragment();
        Bundle args = new Bundle();
        args.putString("url",url);
        myFragment.setArguments(args);
        return myFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        task = new DownloadImageTask();
        URL url = null;
        try {
            String urlStr = getArguments().getString("url");
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        task.execute(url);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (AsyncListener)activity;
    }

    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    public void cancel(){
        if(task!=null){
            task.cancel(false);
        }
    }

    private class DownloadImageTask extends AsyncTask<URL, Integer, Bitmap> {

        int downloadedBytes = 0;
        int totalBytes = 0;

        @Override
        protected Bitmap doInBackground(URL... params) {
            Bitmap bitmap =null;
            InputStream is = null;
            try {
                if (isCancelled()) {
                    return null;
                }
                publishProgress(0);
                HttpURLConnection conn = (HttpURLConnection) params[0].openConnection();
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

                    public int read(byte[] buffer, int byteOffset, int byteCount)
                               throws IOException {
                        int readBytes = super.read(buffer, byteOffset, byteCount);
                        if ( isCancelled() ){
                            // Returning -1 means that there is no more data because the
                            // end of the stream has been reached.
                            return -1;
                        }
                        if (readBytes > 0) {
                            downloadedBytes += readBytes;
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

        protected void onPreExecute() {
            if (listener != null) listener.onPreExecute();
        }
        protected void onProgressUpdate(Integer... values) {
            if (listener != null)
                listener.onProgressUpdate(values);
        }
        protected void onPostExecute(Bitmap result) {
            if (listener != null)
                listener.onPostExecute(result);
        }
        protected void onCancelled(Bitmap result) {
            if (listener != null)
                listener.onCancelled(result);
        }

    }

}
