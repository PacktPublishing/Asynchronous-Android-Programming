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
package com.packpublishing.asynchronousandroid.chapter9;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class MyNativeActivity extends Activity {

    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("mylib");
    }

    protected EditText inputTextEt = null;
    protected TextView charCountTv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_jni);
        TextView cTv = (TextView) findViewById(R.id.helloFromC);
        TextView cPlusTv = (TextView) findViewById(R.id.helloFromCPlusPlus);
        cTv.setText(isPrime(2) ? "true" : "false");
        cPlusTv.setText(isPrimeCPlusPlus(4));

        inputTextEt = (EditText) findViewById(R.id.inputText);
        charCountTv = (TextView) findViewById(R.id.charCount);

        inputTextEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateWordCounter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private native void updateWordCounter(String s);

    public native boolean isPrime(int number);

    public native String isPrimeCPlusPlus(int number);
}
