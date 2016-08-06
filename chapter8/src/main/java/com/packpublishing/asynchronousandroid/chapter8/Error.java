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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
@Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
public class Error {

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="ResultCode")
    public int resultCode;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="ResultMessage")
    public String  resultMessage;

    @Override
    public String toString() {
        return "Error{" +
                "resultCode=" + resultCode +
                ", resultMessage='" + resultMessage + '\'' +
                '}';
    }
}
