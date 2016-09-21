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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AlbumCursorAdapter extends CursorAdapter {

    Context ctx;
    private LayoutInflater inf;
    private LoaderManager mgr;
    private List<Integer> ids;
    private int count;

    public AlbumCursorAdapter(Context ctx, LoaderManager mgr) {
        super(ctx.getApplicationContext(), null, true);
        this.ctx = ctx;
        this.mgr = mgr;
        inf = (LayoutInflater) ctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ids = new ArrayList<Integer>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (View) inf.inflate(R.layout.album_item, parent, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.album_art);
        TextView artistView = (TextView) view.findViewById(R.id.album_artist);
        TextView albumView = (TextView) view.findViewById(R.id.album_name);
        int viewId = AlbumCursorAdapter.class.hashCode() + count++;
        Log.i("AlbumListActivity", "NewView["+viewId+"] with loader "+count);
        view.setId(viewId);
        mgr.initLoader(viewId, null, new ArtworkLoaderCallbacks(ctx, imageView));
        ids.add(viewId);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.album_art);
        TextView artistView = (TextView) view.findViewById(R.id.album_artist);
        TextView albumView = (TextView) view.findViewById(R.id.album_name);
        imageView.setImageBitmap(null);
        Loader<?> loader = mgr.getLoader(view.getId());
        AlbumArtworkLoader artworkLoader = (AlbumArtworkLoader) loader;
        // starts a new reload
        int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
        Log.i("AlbumListActivity", "bindView [" + view.getId() + "]  to album [" + albumId+"]");
        artworkLoader.setAlbumId(albumId);
        artistView.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
        albumView.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
    }

    public void destroyLoaders() {
        for (Integer id : ids) {
            mgr.destroyLoader(id);
        }
    }

    public static class ArtworkLoaderCallbacks implements LoaderManager.LoaderCallbacks<Bitmap> {

        private Context context;
        private ImageView image;

        public ArtworkLoaderCallbacks(Context context, ImageView image) {
            this.context = context.getApplicationContext();
            this.image = image;
        }
        @Override
        public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
            return new AlbumArtworkLoader(context);
        }
        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap b) {
            image.setImageBitmap(b);
        }

        @Override
        public void onLoaderReset(Loader<Bitmap> loader) {}
    }
}
