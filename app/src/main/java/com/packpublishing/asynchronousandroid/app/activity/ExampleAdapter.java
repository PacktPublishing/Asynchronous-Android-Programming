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

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.packpublishing.asynchronousandroid.R;

import java.util.List;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ViewHolder> {


    public final String TAG = this.getClass().getSimpleName();

    private final List<Example> mDataset;
    private final Activity mActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.title);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExampleAdapter(Activity activity,final List<Example> myDataset) {
        mDataset = myDataset;
        mActivity= activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ExampleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.example_row_layout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).title);
        if( Build.VERSION.SDK_INT >= mDataset.get(position).minVersion){
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class  toStart =  mDataset.get(position).clazz;
                    Log.i(TAG,"Starting example activity "+toStart.getSimpleName());
                    mActivity.startActivity(
                        new Intent(mActivity,(mDataset.get(position).clazz))
                    );
                }
            });
        } else {
            holder.mTextView.setTextColor(mActivity.getResources().getColor(R.color.hidden));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
