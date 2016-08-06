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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.google.gson.Gson;

import org.json.JSONObject;

public class TextPlainBody  extends Body {

    private String mContent;

    public TextPlainBody(String mimeType){
        super(mimeType);
    }

    public String getContent() {
        Gson gson = new Gson();
        JSONObject object;
        return mContent;

    }
    @Override
    void consume(InputStream is) throws IOException {
        byte[] content = read(is);
        mContent = new String(content);
    }

    @Override
    void write(OutputStream out) throws IOException {
        out.write(mContent.getBytes());
    }
}
