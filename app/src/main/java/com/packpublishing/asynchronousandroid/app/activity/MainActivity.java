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
package com.packpublishing.asynchronousandroid.app.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.packpublishing.asynchronousandroid.R;
import com.packpublishing.asynchronousandroid.chapter1.ExecutorActivity;
import com.packpublishing.asynchronousandroid.chapter1.RunFromUiActivity;
import com.packpublishing.asynchronousandroid.chapter10.AccountSettingsActivity;
import com.packpublishing.asynchronousandroid.chapter10.MessagingActivity;
import com.packpublishing.asynchronousandroid.chapter11.LocationActivity;
import com.packpublishing.asynchronousandroid.chapter11.MobileNetDetectionActivity;
import com.packpublishing.asynchronousandroid.chapter11.PaginatedActivity;
import com.packpublishing.asynchronousandroid.chapter12.HelloRxJava;
import com.packpublishing.asynchronousandroid.chapter12.RxListActivity;
import com.packpublishing.asynchronousandroid.chapter12.RxSchedulerActivity;
import com.packpublishing.asynchronousandroid.chapter12.ZipActivity;
import com.packpublishing.asynchronousandroid.chapter2.CancelRunnableActivity;
import com.packpublishing.asynchronousandroid.chapter2.HandlerExampleActivity;
import com.packpublishing.asynchronousandroid.chapter2.SchedulingWorkActivity;
import com.packpublishing.asynchronousandroid.chapter2.SpeakActivity;
import com.packpublishing.asynchronousandroid.chapter3.MyPuppyAlbumActivity;
import com.packpublishing.asynchronousandroid.chapter3.ShowMyPuppyHeadlessActivity;
import com.packpublishing.asynchronousandroid.chapter4.AlbumListActivitySimple;
import com.packpublishing.asynchronousandroid.chapter4.BitcoinExchangeRateActivity;
import com.packpublishing.asynchronousandroid.chapter4.WhoIsOnlineActivity;
import com.packpublishing.asynchronousandroid.chapter5.CountMsgsFromActivity;
import com.packpublishing.asynchronousandroid.chapter5.GrepActivity;
import com.packpublishing.asynchronousandroid.chapter5.SaveMyLocationActivity;
import com.packpublishing.asynchronousandroid.chapter5.Sha1Activity;
import com.packpublishing.asynchronousandroid.chapter5.UploadArtworkActivity;
import com.packpublishing.asynchronousandroid.chapter6.AlarmActivity;
import com.packpublishing.asynchronousandroid.chapter6.AlarmClockActivity;
import com.packpublishing.asynchronousandroid.chapter6.RepeatingAlarmActivity;
import com.packpublishing.asynchronousandroid.chapter6.SMSDispatchActivity;
import com.packpublishing.asynchronousandroid.chapter7.AccountInfoActivity;
import com.packpublishing.asynchronousandroid.chapter7.JobListActivity;
import com.packpublishing.asynchronousandroid.chapter8.GreetingsActivity;
import com.packpublishing.asynchronousandroid.chapter8.HelloSSLActivity;
import com.packpublishing.asynchronousandroid.chapter8.UserInfoActivity;
import com.packpublishing.asynchronousandroid.chapter8.UserListActivity;
import com.packpublishing.asynchronousandroid.chapter9.ConvertGrayScaleActivity;
import com.packpublishing.asynchronousandroid.chapter9.ExceptionActivity;
import com.packpublishing.asynchronousandroid.chapter9.MyNativeActivity;
import com.packpublishing.asynchronousandroid.chapter9.NativeThreadsActivity;
import com.packpublishing.asynchronousandroid.chapter9.StatsActivity;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<Example> examples = Arrays.asList(new Example[]{
        // Chapter 1
        new Example("Chap.1 - Executor", ExecutorActivity.class, 9),
        new Example("Chap.1 - Run from UI", RunFromUiActivity.class, 9),
        // Chapter 2
        new Example("Chap.2 - Scheduling Work", SchedulingWorkActivity.class, 9),
        new Example("Chap.2 - Cancelling Work", CancelRunnableActivity.class, 9),
        new Example("Chap.2 - Handler Example",  SpeakActivity.class,9),
        new Example("Chap.2 - Multi-thread Handler",  HandlerExampleActivity.class,9),
        // Chapter 3
        new Example("Chap.3 - Download AsyncTask", MyPuppyAlbumActivity.class, 9),
        new Example("Chap.3 - Headless Fragment", ShowMyPuppyHeadlessActivity.class, 9),
        // Chapter 4
        new Example("Chap.4 - Loading Data with Loader", WhoIsOnlineActivity.class, 9),
        new Example("Chap.4 - AsyncTaskLoader", BitcoinExchangeRateActivity.class, 9),
        new Example("Chap.4 - CursorLoader", AlbumListActivitySimple.class, 9),
        // Chapter 5
        new Example("Chap.5 - Started Service", SaveMyLocationActivity.class, 9),
        new Example("Chap.5 - Intent Service", CountMsgsFromActivity.class, 9),
        new Example("Chap.5 - Upload Image Service", UploadArtworkActivity.class, 9),
        new Example("Chap.5 - Bound Service", Sha1Activity.class, 9),
        new Example("Chap.5 - Remote Service", GrepActivity.class, 9),
        // Chapter 6
        new Example("Chap.6 - Scheduling Alarm", AlarmActivity.class, 9),
        new Example("Chap.6 - Alarm Clock", AlarmClockActivity.class, 9),
        new Example("Chap.6 - Repeating Alarm", RepeatingAlarmActivity.class, 9),
        new Example("Chap.6 - Wake up Alarm", SMSDispatchActivity.class, 9),
        // Chapter 7
        new Example("Chap.7 - Schedule Job", AccountInfoActivity.class, 21),
        new Example("Chap.7 - Cancel Jobs", JobListActivity.class, 21),
        // Chapter 8
        new Example("Chap.8 - Retrieve Remote Text", GreetingsActivity.class, 9),
        new Example("Chap.8 - Retrieve JSON", UserListActivity.class, 9),
        new Example("Chap.8 - Retrieve XML", UserInfoActivity.class, 9),
        new Example("Chap.8 - Connect using SSL", HelloSSLActivity.class, 9),
        // Chapter 9
        new Example("Chap.9 - Calling Native Functions", MyNativeActivity.class, 9),
        new Example("Chap.9 - Background Work on Native", ConvertGrayScaleActivity.class, 9),
        new Example("Chap.9 - Native Threads", NativeThreadsActivity.class, 9),
        new Example("Chap.9 - Wrapping Native Objects", StatsActivity.class, 9),
        new Example("Chap.9 - Exception Handling", ExceptionActivity.class, 9),
        // Chapter 10
        new Example("Chap.10 - Up/DownStream data", MessagingActivity.class, 9),
        new Example("Chap.10 - GCM JobScheduling", AccountSettingsActivity.class, 9),
        // Chapter 11
        new Example("Chap.11 - Post Event", MobileNetDetectionActivity.class, 9),
        new Example("Chap.11 - Post Sticky Event", LocationActivity.class, 9),
        new Example("Chap.11 - Delivery Mode", PaginatedActivity.class, 9),
        // Chapter 12
        new Example("Chap.12 - Hello RxJava", HelloRxJava.class, 9),
        new Example("Chap.12 - Debounce Operator", RxListActivity.class, 9),
        new Example("Chap.12 - Combining Operator", ZipActivity.class, 9),
        new Example("Chap.12 - Rx Scheduler", RxSchedulerActivity.class, 9),
    });

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Android Asynchronous Programming");

        mRecyclerView = (RecyclerView) findViewById(R.id.exampleList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ExampleAdapter(this,examples);
        mRecyclerView.setAdapter(mAdapter);
    }
}
