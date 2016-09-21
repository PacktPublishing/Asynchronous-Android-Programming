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
package com.packpublishing.asynchronousandroid.chapter4;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BitcoinExchangeRateFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Double>  {

    public static final int BITCOIN_EURO_EXRATE_LOADER_ID = 1;
    private static final String CURRENNCY_KEY="currency";
    private static final String REFRESH_INTERNAL="internal";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("BitcoinExchangeRate","onCreateView created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bitcoin_exchange_rate_fragment_layout,
                                container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("BitcoinExchangeRate","Activity created");
        super.onActivityCreated(savedInstanceState);
        LoaderManager lm = getActivity().getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString(CURRENNCY_KEY,"EUR");
        bundle.putInt(REFRESH_INTERNAL, 5000);
                lm.initLoader(BITCOIN_EURO_EXRATE_LOADER_ID, bundle, BitcoinExchangeRateFragment.this);
    }

    @Override
    public Loader<Double> onCreateLoader(int id, Bundle args) {
        Loader res = null;
        Log.i("BitcoinExchangeRate", "LoaderCallbacks.onCreateLoader[" + id + "]");
        switch (id) {
            case BITCOIN_EURO_EXRATE_LOADER_ID:
                res = new BitcoinExchangeRateLoader(getActivity(),
                                                    args.getString(CURRENNCY_KEY),
                                                    args.getInt(REFRESH_INTERNAL));
                break;
        }
        return res;
    }

    @Override
    public void onLoadFinished(Loader<Double> loader, Double data) {

        Log.i("BitcoinExchangeRate", "LoaderCallbacks.onLoadFinished[" + loader.getId() + "]");
        switch (loader.getId()) {
            case BITCOIN_EURO_EXRATE_LOADER_ID:
                // Initialize recycler view
                TextView tv  = (TextView) getView().findViewById(R.id.temperature);
                tv.setText(data.toString());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Double> loader) {

    }
}
