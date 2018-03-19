package com.girish.venecon;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.girish.venecon.utils.Constants;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;


/**
 * Created by girish on 02/10/2016.
 */

public class CalculatorFragment extends Fragment {

    public static final String DOLLAR_FORMAT_LONG = "$%,d";
    public static final String DOLLAR_FORMAT_DOUBLE = "$%,.2f";
    public static final String BSF_FORMAT_LONG = "%,d BsF";
    public static final String BSF_FORMAT_DOUBLE = "%,.2f BsF";
    private static final float DEFAULT_BLACK_MARKET_VALUE = 40000;
    private static final float DEFAULT_DICOM_VALUE = 205899.46f;
    private TextView formattedValueTextView, dicomTextView, blackMarketTextView;
    private EditText valueEditText;
    private ToggleSwitch toggleSwitch;
    private boolean fromDollarsToBsf = true;
    private double blackMarketConversionRate = DEFAULT_BLACK_MARKET_VALUE, dicomConversionRate = DEFAULT_DICOM_VALUE;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            updateUI();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private View myView;

    public CalculatorFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.calculator_layout, container, false);
        formattedValueTextView = myView.findViewById(R.id.formattedValueTextView);
        dicomTextView = myView.findViewById(R.id.dicomTextView);
        blackMarketTextView = myView.findViewById(R.id.blackMarketTextView);
        valueEditText = myView.findViewById(R.id.valueEditText);
        valueEditText.addTextChangedListener(textWatcher);
        toggleSwitch = myView.findViewById(R.id.toggleSwitch);
        toggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if(position == 0 && isChecked){
                    fromDollarsToBsf = true;
                } else if(position == 1 && isChecked){
                    fromDollarsToBsf = false;
                }
                updateUI();
            }
        });

        initializeAds();

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            blackMarketConversionRate = sharedPreferences.getFloat(Constants.BLACK_MARKET_VALUE, DEFAULT_BLACK_MARKET_VALUE);
            dicomConversionRate = sharedPreferences.getFloat(Constants.DICOM_VALUE, DEFAULT_DICOM_VALUE);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return myView;
    }

    private void initializeAds() {
        Utils.loadIntersitialAd(getActivity());
        AdView adView = myView.findViewById(R.id.adView);
        Utils.loadBannerAd(adView);
    }

    private void updateUI() {
        try {
            long number = Long.parseLong(valueEditText.getText().toString());
            if (fromDollarsToBsf) {
                formattedValueTextView.setText(String.format(Locale.getDefault(), DOLLAR_FORMAT_LONG, number));
                double bsfBlackMarket = number * blackMarketConversionRate;
                double bsfDicom = number * dicomConversionRate;
                blackMarketTextView.setText(String.format(Locale.getDefault(), BSF_FORMAT_DOUBLE, bsfBlackMarket));
                dicomTextView.setText(String.format(Locale.getDefault(), BSF_FORMAT_DOUBLE, bsfDicom));
            } else {
                formattedValueTextView.setText(String.format(Locale.getDefault(), BSF_FORMAT_LONG, number));
                double dollarsBlackMarket = number / blackMarketConversionRate;
                double dollarsDicom = number / dicomConversionRate;
                blackMarketTextView.setText(String.format(Locale.getDefault(), DOLLAR_FORMAT_DOUBLE, dollarsBlackMarket));
                dicomTextView.setText(String.format(Locale.getDefault(), DOLLAR_FORMAT_DOUBLE, dollarsDicom));
            }
        } catch (NumberFormatException e){
            formattedValueTextView.setText("");
            blackMarketTextView.setText(getString(R.string.dots));
            dicomTextView.setText(getString(R.string.dots));
        }
    }


}

