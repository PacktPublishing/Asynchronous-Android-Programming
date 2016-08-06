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
package com.packpublishing.asynchronousandroid.chapter12;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;


public class SubjectActivity extends Activity {

    public final String TAG = this.getClass().getSimpleName();

    void pubSubject(){
        PublishSubject<Integer> pubSubject = PublishSubject.create();
        pubSubject.onNext(1);
        pubSubject.onNext(2);
        Subscription subscription = pubSubject.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                Log.i(TAG, "Observer subscribed to PubSubject");
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                Log.i(TAG, "Observer unsubscribed to PubSubject ");
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "New Event received from PubSubject: " + integer);
            }
        });
        pubSubject.onNext(3);
        pubSubject.onNext(4);
        subscription.unsubscribe();
        pubSubject.onNext(5);
        pubSubject.onCompleted();
    }


    void replaySubject(){
        ReplaySubject<Integer> replaySub = ReplaySubject.create();
        replaySub.onNext(1);
        replaySub.onNext(2);
        Subscription subscription = replaySub.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                Log.i(TAG, "Observer subscribed to ReplaySubject");
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                Log.i(TAG, "Observer unsubscribed to ReplaySubject ");
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "New Event received from ReplaySubject: " + integer);
            }
        });
        replaySub.onNext(3);
        replaySub.onNext(4);
        subscription.unsubscribe();
        replaySub.onNext(5);
        replaySub.onCompleted();
    }

    void behaviourSubject(){
        Action1<Integer> onNextFuc= new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "New Event received on behaviourSubject: " + integer);
            }
        };
        BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create();
        behaviorSubject.onNext(1);
        behaviorSubject.onNext(2);
        Subscription subscription = behaviorSubject.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                Log.i(TAG, "Observer subscribed to behaviorSubject");
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                Log.i(TAG, "Observer subscribed to behaviorSubject");
            }
        }).subscribe(onNextFuc);
        behaviorSubject.onNext(3);
        behaviorSubject.onNext(4);
        subscription.unsubscribe();
        behaviorSubject.onNext(5);
        behaviorSubject.onCompleted();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_activity);
        pubSubject();
        replaySubject();
        behaviourSubject();
    }
}
