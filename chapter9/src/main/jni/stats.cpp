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
#include "thread.h"

#include <unistd.h>
#include <ios>
#include <iostream>
#include <fstream>
#include <string>

//////////////////////////////////////////////////////////////////////////////
//
// process_mem_usage(double &, double &) - takes two doubles by reference,
// attempts to read the system-dependent data for a process' virtual memory
// size and resident set size, and return the results in KB.
//
// On failure, returns 0.0, 0.0



template<typename T>
T *getNativePtr(JNIEnv *env, jobject obj) {
    jclass c = env->GetObjectClass(obj);
    jfieldID nativePtrFID = env->GetFieldID(c, "nativePtr", "J");
    jlong nativePtr = env->GetLongField(obj, nativePtrFID);
    return reinterpret_cast<T *>(nativePtr);
}


void unsetNativePtr(JNIEnv *env, jobject obj) {
    jclass c = env->GetObjectClass(obj);
    jfieldID nativePtrFID = env->GetFieldID(c, "nativePtr", "J");
    env->SetLongField(obj, nativePtrFID, 0);
}


struct CPUStat {

    double vm;
    double rss;

    CPUStat(double &vm_, double &rss_) :
            vm(vm_), rss(rss_) { }

    ~CPUStat() {
        LOGI("Destroying CPU Stat");
    }
};

static const int RSS_REQUEST = 0;
static const int RSS_RESPONSE = 1;

class CPUStatThread : public JavaThread {

    jobject activityObj;

public:
    CPUStatThread(JNIEnv *env_, jobject activity_) :
            JavaThread(),
            activityObj(env_->NewGlobalRef(activity_)) { }


    virtual void onDetach(){
        jniEnv()->DeleteGlobalRef(activityObj);
    }

    void process_mem_usage(double &vm_usage, double &resident_set) {
        using std::ios_base;
        using std::ifstream;
        using std::string;

        vm_usage = 0.0;
        resident_set = 0.0;

        // 'file' stat seems to give the most reliable results
        //
        ifstream stat_stream("/proc/self/stat", ios_base::in);

        // dummy vars for leading entries in stat that we don't care about
        //
        string pid, comm, state, ppid, pgrp, session, tty_nr;
        string tpgid, flags, minflt, cminflt, majflt, cmajflt;
        string utime, stime, cutime, cstime, priority, nice;
        string O, itrealvalue, starttime;

        // the two fields we want
        //
        unsigned long vsize;
        long rss;

        stat_stream >> pid >> comm >> state >> ppid >> pgrp >> session >> tty_nr
        >> tpgid >> flags >> minflt >> cminflt >> majflt >> cmajflt
        >> utime >> stime >> cutime >> cstime >> priority >> nice
        >> O >> itrealvalue >> starttime >> vsize >> rss; // don't care about the rest

        stat_stream.close();

        long page_size_kb =
                sysconf(_SC_PAGE_SIZE) / 1024; // in case x86-64 is configured to use 2MB pages
        vm_usage = vsize / 1024.0;
        resident_set = rss * page_size_kb;
    }

    void sendRSSMessage(jobject &handlerObj) {

        double vm, rss;
        process_mem_usage(vm, rss);

        jclass cpuStatClass = getCPUStatClass();
        jmethodID cpuConstructor = jniEnv()->GetMethodID(cpuStatClass, "<init>", "(J)V");

        CPUStat *cpuStat = new CPUStat(vm, rss);
        jlong nativePtr = reinterpret_cast<jlong>(cpuStat);
        jobject cpusStat = jniEnv()->NewObject(cpuStatClass, cpuConstructor, nativePtr);

        // Get the Handler.obtainMessage and call it
        jclass handlerClass = jniEnv()->FindClass("android/os/Handler");
        jmethodID obtainMId = jniEnv()->GetMethodID(handlerClass, "obtainMessage",
                                                    "(ILjava/lang/Object;)Landroid/os/Message;");
        // Build up a Handler Message
        jobject messagObj = jniEnv()->CallObjectMethod(
                handlerObj, obtainMId, RSS_RESPONSE, cpusStat);

        // Send the Message to the main UI Thread
        jmethodID sendMsgMId = jniEnv()->GetMethodID(handlerClass, "sendMessage",
                                                     "(Landroid/os/Message;)Z");
        LOGI("Send an RSS Response %f - %f", rss, vm);
        bool sent = jniEnv()->CallBooleanMethod(handlerObj, sendMsgMId, messagObj);


        jniEnv()->DeleteLocalRef(cpusStat);
        jniEnv()->DeleteLocalRef(messagObj);
        jniEnv()->DeleteLocalRef(handlerClass);
    }

    void processMessage() {

        jclass activityClass = jniEnv()->GetObjectClass(activityObj);

        // Retrieve the QueueLock
        jfieldID queueLockFid = jniEnv()->GetFieldID(activityClass, "queueLock",
                                                     "Ljava/lang/Object;");
        jobject lockObj = jniEnv()->GetObjectField(activityObj, queueLockFid);

        // Retrieve the Handler
        jfieldID handlerFid = jniEnv()->GetFieldID(activityClass, "myHandler",
                                                   "Landroid/os/Handler;");
        jobject handlerObj = jniEnv()->GetObjectField(activityObj, handlerFid);

        jmethodID getNextRequestMid = jniEnv()->GetMethodID(activityClass, "getNextRequest", "()I");

        if (jniEnv()->MonitorEnter(lockObj) != 0) { }

        // Retrieve the next command request to be processed
        int requestCode = jniEnv()->CallIntMethod(activityObj, getNextRequestMid);

        if (jniEnv()->MonitorExit(lockObj) != 0) { }

        switch (requestCode) {
            case RSS_REQUEST:
                LOGI("Received a RSS Request");
                sendRSSMessage(handlerObj);
                break;
        }
        jniEnv()->DeleteLocalRef(activityClass);
        jniEnv()->DeleteLocalRef(lockObj);
        jniEnv()->DeleteLocalRef(handlerObj);

    }

    virtual void run() {

        while (!shouldStop) {
            std::unique_lock<std::mutex> lck(stopMutex);
            // Do Work
            // ...
            processMessage();
            // Wait until a stop signal is sent
            stopCond.wait_for(lck, std::chrono::milliseconds(30));
        }

    }
};


#ifdef __cplusplus
extern "C" {
#endif

static CPUStatThread *cpuStatThread = 0;

jlong Java_com_packpublishing_asynchronousandroid_chapter9_JCPUStat_getRSSMemory
        (JNIEnv *env, jobject obj) {
    CPUStat *stat = getNativePtr<CPUStat>(env, obj);
    LOGI("found an RSS Response %f ", stat->rss);
    return (jlong) stat->rss;

}

void Java_com_packpublishing_asynchronousandroid_chapter9_JCPUStat_dispose
        (JNIEnv *env, jobject obj) {
    CPUStat *stat = getNativePtr<CPUStat>(env, obj);
    if (stat != 0) {
        delete stat;
        unsetNativePtr(env, obj);
    }
}

void Java_com_packpublishing_asynchronousandroid_chapter9_StatsActivity_startCPUStatThread
        (JNIEnv *env, jobject activity) {


    if (cpuStatThread == 0) {
        LOGI("Starting The CPU Thread");
        cpuStatThread = new CPUStatThread(env, activity);
        cpuStatThread->start();
    }
}

void Java_com_packpublishing_asynchronousandroid_chapter9_StatsActivity_stopCPUStatThread
        (JNIEnv *, jobject activity) {

    if (cpuStatThread != 0) {
        LOGI("Stopping The CPU Thread");
        cpuStatThread->stop();
        cpuStatThread->join();
        delete cpuStatThread;
        cpuStatThread = 0;
    }
}

#ifdef __cplusplus
}
#endif