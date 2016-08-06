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

import java.lang.reflect.Type;

public abstract class JsonResponseHandler<ResponseType, ErrorType> extends ResponseHandler {

    private final Type responseType;

    private final Type errorType;

    JsonResponseHandler(Type responseType,
                        Type errorClass){
        this.responseType = responseType;
        this.errorType = errorClass;
    }

    // Callback invoked with the converted U object instance
    abstract public void onSuccess(ResponseType response) ;

    // Callback invoked with the converted U object instance
    abstract public void onFailure(ErrorType response);


    @Override
    public void onSuccess(HTTPResponse response) {
        RawBody body = (RawBody)response.mBody;

        if ( body != null ) {
            ResponseType obj = null;
            try {
                obj = new JSONConverter<ResponseType>(responseType).
                        decode(body);
                onSuccess(obj);
            } catch (Exception e) {
                onError(e);
            }
        } else {
            onSuccess((ResponseType)null);
        }
    }

    @Override
    public void onFailure(HTTPResponse response) {
        RawBody body = (RawBody)response.mBody;
        if ( body != null ){
            ErrorType obj =null;
            try {
                obj = new JSONConverter<ErrorType>(errorType)
                        .decode(body);

            } catch (Exception e) {
                onError(e);
            }
        } else{
            onFailure((ErrorType)null);
        }
    }

}
