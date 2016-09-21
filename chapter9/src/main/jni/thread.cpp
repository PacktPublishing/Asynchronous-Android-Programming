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
#include "./thread.h"
#include <iostream>
#include <sstream>
#include <ctime>
#include <ratio>
#include <chrono>

// Java VM interface pointer
static JavaVM *gVm = NULL;
static jclass cpuStatClass;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    // Cache the JavaVM interface pointer
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_4);
    gVm = vm;
    cpuStatClass = (jclass) env->NewGlobalRef(
            env->FindClass("com/packpublishing/asynchronousandroid/chapter9/CPUStat"));
    return JNI_VERSION_1_4;
}

jclass getCPUStatClass() {
    return cpuStatClass;
}

class JLocalRef {

    jobject ref;
    JNIEnv *env;

    JLocalRef(JNIEnv *env_, jobject &ref_) :
            env(env),
            ref(env->NewLocalRef(ref_)) { }

    ~JLocalRef() {
        env->DeleteLocalRef(ref);
    }

    inline jobject getRef() { return ref; };

};

const int HEALTHECK_MESSAGE = 0;

class HealthCheckThread : public JavaThread {

    jobject handlerObj;

public:
    HealthCheckThread(JNIEnv *env_, jobject handlerObj_) :
            JavaThread(),
            handlerObj(env_->NewGlobalRef(handlerObj_)) { }

    virtual void onDetach() {
        jniEnv()->DeleteGlobalRef(handlerObj);
    }

    void sendHealthMessage() {

        // Get the Handler.obtainMessage and call it
        jclass handlerClass = jniEnv()->FindClass("android/os/Handler");
        jmethodID obtainMId = jniEnv()->GetMethodID(handlerClass,
                                                    "obtainMessage",
                                                    "(ILjava/lang/Object;)Landroid/os/Message;");
        // Get the current time
        std::ostringstream oss;
        using std::chrono::system_clock;
        system_clock::time_point today = system_clock::now();
        std::time_t tt;
        tt = system_clock::to_time_t(today);

        // Build up the alive message
        oss << "Thread[" << std::this_thread::get_id() << "] is alive at " << ctime(&tt) <<
        std::endl;;

        // Build up a Handler Message
        jobject messagObj = jniEnv()->CallObjectMethod(handlerObj, obtainMId, 0,
                                                       jniEnv()->NewStringUTF(
                                                               oss.str().c_str()));

        // Send the Message to the main UI Thread
        jmethodID sendMsgMId = jniEnv()->GetMethodID(handlerClass, "sendMessage",
                                                     "(Landroid/os/Message;)Z");

        bool sent = jniEnv()->CallBooleanMethod(handlerObj, sendMsgMId, messagObj);

        jniEnv()->DeleteLocalRef(messagObj);
        jniEnv()->DeleteLocalRef(handlerClass);
    }

    virtual void run() {

        while (!shouldStop) {
            std::unique_lock<std::mutex> lck(stopMutex);
            // Do Work
            // ...
            sendHealthMessage();
            // Wait until a stop signal is sent
            stopCond.wait_for(lck, std::chrono::seconds(1));
        }
    }

};

JavaThread::JavaThread() {}



void JavaThread::stop() {
    std::unique_lock<std::mutex> lck(stopMutex);
    this->shouldStop = true;
    stopCond.notify_all();
}


void JavaThread::entryPoint() {

    // Attach current thread to Java virtual machine
    // and obrain JNIEnv interface pointer
    {
        std::unique_lock<std::mutex> lck(startMutex);
        LOGI("Attaching the thread to JVM ");

        if (gVm->AttachCurrentThread(&threadEnv, NULL) != 0) {
            jclass clazz = threadEnv->FindClass("java/lang/RuntimeException");
            threadEnv->ThrowNew(clazz, "Failed to attach the thread to JVM");
        }
        isStarted = true;
        startCond.notify_all();
    }
    onAttach();

    try {
        // Run the subclass method

        run();
    } catch (...) {
        // Detach current thread when an exception happens
        onDetach();
        gVm->DetachCurrentThread();
        throw;
    }
    LOGI("Detaching the thread from JVM ");
    // Detach current thread from Java virtual machine
    onDetach();
    gVm->DetachCurrentThread();
}


void JavaThread::start(){
    //execution = std::thread(&JavaThread::entryPoint, this);
    thread_ = std::thread(&JavaThread::entryPoint, this);
    std::unique_lock<std::mutex> lck(startMutex);
    while (!isStarted) startCond.wait(lck);
}


#ifdef __cplusplus
extern "C" {
#endif


static const int num_threads = 10;
static JavaThread *threads[num_threads];

void Java_com_packpublishing_asynchronousandroid_chapter9_NativeThreadsActivity_startNativeThreads
        (JNIEnv *jEnv, jobject activity, jobject handler) {

    LOGI("Starting  %d Native Threads", num_threads);
    //Launch a group of threads
    for (int i = 0; i < num_threads; ++i) {
        threads[i] = new HealthCheckThread(jEnv, handler);
        threads[i]->start();
    }
}

JNIEXPORT void JNICALL Java_com_packpublishing_asynchronousandroid_chapter9_NativeThreadsActivity_stopNativeThreads
        (JNIEnv *env, jobject activity) {

    LOGI("Stopping %d Native Threads", num_threads);

    //Launch a group of threads
    for (int i = 0; i < num_threads; ++i) {
        threads[i]->stop();
        threads[i]->join();
        delete threads[i];
    }
}

#ifdef __cplusplus
}
#endif



