package com.girish.venecon;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.gms.ads.InterstitialAd; // 20171130
import com.google.android.gms.ads.AdRequest;
import android.os.*;


/**
 * Created by girish on 02/10/2016.
 */

public class CalculatorFragment extends Fragment {


    View myView;

    public CalculatorFragment() {


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.calculator_layout, container, false);

        return myView;
    }







}

