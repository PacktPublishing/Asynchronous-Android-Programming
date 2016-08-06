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
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.packpublishing.asyncandroid.chapter7.R;

import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobListRecyclerAdapter extends RecyclerView.Adapter<JobListRecyclerAdapter.JobViewHolder> {

    private List<JobInfo> mJobList;
    private JobListActivity mContext;

    public JobListRecyclerAdapter(JobListActivity context, List<JobInfo> jobList) {
        this.mJobList = jobList;
        this.mContext = context;
    }

    @Override
    public JobListRecyclerAdapter.JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_view, null);
        JobViewHolder viewHolder = new JobViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(JobListRecyclerAdapter.JobViewHolder holder, int position) {

        final JobInfo ji= mJobList.get(position);
        holder.jobId.setText(Integer.toString(ji.getId()));
        holder.serviceName.setText(ji.getService().getClassName());
        holder.stopBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                JobScheduler jobScheduler = (JobScheduler)mContext.getSystemService(mContext.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancel(ji.getId());
                Log.i("JobList", "Stopping the job "+ji.getId());
                Toast.makeText(mContext,
                        "Canceling the job "+ji.getId(),
                        Toast.LENGTH_LONG).show();
                mContext.initList();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != mJobList ? mJobList.size() : 0);
    }


    public static class JobViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView jobId;
        TextView serviceName;
        Button stopBut;

        JobViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            jobId = (TextView)itemView.findViewById(R.id.jobIdTv);
            serviceName = (TextView)itemView.findViewById(R.id.className);
            stopBut = (Button)itemView.findViewById(R.id.stopBut);
        }
    }
}
