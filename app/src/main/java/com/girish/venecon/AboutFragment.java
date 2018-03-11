package com.girish.venecon;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.InterstitialAd; // 20171130
import com.google.android.gms.ads.AdRequest;
import android.os.*;
import android.widget.TextView;


/**
 * Created by girish on 02/10/2016.
 */

public class AboutFragment extends Fragment {

    Button button;

    View myView;

    public AboutFragment() {


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //https://developers.google.com/admob/android/interstitial 20171130
        final InterstitialAd mInterstitialAd = new InterstitialAd(getActivity()); // https://stackoverflow.com/questions/37685388/getting-an-interstitial-ad-to-display-from-a-fragment
        mInterstitialAd.setAdUnitId("ca-app-pub-7175811277195688/6615701473");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        new Handler().postDelayed(new Runnable() { //https://stackoverflow.com/questions/17121248/missing-the-android-os-handler-object-from-android-studio https://stackoverflow.com/questions/31041884/execute-function-after-5-seconds-in-android
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
                }
            }
        }, 5000);


        myView = inflater.inflate(R.layout.about_layout, container, false);
        TextView versionTextView = myView.findViewById(R.id.versionTextView);
        versionTextView.setText(getString(R.string.version, BuildConfig.VERSION_NAME));

        return myView;


    }


//    public void addListenerOnButton() {
//
//        button = (Button) myView.findViewById(R.id.button);
//
//        button.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
////                Intent browserIntent =
////                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mkyong.com"));
////                startActivity(browserIntent);
//
//
//                Intent intent = new Intent(Intent.ACTION_SENDTO);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "");
//                intent.putExtra(Intent.EXTRA_TEXT, "");
//                intent.setData(Uri.parse("mailto:girish@girish-gupta.com"));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//
//
//            }
//
//        });
//
//    }






}

