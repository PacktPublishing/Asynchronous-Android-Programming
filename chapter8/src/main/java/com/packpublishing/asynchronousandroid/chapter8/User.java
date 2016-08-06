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
public class User {

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Id")
    public int id;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Name")
    public String name;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Username")
    public String username;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Email")
    public String email;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Phone")
    public String phone;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Website")
    public String website;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Address")
    public Address address;

    @Namespace(prefix="p", reference="https://www.packtpub.com/asynchronous_android")
    @Element(name="Company")
    public Company company;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
