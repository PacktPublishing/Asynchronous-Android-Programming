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
package com.packpublishing.asynchronousandroid.chapter8;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class GreetingsActivity extends Activity {

    boolean isLoading= false;
    PacktAsyncHTTPClient client = new PacktAsyncHTTPClient();

    TextResponseHandler greetingsHandler = new TextResponseHandler(){

        @Override
        public void onFailure(HTTPResponse response) {
            Log.e("GreetingsActivity","Server returned error:  "+response.mResponseCode+" "+response.mResponseMessage);
            Toast.makeText(GreetingsActivity.this,
                    "Failed to retrieve the greeting content",
                    Toast.LENGTH_SHORT).show();
            dismissProgress();
        }

        @Override
        public void onError(Throwable error) {
            Log.e("GreetingsActivity","Exception happened retrieving greetings "+error.getMessage(),error);
            Toast.makeText(GreetingsActivity.this,
                    "Failed to retrieve the greeting content",
                    Toast.LENGTH_SHORT).show();
            dismissProgress();
        }

        @Override
        void onSuccess(String response) {
            EditText et =(EditText) findViewById(R.id.inputText);
            et.setText(response);
            dismissProgress();
        }
    };

    void dismissProgress(){
        isLoading = false;
        ProgressBar pb =(ProgressBar) findViewById(R.id.loading);
        pb.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greetings_layout);
        Button loadingBut = (Button) findViewById(R.id.retrieveBut);
        loadingBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoading){
                    ProgressBar pb =(ProgressBar) findViewById(R.id.loading);
                    pb.setVisibility(View.VISIBLE);

                    HTTPRequest.Builder builder = new HTTPRequest.Builder();
                    // Set the HTTP Verb to GET
                    builder.setVerb(HTTPRequest.Verb.GET);
                    // Sets location of the remote resource
                    builder.setUrl("http://demo1472539.mockable.io/greetings");
                    HTTPRequest request =  builder.build();
                    client.execute(request,greetingsHandler);
                }
            }
        });
    }


}
