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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Body {

    final String mMimeType;

    public Body(String mimeType){
        this.mMimeType = mimeType;
    }

    public String getMimeType() {
        return mMimeType;
    }

    abstract void consume(InputStream is) throws IOException;

    abstract void write(OutputStream out) throws IOException ;


    protected byte[] read(InputStream is) throws IOException {

        byte[] result = null;
        int readBytes=0;
        try {

            if ( is!=null  ) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[128];
                int rc =0;
                while( (rc = is.read(buffer)) > 0){
                    baos.write(buffer, 0, rc);
                    readBytes+=rc;
                }
                result =  baos.toByteArray();
            }

        } finally {
            if (is != null) {
                is.close();
            }
        }
        return result;
    }

}
