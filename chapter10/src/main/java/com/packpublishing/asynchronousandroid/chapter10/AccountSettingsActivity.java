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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import java.util.concurrent.TimeUnit;


public class AccountSettingsActivity extends Activity {

    public static final String TAG = AccountSettingsActivity.class.getSimpleName();
    public static final String TASK_BACKUP = "backup";
    public static final String TASK_PERIODIC_BACKUP = "periodic_backup";

    TextView firstName;
    TextView lastName ;
    TextView age ;
    Button button;
    Button perButton;
    Button cancelButton;

    public static long ONE_HOUR = 60L;

    OnClickListener listener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AccountSettingsActivity.this);
            sharedPreferences.edit().putString("firstName", firstName.getText().toString()).apply();
            sharedPreferences.edit().putString("lastName", lastName.getText().toString()).apply();
            sharedPreferences.edit().putString("age",age.getText().toString()).apply();

            GcmNetworkManager gcmNM = GcmNetworkManager.getInstance(AccountSettingsActivity.this);
            Log.i(TAG, "Registering the BackupTask");
            OneoffTask task = new OneoffTask.Builder()
                    .setService(MyBackupService.class)
                    .setTag(TASK_BACKUP)
                    .setExecutionWindow(0L, ONE_HOUR)
                    .setRequiredNetwork(Task.NETWORK_STATE_UNMETERED)
                    .setRequiresCharging(true)
                    .setUpdateCurrent(true)
                    .build();

            gcmNM.schedule(task);
        }
    };


    OnClickListener startPeriodic = new OnClickListener(){
        @Override
        public void onClick(View v) {
            GcmNetworkManager gcmNM = GcmNetworkManager.getInstance(AccountSettingsActivity.this);
            Log.i(TAG, "Registering Periodic BackupTask");
            PeriodicTask task = new PeriodicTask.Builder()
                    .setService(MyBackupService.class)
                    .setTag(TASK_PERIODIC_BACKUP)
                    .setFlex(TimeUnit.HOURS.toSeconds(1))
                    .setRequiredNetwork(Task.NETWORK_STATE_UNMETERED)
                    .setRequiresCharging(true)
                    .setPeriod(TimeUnit.HOURS.toSeconds(6))
                    .setPersisted(true)
                    .build();
            gcmNM.schedule(task);
        }
    };


    OnClickListener cancelListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            GcmNetworkManager gcmNM = GcmNetworkManager.getInstance(AccountSettingsActivity.this);
            Log.i(TAG, "Cancelling Periodic BackupTask");
            gcmNM.cancelTask(TASK_PERIODIC_BACKUP, MyBackupService.class);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AccountSettingsActivity.this);

        firstName = (TextView)findViewById(R.id.firstName);
        firstName.setText(sharedPreferences.getString("firstName", ""));
        lastName = (TextView)findViewById(R.id.lastName);
        lastName.setText(sharedPreferences.getString("lastName",""));
        age = (TextView)findViewById(R.id.age);
        age.setText(sharedPreferences.getString("age",""));
        button = (Button)findViewById(R.id.save);
        button.setOnClickListener(listener);

        perButton = (Button)findViewById(R.id.periodic);
        perButton.setOnClickListener(startPeriodic);

        cancelButton = (Button)findViewById(R.id.cancel_periodic);
        cancelButton.setOnClickListener(cancelListener);

    }
}
