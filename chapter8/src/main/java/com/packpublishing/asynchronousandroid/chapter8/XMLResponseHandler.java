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

public abstract class XMLResponseHandler <Response, Error> extends ResponseHandler {

    private final Class<Response> responseClass;

    private final Class<Error> errorClass;

    XMLResponseHandler(Class<Response> responseClass,
                       Class<Error> errorClass){
        this.responseClass = responseClass;
        this.errorClass = errorClass;
    }

    // Callback invoked with the converted U object instance
    abstract public void onSuccess(Response response) ;

    // Callback invoked with the converted U object instance
    abstract public void onFailure(Error response);

    @Override
    public void onSuccess(HTTPResponse response) {
        RawBody body = (RawBody)response.mBody;

        if ( body != null ) {
            Response obj = null;
            try {
                obj = new XMLConverter<Response>(responseClass).
                        decode(body);
            } catch (Exception e) {
                onError(e);
            }
            onSuccess(obj);
        } else {
            onSuccess((Response)null);
        }

    }

    @Override
    public void onFailure(HTTPResponse response) {
        RawBody body = (RawBody)response.mBody;
        if ( body != null ){
            Error obj =null;
            try {
                obj = new XMLConverter<Error>(errorClass)
                        .decode(body);

            } catch (Exception e) {
                onError(e);
            }
        } else{
            onFailure((Error)null);
        }
    }

}
