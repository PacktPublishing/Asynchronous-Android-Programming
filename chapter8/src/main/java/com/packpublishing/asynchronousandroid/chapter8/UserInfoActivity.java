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
import android.widget.TextView;

public class UserInfoActivity extends Activity {

    XMLResponseHandler<GetUserInfoResponse,Error> xmlResponseHandler =
            new XMLResponseHandler<GetUserInfoResponse, Error>(GetUserInfoResponse.class,Error.class) {

        @Override
        public void onSuccess(GetUserInfoResponse getUserInfoResponse) {
            TextView nameTv = (TextView)findViewById(R.id.nameValue);
            TextView emailTv = (TextView)findViewById(R.id.emailValue);
            TextView usernameTv = (TextView)findViewById(R.id.usernameValue);
            TextView phoneTv = (TextView)findViewById(R.id.phoneValue);
            TextView websiteTv = (TextView)findViewById(R.id.websiteValue);
            nameTv.setText(getUserInfoResponse.getUser().name);
            emailTv.setText(getUserInfoResponse.getUser().email);
            usernameTv.setText(getUserInfoResponse.getUser().username);
            phoneTv.setText(getUserInfoResponse.getUser().phone);
            websiteTv.setText(getUserInfoResponse.getUser().website);
        }

        @Override
        public void onFailure(Error response) {
            Log.e("UserInfoActivity","Failure from server "+response.toString());
        }

        @Override
        public void onError(Throwable error) {
            Log.e("UserInfoActivity", "Exception: " + error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_layout);

        HTTPRequest.Builder builder = new HTTPRequest.Builder();
        // Set the HTTP Verb to GET
        builder.setVerb(HTTPRequest.Verb.POST);
        // Sets location of the remote resource
        builder.setUrl("http://demo1472539.mockable.io/GetUserInfo");

        builder.addHeader(new Header("Accept","application/xml"));

        GetUserInfo gui = new GetUserInfo();
        gui.setUserId("123");
        try {
            Body body = new XMLConverter<GetUserInfo>(GetUserInfo.class)
                          .encode(gui, "application/xml");
            builder.setBody(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HTTPRequest request =  builder.build();

        PacktAsyncHTTPClient client = new PacktAsyncHTTPClient();
        client.execute(request, xmlResponseHandler);
    }
}
