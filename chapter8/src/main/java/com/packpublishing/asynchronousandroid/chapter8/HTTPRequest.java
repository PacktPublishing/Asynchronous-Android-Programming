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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HTTPRequest {

        public enum Verb {
            POST,
            GET,
            PATCH,
            UPDATE,
            DELETE,
        };

        public enum CachePolicy {
            DEFAULT,
            NO_CACHE,
            ONLY_IF_CACHED,
            RESET_MAX_AGE,
            DISABLED
        }

        final String mVerb;
        final String mUrl;
        final List<Header> mHeaders;
        final Map<String, String> mParameters;
        final  Body mBody;
        final int mReadTimeout;
        final int mConnectTimeout;
        final SSLOptions mSSLOptions;
        final HTTPProxy mProxy;
        final CachePolicy mCachePolicy;

        public HTTPRequest(Builder builder){
            this.mVerb= toString(builder.mVerb);
            this.mUrl= builder.mUrl;
            this.mHeaders= builder.mHeaders;
            this.mParameters= builder.mParameters;
            this.mBody = builder.mBody;
            this.mReadTimeout = builder.mReadTimeout;
            this.mConnectTimeout = builder.mConnectTimeout;
            this.mSSLOptions = builder.mSSLOptions;
            this.mProxy = builder.mProxy;
            this.mCachePolicy =  builder.mCachePolicy;
        }


        String toString(Verb verb){
            switch (verb){
                case POST: return "POST";
                case GET: return "GET";
                case PATCH: return "PATCH";
                case UPDATE: return "UPDATE";
                case DELETE: return "DELETE";
            }
            return "";
        }
        public static class Builder {

            private Verb mVerb;
            private String mUrl;
            private List<Header> mHeaders=  new LinkedList<Header>();
            private Map<String, String> mParameters = new HashMap<String, String>();
            private Body mBody;
            private int mReadTimeout;
            private int mConnectTimeout;
            private SSLOptions mSSLOptions =null;
            private HTTPProxy mProxy=null;
            private CachePolicy mCachePolicy=CachePolicy.DEFAULT;

            public void setCachePolicy(CachePolicy mCachePolicy) {
                this.mCachePolicy = mCachePolicy;
            }



            public Builder setVerb(SSLOptions sslOptions) {
                this.mSSLOptions = sslOptions;
                return this;
            }

            public Builder setVerb(Verb mVerb) {
                this.mVerb = mVerb;
                return this;
            }

            public Builder setUrl(String mUrl) {
                this.mUrl = mUrl;
                return this;
            }
            public Builder setProxy(HTTPProxy proxy){
                mProxy = proxy;
                return this;
            }

            public Builder addHeader(Header header) {
                this.mHeaders.add(header);
                return this;
            }

            public Builder setParameter(String name,String value) {
                this.mParameters.put(name, value);
                return this;
            }

            public Builder setBody(Body body) {
                this.mBody = body;
                return this;
            }

            public Builder setSSLOptions(SSLOptions options){
                this.mSSLOptions = options;
                return this;
            }

            HTTPRequest build(){
                return new HTTPRequest(this);
            }

            public void setConnectTimeout(int connectTimeoutMs) {
                this.mConnectTimeout = connectTimeoutMs;
            }
            public void setReadTimeout(int readTimeoutMs) {
                this.mReadTimeout = readTimeoutMs;
            }
        }

}
