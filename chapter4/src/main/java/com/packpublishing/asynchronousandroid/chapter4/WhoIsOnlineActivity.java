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


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.packpublishing.asynchronousandroid.chapter4.R;

import java.util.List;

public class WhoIsOnlineActivity extends FragmentActivity implements LoaderCallbacks<List<String>> {

    public static final int WHO_IS_ONLINE_LOADER_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter4_room_list);
        final LoaderManager lm =  getSupportLoaderManager();
        final Bundle bundle =new Bundle();
        bundle.putString("chatRoom", "Developers");
       // lm.initLoader(WHO_IS_ONLINE_LOADER_ID, bundle, this);
        Button initButton = (Button)findViewById(R.id.init);
        Button restartButton = (Button)findViewById(R.id.restart);
        Button destroyButton = (Button)findViewById(R.id.destroy);
        Button closeButton = (Button)findViewById(R.id.closeActivty);
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("WhoIsOnlineLoader", "LoaderManager.init [" + WHO_IS_ONLINE_LOADER_ID+ "]");
                lm.initLoader(WHO_IS_ONLINE_LOADER_ID, bundle, WhoIsOnlineActivity.this);
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("WhoIsOnlineLoader", "LoaderManager.restart [" + WHO_IS_ONLINE_LOADER_ID+ "]");
                lm.restartLoader(WHO_IS_ONLINE_LOADER_ID, bundle, WhoIsOnlineActivity.this);
            }
        });
        destroyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("WhoIsOnlineLoader", "LoaderManager.destroy [" + WHO_IS_ONLINE_LOADER_ID+ "]");
                lm.destroyLoader(WHO_IS_ONLINE_LOADER_ID);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhoIsOnlineActivity.this.finish();
            }
        });

    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        Loader res = null;
        Log.i("WhoIsOnlineLoader", "LoaderCallbacks.onCreateLoader[" + id + "]");
        switch (id) {
            case WHO_IS_ONLINE_LOADER_ID:
                res = new WhosOnlineLoader(this, args.getString("chatRoom"));
                break;
        }
        return res;
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> users) {
        Log.i("WhoIsOnlineLoader", "LoaderCallbacks.onLoadFinished[" + loader.getId() + "]");
        switch (loader.getId()) {
            case WHO_IS_ONLINE_LOADER_ID:
                ListView listView = (ListView) findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, users);

                listView.setAdapter(adapter);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        Log.i("WhoIsOnlineLoader","LoaderCallbacks.onLoaderReset["+loader.getId()+"]");
        switch (loader.getId()) {
            case WHO_IS_ONLINE_LOADER_ID:
                ListView listView = (ListView)findViewById(R.id.list);
                listView.setAdapter(null);
                break;
        }
    }
}
