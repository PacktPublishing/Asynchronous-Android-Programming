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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class JSONConverter<POJO> implements BodyEncoder<POJO>, BodyDecoder<POJO> {

    private final Type pojoType;

    JSONConverter(Type pojoType) {
        this.pojoType = pojoType;
    }

    @Override
    public POJO decode(Body body) throws Exception {
        Gson gson = new Gson();
        RawBody rawBody = (RawBody) body;
        InputStream is = null;
        POJO obj = null;
        try {
            is = new ByteArrayInputStream(rawBody.getContent());
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(is));
            obj = gson.fromJson(bfReader, pojoType);
        } finally {
            if (is != null) is.close();
        }
        return obj;
    }

    @Override
    public Body encode(POJO obj, String mimeType) throws Exception {
        Gson gson = new Gson();
        String result = gson.toJson(obj);
        RawBody body = new RawBody(mimeType);
        body.setContent(result.getBytes());
        return body;
    }
}
