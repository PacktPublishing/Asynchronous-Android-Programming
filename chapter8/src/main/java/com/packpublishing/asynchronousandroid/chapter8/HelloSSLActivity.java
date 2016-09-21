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
import android.widget.TextView;

public class HelloSSLActivity  extends Activity {

    TextResponseHandler responseHandler = new TextResponseHandler(){

        @Override
        void onSuccess(String response) {
            TextView tv = (TextView)findViewById(R.id.response);
            tv.setText(response);
        }

        @Override
        public void onFailure(HTTPResponse response) {
            Log.e("HelloSSLActivity","Failed contact server "+response.mResponseMessage);
        }

        @Override
        public void onError(Throwable error) {
            Log.e("HelloSSLActivity","Exception happened during the http request  ",error);
        }
    };


    OnClickListener startSSLListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                HTTPRequest.Builder builder = new HTTPRequest.Builder();
                // Set the HTTP Verb to GET
                builder.setVerb(HTTPRequest.Verb.GET);
                // Sets location of the remote resource

                // Change the URL to use your Computer IP Address
                builder.setUrl("https://192.168.1.10:8443/hello_ssl");
                builder.addHeader(new Header("Accept", "text/plain"));

                SSLOptions.Builder sslBuilder = new SSLOptions.Builder();


                sslBuilder.setKeyStore("asynchronous_client.ks", "123qwe")
                        .setTrustStore("asynchronous_client.ks", "123qwe")
                        .setCipherSuite(SSLOptions.CipherSuite.TLS);

                builder.setSSLOptions(sslBuilder.build(getApplication()));

                /* Proxy Code  Example

                HTTPProxy proxy = new HTTPProxy.Builder().
                        setHost("myproxy.net").
                        setPort(8080).
                        setUsername("helder").
                        setPassword("123qwe").
                        build();

                builder.setProxy(proxy);*/

                HTTPRequest request = builder.build();

                PacktAsyncHTTPClient client = new PacktAsyncHTTPClient();
                client.execute(request, responseHandler);
            } catch (Exception e){
                Log.e("HelloSSLActivity","Failed to build the SSL Request",e);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_ssl_layout);
        Button but = (Button)findViewById(R.id.startSSL);
        but.setOnClickListener(startSSLListener);
    }
}
