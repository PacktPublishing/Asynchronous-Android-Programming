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

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.GridView;

public class AlbumListActivity extends FragmentActivity
        implements LoaderCallbacks<Cursor> {

    public static final int ALBUM_LIST_LOADER = "phone_list".hashCode();
    private AlbumCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list_layout);
        GridView grid = (GridView)findViewById(R.id.album_grid);
        mAdapter = new AlbumCursorAdapter(getApplicationContext(),
                                          getSupportLoaderManager());
        grid.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(ALBUM_LIST_LOADER,
                                             null, AlbumListActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = new String[]{
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.changeCursor(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            getSupportLoaderManager().destroyLoader(ALBUM_LIST_LOADER);
            mAdapter.destroyLoaders();
        }
    }

}
