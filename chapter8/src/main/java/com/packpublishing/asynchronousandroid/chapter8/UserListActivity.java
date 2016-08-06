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
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends Activity {


    JsonResponseHandler<List<User>,Error> jsonResponseHandler =
            new JsonResponseHandler<List<User>,Error>(
                    new TypeToken<ArrayList<User>>() {}.getType(),
                    new TypeToken<Error>() {}.getType()){

        @Override
        public void onError(Throwable error) {
            Log.e("GreetingsActivity",
                    "Exception happened retrieving greetings "+error.getMessage(),error);
        }

        @Override
        public void onSuccess(List<User> users) {

            ListView listView = (ListView)findViewById(R.id.usersList);
            ListAdapter adapter = new UserListAdapter(UserListActivity.this,users);
            listView.setAdapter(adapter);
        }

        @Override
        public void onFailure(Error response) {
            Log.e("GreetingsActivity",
                    "Exception happened retrieving greetings "+response.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_layout);

        HTTPRequest.Builder builder = new HTTPRequest.Builder();
        // Set the HTTP Verb to GET
        builder.setVerb(HTTPRequest.Verb.GET);
        // Sets location of the remote resource
        builder.setUrl("http://jsonplaceholder.typicode.com/users");
        HTTPRequest request =  builder.build();

        PacktAsyncHTTPClient client = new PacktAsyncHTTPClient();
        client.execute(request, jsonResponseHandler);

    }

}
