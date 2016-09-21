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
package com.packpublishing.asynchronousandroid.chapter11;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PaginatedActivity extends FragmentActivity {

    public final String TAG = this.getClass().getSimpleName();

    int productId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paginated_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DetailFragment fragment = new DetailFragment();
        fragmentTransaction.add(R.id.detail_fragment, fragment);
        fragmentTransaction.commit();

        // Retrieve the product
        EventBus.getDefault().post(new RetrieveProductEvent(productId));

        Button next = (Button)findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RetrieveProductEvent(++productId));
            }
        });
        Button prev = (Button)findViewById(R.id.previous);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RetrieveProductEvent(productId > 0 ? --productId :0));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Subscribe(threadMode=ThreadMode.ASYNC)
    public void onRetrieveProductEvent(RetrieveProductEvent event) {
        Log.i(TAG,"Retrieving the product "+event.identifier+" on "+Thread.currentThread().getName());

        ProductDetailEvent pde =  event.identifier % 2 == 0 ?
                                new ProductDetailEvent(1L,"Timberland","Blue Plain Shirt",54.00f):
                                new ProductDetailEvent(2L,"RipCurl","Red Skinned Shirt",52.00f);

        EventBus.getDefault().post(pde);
    }

    public static class DetailFragment extends Fragment {

        public final String TAG = this.getClass().getSimpleName();

        TextView brandTv;
        TextView nameTv;
        TextView priceTv;

        @Override
        public void onResume() {
            EventBus.getDefault().register(this);
            super.onResume();
        }

        @Override
        public void onPause() {
            EventBus.getDefault().unregister(this);
            super.onPause();
        }

        @Subscribe(threadMode=ThreadMode.MAIN)
        public void onProductDetailEvent(ProductDetailEvent event) {
            Log.i(TAG,"Product details received for identifier "+event.identifier+" on "+Thread.currentThread().getName());
            brandTv.setText(event.brand);
            nameTv.setText(event.name);
            priceTv.setText(Float.toString(event.price));

        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.detail_fragment, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            brandTv= (TextView)view.findViewById(R.id.brandValue);
            nameTv= (TextView)view.findViewById(R.id.productName);
            priceTv= (TextView)view.findViewById(R.id.productPrice);
        }
    }

}
