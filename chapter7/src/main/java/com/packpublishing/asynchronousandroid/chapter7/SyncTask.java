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
package com.packpublishing.asynchronousandroid.chapter7;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;



@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SyncTask extends AsyncTask<JobParameters, Void, Result<JobParameters>> {

    private static final String TAG = "SyncJobService";

    public static final String SYNC_FILE_KEY = "file";
    public static final String SYNC_PATH_KEY = "http_endpoint";

    private static final int NOTIFICACTION_ID = "SyncTaskNotif".hashCode();

    private final JobService jobService;

    public SyncTask(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    protected Result<JobParameters> doInBackground(JobParameters... params) {
        Log.i(TAG, "SyncTask starting ..");
        Result<JobParameters> result = new Result<JobParameters>();
        result.result = params[0];
        HttpURLConnection urlConn = null;
        try {
            URL url;
            DataOutputStream printout;
            DataInputStream input;
            String file = params[0].getExtras().getString(SYNC_FILE_KEY);
            String endpoint = params[0].getExtras().getString(SYNC_PATH_KEY);
            url = new URL ("http://192.168.1.83:4567/"+endpoint);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput (true);
            urlConn.setDoOutput (true);
            urlConn.setUseCaches (false);
            urlConn.setRequestProperty("Content-Type","application/json");
            urlConn.connect();
            //Create JSONObject here
            String body = Util.loadJSONFromFile(jobService,file);
            uploadJsonToServer(urlConn, body);

        } catch (Exception e) {
            Log.e("Upload Service", "upload failed", e);
            result.exc = e;
        } finally {
            Log.i(TAG, "SyncTask finished ..");
            if ( urlConn != null){
                urlConn.disconnect();
            }
        }
        return result;
    }

    private void uploadJsonToServer(HttpURLConnection urlConn, String body) throws Exception {
        OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
        writer.write(body);
        writer.flush();
        writer.close();
        int resultCode = urlConn.getResponseCode();
        if (resultCode != HttpURLConnection.HTTP_OK ) {
            throw new Exception("Failed to sync with server :"+resultCode);
        }
    }

    @Override
    protected void onPostExecute(Result<JobParameters> result) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(jobService);
        NotificationManager nm = (NotificationManager) jobService.
                getSystemService(jobService.NOTIFICATION_SERVICE);
        builder.setSmallIcon( android.R.drawable.sym_def_app_icon);
        if ( result.exc!=null ) {
            jobService.jobFinished(result.result, true);
            builder.setContentTitle("Failed to sync account")
                    .setContentText("Failed to sync account " + result.exc);
        } else{
            builder.setContentTitle("Account Updated")
                    .setContentText("Updated Account Sucessfully at " + new Date().toString());
            jobService.jobFinished(result.result, false);
        }
        nm.notify(NOTIFICACTION_ID, builder.build());

    }

}
