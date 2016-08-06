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
#include "com_packpublishing_asynchronousandroid_chapter9_MyNativeActivity.h"

/*
 * Class:     com_packpublishing_asynchronousandroid_chapter9_MyNativeActivity
 * Method:    helloJNI
 * Signature: ()Ljava/lang/String;
 */

#ifdef __cplusplus
extern "C" {
#endif

jboolean Java_com_packpublishing_asynchronousandroid_chapter9_MyNativeActivity_isPrime
        (JNIEnv *env, jobject obj, jint number) {
    int c;

    for (c = 2; c < number ; c++) {
        if (number % c == 0)
            return JNI_FALSE;
    }
    return JNI_TRUE;

}

#ifdef __cplusplus
}
#endif