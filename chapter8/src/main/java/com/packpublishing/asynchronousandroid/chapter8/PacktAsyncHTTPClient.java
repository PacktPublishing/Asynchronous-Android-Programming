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

import android.os.AsyncTask;
import android.util.Base64;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class PacktAsyncHTTPClient implements AsyncHTTPClient {

    @Override
    public void execute(HTTPRequest request, ResponseHandler handler)  {
        new HTTPAsyncTask(handler).execute(request);
    }

    public class HTTPAsyncTask extends AsyncTask<HTTPRequest,Void,Result<HTTPResponse>>{

        // Response Handler to be invoked later
        // on the UI Thread
        final ResponseHandler mHandler;

        public  HTTPAsyncTask(ResponseHandler handler){
            this.mHandler = handler;
        }

        void fillHeaders(Map<String,List<String>> headers, HTTPResponse.Builder builder){
            for(String name : headers.keySet()){
                for(String value : headers.get(name)){
                    builder.addHeader(new Header(name,value));
                }
            }
        }
        void setRequestHeaders(HttpURLConnection con,HTTPRequest request ){
            for (Header header: request.mHeaders) {
                con.addRequestProperty(header.getName(),header.getValue());
            }
        }

        void setRequestBody(HttpURLConnection con,HTTPRequest request) throws IOException {
            if ( request.mBody!=null ) {
                // Allows Sending data on the request
                con.setDoOutput(true);
                con.setRequestProperty("Content-type",request.mBody.getMimeType() );
                OutputStream os = con.getOutputStream();
                request.mBody.write(os);
            }
        }

        HttpURLConnection setupProxyConnection(URL url,HTTPProxy httpProxy) throws IOException {
            HttpsURLConnection con = null;

            Proxy proxy = new Proxy( httpProxy.mType.toType(),
                                     new InetSocketAddress(httpProxy.mHost,
                                     httpProxy.mPort));

            con =  (HttpsURLConnection) url.openConnection(proxy);

            if ( httpProxy.mUsername !=  null ) {
                String authString = httpProxy.mUsername + ":" + httpProxy.mPassword;
                String encoded = Base64.encodeToString(authString.getBytes(), 0);
                con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
            }
            return con;
        }

        void applySSLContext(HTTPRequest request,HttpsURLConnection con){

            // Initialize the SSL Session with your own keystore and truststore
            if( request.mSSLOptions != null ) {
                SSLContext ctx = request.mSSLOptions.sslContext;
                con.setSSLSocketFactory(ctx.getSocketFactory());
                con.setHostnameVerifier(new AcceptAllHostNameVerifier());
            }
        }

        HttpURLConnection initConnection( HTTPRequest request,URL url) throws IOException {

            HttpURLConnection result;
            HTTPProxy httpProxy = request.mProxy;

            if ( httpProxy != null ) {
                // Setup a connection through a proxy
                result = (HttpURLConnection) setupProxyConnection(url,httpProxy);
            } else {
                // Setup a direct connection
                result =  (HttpURLConnection) url.openConnection();
            }

            if ( url.getProtocol().equals("https") ){
                HttpsURLConnection con = (HttpsURLConnection)result;
                // Apply our SSL Options to the connection
                if( request.mSSLOptions != null ) {
                    applySSLContext(request,con);
                }
            }
            return result;
        }

        @Override
        protected Result<HTTPResponse> doInBackground(HTTPRequest... params) {

            HTTPRequest request = params[0];
            Body body = null;
            HttpURLConnection conn =null;
            Result<HTTPResponse> response = new Result<HTTPResponse>();
            try {

                // Retrieve the request URL from the request object
                URL url = new URL(request.mUrl);

                // Opens up the connection to the remote pper
                conn = (HttpURLConnection)initConnection(request,url);

                setCacheOptions(conn,request);

                conn.setReadTimeout(request.mReadTimeout);

                conn.setConnectTimeout(request.mConnectTimeout);

                // Set the HTTP Request verb
                conn.setRequestMethod(request.mVerb);

                // Allows Receiving data on the response
                conn.setDoInput(true);

                // Set the Request Headers
                setRequestHeaders(conn,request);
                // Set and write the Request Body
                setRequestBody(conn,request);

                // Retrieve the response code
                int responseCode = conn.getResponseCode();

                // Build the HTTP Response Object
                HTTPResponse.Builder builder = new HTTPResponse.Builder()
                        .setResponseCode(responseCode)
                        .setResponseMessage(conn.getResponseMessage());

                // Fill the HTTP Response Headers
                fillHeaders(conn.getHeaderFields(),builder);

                // Read the Body from the Connection Input Stream
                body = BodyFactory.read(conn.getContentType(),
                            conn.getInputStream());
                builder.setBody(body);
                // Build the HTTP Response Object
                response.obj = builder.build();
            } catch (Exception e) {
                response.error = e;
            } finally {
                if (conn !=null){
                    conn.disconnect();
                }
            }
            return response;
        }

        private void setCacheOptions(HttpURLConnection conn,HTTPRequest request) {
            switch (request.mCachePolicy){
                case DEFAULT:
                    break;
                case NO_CACHE:
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    break;
                case ONLY_IF_CACHED:
                    conn.setRequestProperty("Cache-Control", "only-if-cached");
                case RESET_MAX_AGE:
                    conn.setRequestProperty("Cache-Control", "max-age=0");
                case DISABLED:
                    conn.setUseCaches(false);
                    break;
            }
        }

        protected void onPostExecute(Result<HTTPResponse> result) {

            if(result.error !=null){
                mHandler.onError(result.error);
            } else if (result.obj.mResponseCode == HttpURLConnection.HTTP_OK){
                mHandler.onSuccess(result.obj);
            } else {
                mHandler.onFailure(result.obj);
            }

        }
    }

}
