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

public class BodyFactory {

    public static Body read(String mimeType, InputStream is) throws IOException {

        Body result = null;

        if ( mimeType.startsWith("text") ) {
            result = new TextPlainBody(mimeType);
            result.consume(is);
        } else if (  mimeType.startsWith("application/json") || mimeType.startsWith("application/xml")){
            result = new RawBody(mimeType);
            result.consume(is);
        }

        return result;
    }
}
