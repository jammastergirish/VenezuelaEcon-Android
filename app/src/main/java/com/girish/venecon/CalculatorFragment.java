package com.girish.venecon;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.girish.venecon.utils.Constants;

import java.util.Locale;


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
    private ToggleButton toggleButton;
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

    public CalculatorFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculator_layout, container, false);
        formattedValueTextView = view.findViewById(R.id.formattedValueTextView);
        dicomTextView = view.findViewById(R.id.dicomTextView);
        blackMarketTextView = view.findViewById(R.id.blackMarketTextView);
        valueEditText = view.findViewById(R.id.valueEditText);
        valueEditText.addTextChangedListener(textWatcher);
        toggleButton = view.findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleConversion();
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        blackMarketConversionRate = sharedPreferences.getFloat(Constants.BLACK_MARKET_VALUE, DEFAULT_BLACK_MARKET_VALUE);
        dicomConversionRate = sharedPreferences.getFloat(Constants.DICOM_VALUE, DEFAULT_DICOM_VALUE);
        return view;
    }

    private void toggleConversion() {
        fromDollarsToBsf = !fromDollarsToBsf;
        updateUI();
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

