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
package com.packpublishing.asynchronousandroid.chapter4;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitcoinExchangeRateLoader extends AsyncTaskLoader<Double> {

    private Double mExchangeRate = null;
    private long    mRefreshinterval;
    private Handler mHandler;
    private String mCurrency;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            onContentChanged();
        }
    };

    BitcoinExchangeRateLoader(Context ctx, String currency,int refreshinterval) {
        super(ctx);
        this.mRefreshinterval = refreshinterval;
        this.mHandler = new Handler();
        this.mCurrency= currency;
    }

    @Override
    protected void onStartLoading() {
        Log.i("BitcoinExchangeRate", "Starting");
        if (mExchangeRate != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mExchangeRate);
        }
        if (takeContentChanged() || mExchangeRate == null) {
            // If the mData has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }


    @Override
    protected void onStopLoading() {
        Log.i("BitcoinExchangeRate", "Stopping");
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Double data) {
       onContentChanged();
    }


    @Override
    protected void onReset() {
        super.onReset();
        Log.i("BitcoinExchangeRate", "Reset");
        // Ensure the loader is stopped
        onStopLoading();
        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mExchangeRate != null) {
            mExchangeRate = null;
        }
        // Stop monitoring for changes.
        mHandler.removeCallbacks(refreshRunnable);
    }

    @Override
    public void deliverResult(Double result) {
        this.mExchangeRate = result;
        super.deliverResult(result);
    }

    @Override
    public Double loadInBackground() {
        Double result = null;
        Log.i("BitcoinExchangeRate", "Refreshing the rate in background");
        try {
            StringBuilder builder = new StringBuilder();
            URL url = new URL("https://blockchain.info/ticker");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONObject obj = new JSONObject(builder.toString());
            result = obj.getJSONObject(mCurrency).getDouble("last");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        mHandler.removeCallbacks(refreshRunnable);
        if (!isReset())
            mHandler.postDelayed(refreshRunnable, mRefreshinterval);
    }

}
