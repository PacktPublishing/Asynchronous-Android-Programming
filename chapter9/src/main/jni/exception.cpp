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
#include "com_packpublishing_asynchronousandroid_chapter9_ExceptionActivity.h"

#ifdef __cplusplus
extern "C" {
#endif

void Java_com_packpublishing_asynchronousandroid_chapter9_ExceptionActivity_genException
        (JNIEnv *jniEnv, jobject activityObj, jobject byteBuffer) {

    jclass byteBufferClass = jniEnv->GetObjectClass(byteBuffer);

    jmethodID getMid = jniEnv->GetMethodID(byteBufferClass, "get", "(I)B");

    // Trying to access a buffer position out of the buffer capacity
    jbyte byte = jniEnv->CallByteMethod(byteBuffer, getMid, 32);

    //
    if (jniEnv->ExceptionCheck()) {
        // Prints the exception  on the standard Error
        jniEnv->ExceptionDescribe();
        // Clears the exception
        jniEnv->ExceptionClear();

        jclass exceptionClass = jniEnv->FindClass("java/lang/RuntimeException");
        jniEnv->ThrowNew(exceptionClass, "Failed to get byte from buffer");

        jniEnv->DeleteLocalRef(exceptionClass);
        jniEnv->DeleteLocalRef(byteBufferClass);
        return;
    }
    // Do Stuff

    jniEnv->DeleteLocalRef(byteBufferClass);

}

#ifdef __cplusplus
}
#endif
