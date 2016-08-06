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
package com.packpublishing.asynchronousandroid.chapter10;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;
import java.util.Random;


public class MyBackupService extends GcmTaskService {

    public static final String TAG = AccountSettingsActivity.class.getSimpleName();

    private  static String FIRST_NAME="firstName";
    private  static String LAST_NAME="lastName";
    private  static String AGE="age";
    private  static String RESOURCE="/account";
    private  static String OPERATION="update";


    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.i(TAG, "Backing up the account settings");

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle data = new Bundle();
        data.putString(FIRST_NAME, sharedPreferences.getString(FIRST_NAME,""));
        data.putString(LAST_NAME, sharedPreferences.getString(LAST_NAME,""));
        data.putString(AGE, sharedPreferences.getString(AGE,""));
        data.putString("resource", RESOURCE);
        data.putString("operation", sharedPreferences.getString(OPERATION,""));
        String id = Integer.toString(new Random().nextInt());
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MyBackupService.this);
        try {
            gcm.send(getString(R.string.gcm_SenderId) + "@gcm.googleapis.com", id, data);
        } catch (IOException e) {
            Log.e(TAG, "Failed to backup account");
            return GcmNetworkManager.RESULT_RESCHEDULE;
        }


        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
