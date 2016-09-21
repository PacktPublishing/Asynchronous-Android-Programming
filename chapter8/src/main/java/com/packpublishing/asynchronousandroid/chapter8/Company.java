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

@Root
@Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
public class Company {

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Name")
    public String name;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="CatchPhrase")
    public String catchPhrase;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Bs")
    public String bs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatchPhrase() {
        return catchPhrase;
    }

    public void setCatchPhrase(String catchPhrase) {
        this.catchPhrase = catchPhrase;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }
}
