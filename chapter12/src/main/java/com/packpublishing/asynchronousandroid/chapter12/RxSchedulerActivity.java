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
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxSchedulerActivity extends Activity {

    private static final String TRANSLATE_URL="http://demo1472539.mockable.io/translate";
    private static final String RETRIEVE_TEXT_URL="http://demo1472539.mockable.io/mytext";

    ProgressDialog progress;

    public final String TAG = this.getClass().getSimpleName();

    class  MySubscriber extends Subscriber<String>{

        @Override
        public void onCompleted() {}

        @Override
        public void onError(Throwable e) {
            // Shows a Toast on Error
            Toast.makeText(RxSchedulerActivity.this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error found processing stream", e);
        }

        @Override
        public void onNext(String text) {
            EditText textFrame = (EditText)findViewById(R.id.text);
            textFrame.setText(text);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rx_scheduler_layout);
        Button startRequest = (Button)findViewById(R.id.retrieveBut);
        startRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextFromNetwork(RETRIEVE_TEXT_URL)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MySubscriber());
            }
        });


        Button translateRequest = (Button)findViewById(R.id.getAndTranslateBut);
        translateRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextFromNetwork(RETRIEVE_TEXT_URL)
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String toTranslate) {
                                return translateOnNetwork(TRANSLATE_URL, toTranslate);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MySubscriber());
            }
        });
        Button withProgress = (Button)findViewById(R.id.getTransProgress);
        withProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Observable.just(RETRIEVE_TEXT_URL)
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String url) {
                                progress = ProgressDialog.show(RxSchedulerActivity.this, "Loading",
                                        "Translation started", true);
                                Log.i(TAG, "Network IO Operation will start "+ threadMark());
                            }
                        })
                        .observeOn(Schedulers.io())
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String url) {
                                return getTextFromNetwork(url);
                            }
                        })
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String text) {
                                Log.i(TAG, "Text retrieved with success "+threadMark());
                                Log.i(TAG, "Translating the text "+threadMark());
                            }
                        })
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String toTranslate) {
                                return translateOnNetwork(TRANSLATE_URL, toTranslate);
                            }
                        })
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String translatedText) {
                                Log.i(TAG, "Translation finished "+threadMark());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnTerminate(new Action0() {
                            @Override
                            public void call() {
                                Log.i(TAG, "Dismissing dialog "+threadMark());
                                if (progress != null)
                                    progress.dismiss();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MySubscriber());
            }
        });
    }

    String threadMark(){
     return "on Thread [" + Thread.currentThread().getName()+"]";
    }


    Observable<String> getTextFromNetwork(final String urlString) {

        return Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        try {
                            String text = downloadText(urlString);
                            sub.onNext(text);
                            sub.onCompleted();
                        } catch (Throwable t) {
                            sub.onError(t);
                        }
                    }
                }
        );
    }

    Observable<String> translateOnNetwork(final String url,final String toTranslate) {

        return Observable.create(
            new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> sub) {
                    try {
                        String text = translateText(TRANSLATE_URL, toTranslate);;
                        sub.onNext(text);
                        sub.onCompleted();
                    } catch (Throwable t) {
                        sub.onError(t);
                    }
                }
            }
        );
    }

    String downloadText(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "text/plain");
        conn.setDoInput(true);
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Unsuccessful HTTP Result code: " + responseCode+" for Url "+urlString);
        }
        InputStream is = conn.getInputStream();
        return read(is);
    }

    String translateText(String url,String content) throws Exception {
        InputStream is = null;
        URL urltoAccess = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urltoAccess.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "text/plain");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();

        OutputStream os = conn.getOutputStream();
        os.write(content.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        responseCode = conn.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Unsuccessful HTTP Result code: " + responseCode+" for Url "+url);
        }
        is = conn.getInputStream();

        return read(is);
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
