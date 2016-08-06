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

import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SSLOptions {

    enum CipherSuite{

        DEFAULT("Default"),  // Supported on Android API Level > 9
        SSL("SSL"), // Supported on Android API Level > 9
        SSLv3("SSLv3"), // Supported on Android API Level > 9
        TLS("TLS"), // Supported on Android API Level > 1
        TLSv1("TLSv1"), // Supported on Android API Level > 1+
        TLSv1_1("TLSv1.1"), // Supported on Android API Level > 16+
        TLSv1_2("TLSv1.2"); // Supported on Android API Level > 16+

        private final String cipherSuite;

        private CipherSuite(String value) {
            cipherSuite = value;
        }

        public String toString() {
            return cipherSuite;
        }
    }

    final SSLContext sslContext;
    final CipherSuite cipherSuite;
    private final  String keyStore;
    private final String keyStorePassword;
    private final String trustStore;
    private final String trustStorePassword;

    public SSLOptions(Context ctx,Builder builder) throws Exception {

        cipherSuite = builder.cipherSuite;
        keyStore = builder.keyStore;
        keyStorePassword = builder.keyStorePassword;
        trustStore = builder.trustStore;
        trustStorePassword = builder.trustStorePassword;
        sslContext = initSSLContext(ctx);
    }


    KeyManagerFactory getKeyManagerFactory(Context ctx) throws Exception{

        KeyManagerFactory kmf = null;
        // Initialize Key store
        if ( keyStore !=null ) {

            InputStream keyStoreIs = ctx.getResources().getAssets().open(keyStore);
            String algorithm = KeyManagerFactory.getDefaultAlgorithm();
            kmf = KeyManagerFactory.getInstance(algorithm);
            KeyStore ks = KeyStore.getInstance("BKS");
            ks.load(keyStoreIs, keyStorePassword.toCharArray());
            kmf.init(ks,keyStorePassword.toCharArray());
        }
        return kmf;
    }

    TrustManagerFactory getTrustManagerFactory(Context ctx) throws Exception{

        TrustManagerFactory tmf = null;
        // Initialize Trust store
        if ( trustStore !=null) {
            InputStream keyStoreIs = ctx.getResources().getAssets().open(trustStore);
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(algorithm);
            KeyStore ts = KeyStore.getInstance("BKS");
            ts.load(keyStoreIs, trustStorePassword.toCharArray());
            tmf.init(ts);
        }
        return tmf;
    }



    private  SSLContext initSSLContext(Context ctx)  throws Exception {

        KeyManagerFactory kmf = getKeyManagerFactory(ctx);

        TrustManagerFactory tmf = getTrustManagerFactory(ctx);

        SSLContext result= SSLContext.getInstance(cipherSuite.toString());

        result.init( kmf != null? kmf.getKeyManagers() : null ,
                tmf!=null? tmf.getTrustManagers():null,
                new SecureRandom());

        return result;
    }

    public static class Builder {
        private CipherSuite cipherSuite = CipherSuite.DEFAULT;
        private String keyStore = null;
        private String keyStorePassword =null;
        private String trustStore = null;
        private String trustStorePassword= null;

        public Builder(){}

        Builder setCipherSuite(CipherSuite cipherSuite){
            this.cipherSuite= cipherSuite;
            return this;
        }

        Builder setTrustStore(String trustStorePath,String password){
            this.trustStore = trustStorePath;
            this.trustStorePassword = password;
            return this;
        }

        Builder setKeyStore(String keyStorePath,String password){
            this.keyStore = keyStorePath;
            this.keyStorePassword = password;
            return this;
        }

        SSLOptions build(Context ctx) throws Exception {
            return new SSLOptions(ctx,this);
        }
    }

}
