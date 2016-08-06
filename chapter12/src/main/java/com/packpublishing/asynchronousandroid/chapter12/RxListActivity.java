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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.android.MainThreadSubscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;


public class RxListActivity extends Activity {

    public final String TAG = this.getClass().getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Subscription subcription = null;
    List<String> soccerTeams = Arrays.asList(
            "Real Madrid", "Barcelona","Manchester United","Manchester City","Sporting CP","PSG",
            "Juventus","AC Milan","Bayern Munchen","Benfica","FC Porto","Chelsea","Liverpoll");


    public class TextChangeOnSubscribe implements OnSubscribe<String>{

        WeakReference<EditText> editText;

        public TextChangeOnSubscribe(EditText editText){
            this.editText = new WeakReference<EditText>(editText);
        }

        @Override
        public void call(final Subscriber<? super String> subscriber) {
            final TextWatcher watcher = new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(s.toString());
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {}
            };
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    editText.get().removeTextChangedListener(watcher);
                }
            });
            editText.get().addTextChangedListener(watcher);
            subscriber.onNext("");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rx_list_activity);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        EditText search = (EditText) findViewById(R.id.searchTv);

        Observable<String> textChangeObs = Observable.create(new TextChangeOnSubscribe(search))
                                                     .debounce(400, TimeUnit.MILLISECONDS);

        subcription = Observable.combineLatest(Observable.just(soccerTeams), textChangeObs,
            new Func2<List<String>, String, List<String>>() {
                @Override
                public List<String> call(List<String> fullList, String filter) {
                    List<String> result = new ArrayList<String>();
                    for (String team : fullList) {
                        if (team.startsWith(filter)) {
                            result.add(team);
                        }
                    }
                    Collections.sort(result);
                    return result;
                }
            }
        )
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> teams) {
                mAdapter = new MyAdapter(teams);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ((subcription != null) && (isFinishing()) && !subcription.isUnsubscribed())
            subcription.unsubscribe();
    }

}
