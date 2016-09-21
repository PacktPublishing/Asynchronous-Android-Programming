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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HTTPResponse {

    final String mResponseMessage;
    final int mResponseCode;
    final List<Header> mHeaders;
    final  Body mBody;

    public HTTPResponse(Builder builder){
        this.mResponseCode= builder.mResponseCode;
        this.mHeaders= builder.mHeaders;
        this.mBody = builder.mBody;
        mResponseMessage= builder.mResponseMessage;
    }

    public static class Builder {

        private int mResponseCode;
        private String mResponseMessage;
        private List<Header> mHeaders = new LinkedList<Header>();
        private Body mBody;

        public Builder setResponseCode(int responseCode) {
            this.mResponseCode = responseCode;
            return this;
        }

        public Builder addHeader(Header header) {
            this.mHeaders.add(header);
            return this;
        }


        public Builder setResponseMessage(String message) {
            this.mResponseMessage=message;
            return this;
        }

        public Builder setBody(Body body) {
            this.mBody = body;
            return this;
        }


        HTTPResponse build(){
            return new HTTPResponse(this);
        }
    }
}
