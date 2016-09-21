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

import android.util.Log;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XMLConverter<POJO>  implements BodyEncoder<POJO>, BodyDecoder<POJO> {

    private final Class<POJO> clazz;

    XMLConverter(Class<POJO> clazz){
        this.clazz = clazz;
    }

    @Override
    public Body encode(POJO obj,String mimeType)  throws Exception {
        Serializer serializer = new Persister();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        serializer.write(obj, output);
        RawBody body = new RawBody(mimeType);
        output.close();
        body.setContent(output.toByteArray());
        return body;
    }


    @Override
    public POJO decode(Body body) throws Exception {
        Serializer serializer = new Persister();
        InputStream is =null;
        RawBody rawBody = (RawBody)body;
        POJO obj = null;
        try {
            is= new ByteArrayInputStream(rawBody.getContent());
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(is));
            obj = (POJO) serializer.read(clazz,bfReader);
            Log.i("asd", "asd");
        } finally {
            if(is != null) is.close();
        }
        return obj;
    }


}
