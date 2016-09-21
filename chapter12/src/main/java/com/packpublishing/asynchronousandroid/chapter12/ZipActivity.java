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
package com.packpublishing.asynchronousandroid.chapter12;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func2;
import rx.schedulers.Schedulers;


public class ZipActivity extends Activity {

    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.combining_layout);

        SingleSubscriber<String> subscriber = new SingleSubscriber<String>() {

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Failed to combine posts", e);
            }

            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "Result" + result);
                EditText console = (EditText) findViewById(R.id.text);
                console.setText(result);
            }
        };
        Single<JSONObject> postSingle = Single.create(
            new Single.OnSubscribe<JSONObject>() {
                @Override
                public void call(SingleSubscriber<? super JSONObject> sub) {
                    try {
                        Log.i(TAG,"Getting the Post Object on "+Thread.currentThread().getName());
                        sub.onSuccess(getJson("http://demo1472539.mockable.io/post"));
                    } catch (Throwable t) {
                        sub.onError(t);
                    }
                }
            }
        ).subscribeOn(Schedulers.newThread());

        Single<JSONObject> authorSingle = Single.create(
            new Single.OnSubscribe<JSONObject>() {
                @Override
                public void call(SingleSubscriber<? super JSONObject> sub) {
                    try {
                        Log.i(TAG,"Getting the Author Object on "+Thread.currentThread().getName());
                        sub.onSuccess(getJson("http://demo1472539.mockable.io/author"));
                    } catch (Throwable t) {
                        sub.onError(t);
                    }
                }
            }
        ).subscribeOn(Schedulers.newThread());


        Single.zip(postSingle, authorSingle, new Func2<JSONObject, JSONObject, String>() {
            @Override
            public String call(JSONObject post, JSONObject author) {
                String result = null;
                JSONObject rootObj = new JSONObject();
                try {
                    Log.i(TAG,"Combining objects on "+Thread.currentThread().getName());
                    rootObj.put("post", post);
                    rootObj.put("author", author);
                    result = rootObj.toString(2);
                } catch (Exception e) {
                    Exceptions.propagate(e);
                }
                return result;
            }
            // Convert the result to String
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);
    }

    JSONObject getJson(String urlString) throws Exception {

        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoInput(true);
        conn.connect();
        int responseCode = conn.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Unsuccessful HTTP Result code: " + responseCode+" for Url "+urlString);
        }
        InputStream is = conn.getInputStream();
        return new JSONObject(read(is));
    }


    protected String read(InputStream is) throws IOException {
        String result =null;
        int readBytes = 0;
        try {
            if (is != null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[128];
                int rc = 0;
                while ((rc = is.read(buffer)) > 0) {
                    baos.write(buffer, 0, rc);
                    readBytes += rc;
                }
                result =  new String(baos.toByteArray());;
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return result;
    }
}
