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
#include "com_packpublishing_asynchronousandroid_chapter9_GrayImageLoader.h"
#include <android/bitmap.h>
#include <android/log.h>

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,"ToGrayImageLoader",__VA_ARGS__)


#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    uint8_t red;
    uint8_t green;
    uint8_t blue;
    uint8_t alpha;
} rgba;

/*
 * Class:     com_packpublishing_asynchronousandroid_chapter9_ToGrayImageLoader
 * Method:    convertImageToGray
 * Signature: (Landroid/graphics/Bitmap;)Landroid/graphics/Bitmap;
 */

jobject Java_com_packpublishing_asynchronousandroid_chapter9_GrayImageLoader_convertImageToGray
        (JNIEnv *env, jobject obj, jobject bitmap) {

    AndroidBitmapInfo info;
    void *pixels;
    int ret;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        jclass clazz = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(clazz, "Failed to get Information from the Bitmap!");
        return 0;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void **) &pixels)) < 0) {
        jclass clazz = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(clazz, "Failed to lock Bitmap pixels !");
        return 0;
    }

    rgba *rgba_pixels = (rgba *) pixels;

    for (int i = 0; i < (info.width * info.height); i++) {
        uint8_t red = rgba_pixels[i].red;
        uint8_t green = rgba_pixels[i].green;
        uint8_t blue = rgba_pixels[i].blue;
        uint8_t gray = red * 0.3 + green * 0.59 + blue * 0.11;
        rgba_pixels[i].red = gray;
        rgba_pixels[i].green = gray;
        rgba_pixels[i].blue = gray;
    }

    AndroidBitmap_unlockPixels(env, bitmap);

    return bitmap;
}
#ifdef __cplusplus
}
#endif