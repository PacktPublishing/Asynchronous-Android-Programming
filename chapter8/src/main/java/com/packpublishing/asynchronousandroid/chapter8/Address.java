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
public class Address {

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Street")
    public String street;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Suite")
    public String suite;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="City")
    public String city;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="ZipCode")
    public String zipcode;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Geo")
    public Geo geo;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }


}
