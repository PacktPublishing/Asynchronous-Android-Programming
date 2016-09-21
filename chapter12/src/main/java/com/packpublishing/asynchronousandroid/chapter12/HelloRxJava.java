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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;


public class HelloRxJava extends Activity {

    public final String TAG = this.getClass().getSimpleName();

    View.OnClickListener fromALS = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            fromALSExample();
        }
    };

    View.OnClickListener createIntObs = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            createIntObservable();
        }
    };

    View.OnClickListener actionsObs = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            createActionsObs();
        }
    };

    void createActionsObs() {

        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {

                Log.i(TAG, "New message -" + s);
                logOnConsole("New message -" + s);
            }
        };

        Action1<Throwable> onError = new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                Log.e(TAG, "Error - " + t.getMessage(), t);
                logOnConsole("Error - " + t.getMessage());

            }
        };

        Action0 onComplete = new Action0() {

            @Override
            public void call() {

                Log.i(TAG, "Rx Java events completed");
                logOnConsole("Rx Java events completed");
            }
        };

        Observable<String> myObservable =
                Observable.just("Hello from RxJava", "Welcome...", "Goodbye");

        myObservable.subscribe(onNextAction, onError, onComplete);
    }

    void logOnConsole(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView console = (TextView) findViewById(R.id.consoleTv);
                console.setText(console.getText() + "\n" + message);
            }
        });
    }


    void fromALSExample() {
        Observable<String> myObservable =
                Observable.from(Arrays.asList("Hello from RxJava",
                        "Welcome...",
                        "Goodbye"));

        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

                Log.i(TAG, "Rx Java events completed");
                logOnConsole("Rx Java events completed");
            }
            @Override
            public void onError(Throwable e) {

                Log.e(TAG, "Error found processing stream", e);
                logOnConsole("Error found processing stream "+e.getMessage());
            }
            @Override
            public void onNext(String s) {

                Log.i(TAG, "New message -" + s);
                logOnConsole("New message -" + s);
            }
        };

        myObservable.subscribe(mySubscriber);
    }


    void flatMapExample() {

        String content = "This is may example " +
                "We are looking for lines with the word RxJava " +
                "Counting the lines without RxJava on the content " +
                "We are finished.";

        Observable
           .just(content)
           .flatMap(new Func1<String, Observable<String>>() {
                @Override
                public Observable<String> call(final String content) {
                    return Observable.from(content.split(" "));
                }
            })
            .filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(final String line) {
                return line.contains("RxJava");
            }})
            .count().subscribe(new Subscriber<Integer>() {

            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(Integer s) {

                Log.i(TAG, "Number of Lines " + s);
                logOnConsole("Number of Lines " + s);
            }
        });
    }

    void createIntObservable() {
        Observable<Integer> myObservable = Observable.create(
                new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> sub) {
                        sub.onNext(10);
                        sub.onNext(3);
                        sub.onNext(9);
                        sub.onCompleted();
                    }
                }
        );

        Action1<Integer> onNextAction = new Action1<Integer>() {
            @Override
            public void call(Integer s) {
                Log.i(TAG, "New number :" + s);
                logOnConsole("New number :" + s);
            }
        };

        Action1<Throwable> onError = new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                Log.e(TAG, "Error - " + t.getMessage(), t);
                logOnConsole( "Error - " + t.getMessage());
            }
        };

        Action0 onComplete = new Action0() {

            @Override
            public void call() {

                Log.i(TAG, "Rx number stream completed");
                logOnConsole( "Rx number stream completed");
            }
        };

        myObservable.subscribe(onNextAction, onError, onComplete);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_rxjava_layout);
        TextView console = (TextView)findViewById(R.id.title);
        console.setText("Chapter 12 - Hello RxJava");
        Button fromALSBut = (Button) findViewById(R.id.fromALS);
        fromALSBut.setOnClickListener(fromALS);
        Button createIntBut = (Button) findViewById(R.id.createInt);
        createIntBut.setOnClickListener(createIntObs);

        Button actionsBut = (Button) findViewById(R.id.actions);
        actionsBut.setOnClickListener(actionsObs);
        Button fmBut = (Button) findViewById(R.id.flatMap);
        fmBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flatMapExample();
            }
        });

    }
}
