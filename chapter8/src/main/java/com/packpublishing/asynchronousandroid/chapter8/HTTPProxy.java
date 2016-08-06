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

import java.net.Proxy.Type;

public class HTTPProxy {

    final String mHost;
    final int mPort;
    final String mUsername;
    final String mPassword;
    final ProxyType mType;


    public HTTPProxy(Builder builder) {
        this.mHost=builder.mHost;
        this.mPort=builder.mPort;
        this.mUsername=builder.mUsername;
        this.mPassword=builder.mPassword;
        this.mType=builder.mType;
    }

    enum ProxyType {
        SOCKS,
        HTTP;

        Type toType(){
            switch (this){
                case SOCKS: return Type.SOCKS;
                case HTTP: return Type.HTTP;
            }
            return  Type.DIRECT;
        }
    }
    public static class Builder{
        public Builder setHost(String mHost) {
            this.mHost = mHost;
            return this;
        }

        public Builder setPort(int mPort) {
            this.mPort = mPort;
            return this;
        }

        public Builder setUsername(String mUsername) {
            this.mUsername = mUsername;
            return this;
        }

        public Builder setPassword(String mPassword) {
            this.mPassword = mPassword;
            return this;
        }

        public Builder setType(ProxyType type) {
            this.mType = type;
            return this;
        }
        HTTPProxy build(){
            return new HTTPProxy(this);
        }


        String mHost;
        int mPort;
        String mUsername;
        String mPassword;
        ProxyType mType;
    }
}
