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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class AlbumListActivitySimple extends FragmentActivity implements LoaderCallbacks<Cursor> {

    private static final int MY_PERMISSIONS_EXT_STORAGE = 1;

    public static final int ALBUM_LIST_LOADER = "phone_list".hashCode();
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list_layout);

        // Here, thisActivity is the current activity
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_EXT_STORAGE);

        } else{
            initUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_EXT_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initUI();
                }
                return;
            }
        }
    }

    void initUI(){
        GridView grid = (GridView)findViewById(R.id.album_grid);
        mAdapter = new AlbumCursorAdapter(getApplicationContext());
        grid.setAdapter(mAdapter);
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(ALBUM_LIST_LOADER,
                null, AlbumListActivitySimple.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = new String[]{
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM
        };
        return new CursorLoader(this, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                columns, // projection
                                null, // selection
                                null, // selectionArgs
                                null // sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.changeCursor(data);
        TextView albumCount = (TextView) findViewById(R.id.albumCount);
        albumCount.setText(String.format("%d Albums",data.getCount()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.changeCursor(null);
    }

    public static class AlbumCursorAdapter extends SimpleCursorAdapter {
        private static String[] FIELDS = new String[]{
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM
        };
        private static int[] VIEWS = new int[]{
                R.id.album_artist, R.id.album_name
        };

        public AlbumCursorAdapter(Context context) {
            super(context, R.layout.album_item,
                    null, FIELDS, VIEWS, 0);
        }
    }
}
