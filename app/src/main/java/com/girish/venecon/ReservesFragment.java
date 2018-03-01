package com.girish.venecon;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.girish.venecon.api.models.ExchangeData;
import com.girish.venecon.api.models.ReserveData;
import com.girish.venecon.utils.Constants;
import com.shinobicontrols.charts.ChartView;
import com.shinobicontrols.charts.DataAdapter;
import com.shinobicontrols.charts.DataPoint;
import com.shinobicontrols.charts.DateRange;
import com.shinobicontrols.charts.DateTimeAxis;
import com.shinobicontrols.charts.LineSeries;
import com.shinobicontrols.charts.NumberAxis;
import com.shinobicontrols.charts.NumberRange;
import com.shinobicontrols.charts.ShinobiChart;
import com.shinobicontrols.charts.SimpleDataAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.android.gms.ads.InterstitialAd; // 20171130
import com.google.android.gms.ads.AdRequest;
import android.os.*;
import android.widget.Toast;


import static com.girish.venecon.Utils.mContext;



/**
 * Created by girish on 02/10/2016.
 */

public class ReservesFragment extends Fragment {
    public static String FRAGMENT_NAME = "Foreign Reserves";

    View myView;
    private ChartView chartView;
    private ShinobiChart shinobiChart;

    public ReservesFragment() {

        GregorianCalendar calendar = new GregorianCalendar(2012, Calendar.JANUARY, 1);
        Date rangeMin = calendar.getTime();
        calendar.set(2017, Calendar.DECEMBER, 31);
        Date rangeMax = calendar.getTime();
        xDefaultRange = new DateRange(rangeMin, rangeMax);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.reserves_layout, container, false);



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

        chartView = myView.findViewById(R.id.Chart);
        shinobiChart = chartView.getShinobiChart();
        Utils.setShinobiChartBackground(shinobiChart);

        NetworkHelper networkHelper = new NetworkHelper();
        networkHelper.getReservesDataRetrofit(new NetworkHelper.OnDataCallback<List<ReserveData>>() {
            @Override
            public void onSuccess(List<ReserveData> data) {
                fillUI(data);
            }

            @Override
            public void onFailure(String message) {
                Utils.handleError(getActivity(), message);
            }
        });

        return myView;
    }

    private void fillUI(List<ReserveData> dataList) {
        LinkedHashMap<String, Double> Reserves = new LinkedHashMap<>();
        for (ReserveData reserveData : dataList) {
            Reserves.put(reserveData.getDate(), reserveData.getRes() / Constants.RESERVE_DIVIDER);
        }

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        NumberFormat numberFormat = NumberFormat.getInstance();

        TextView ReservesTV = (TextView) myView.findViewById(R.id.ReservesVal);
        ReservesTV.setText(Html.fromHtml("$" + numberFormat.format(Utils.GetLatestNonZeroValue(Reserves, DateFormat.format(new Date()))) + "<small><small><small><small> " + mContext.getString(R.string.billion) + "</small></small></small>"));

        TextView ReservesMonth = (TextView) myView.findViewById(R.id.ReservesMonth);
        Utils.Compare(Reserves, Utils.MonthsAgo(1), ReservesMonth, "notFX");

        TextView ReservesYear = (TextView) myView.findViewById(R.id.ReservesYear);
        Utils.Compare(Reserves, Utils.YearsAgo(1), ReservesYear, "notFX");

        TextView Reserves2Year = (TextView) myView.findViewById(R.id.Reserves2Year);
        Utils.Compare(Reserves, Utils.YearsAgo(2), Reserves2Year, "notFX");

        TextView Reserves3Year = (TextView) myView.findViewById(R.id.Reserves3Year);
        Utils.Compare(Reserves, Utils.YearsAgo(3), Reserves3Year, "notFX");

        TextView Reserves4Year = (TextView) myView.findViewById(R.id.Reserves4Year);
        Utils.Compare(Reserves, Utils.YearsAgo(4), Reserves4Year, "notFX");

        DateTimeAxis xAxis = new DateTimeAxis();
        setupXAxis(xAxis);
        xAxis.setTitle(mContext.getString(R.string.date));
        xAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
        shinobiChart.addXAxis(xAxis);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setDefaultRange(new NumberRange(0.0, 1.05 * Collections.max(Reserves.values())));
        yAxis.setTitle(mContext.getString(R.string.foreign_reserves) + " ($ bn)");
        yAxis.setRangePaddingHigh(100.0);
        yAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
        yAxis.getTickMarkClippingModeHigh();
        shinobiChart.addYAxis(yAxis);

        final LineSeries ResLine = new LineSeries();
        ResLine.getStyle().setLineColor(Color.RED);
        ResLine.getStyle().setLineWidth((float) 2);
        shinobiChart.addSeries(ResLine);

        LinkedHashMap[] HMArray = {Reserves};

        populateChartWithData(HMArray, shinobiChart);
    }

    private void setupXAxis(DateTimeAxis xAxis) {
        xAxis.setDefaultRange(xDefaultRange);
        xAxis.enableGesturePanning(true);
        xAxis.enableGestureZooming(true);
        xAxis.enableMomentumPanning(true);
        xAxis.enableMomentumZooming(true);
    }

    private final DateRange xDefaultRange;


    private void populateChartWithData(LinkedHashMap<String, Double>[] HM, ShinobiChart shinobiChart) {

        int i = 0;
        int NumberOfHMs = HM.length;

        while (i < NumberOfHMs) { // 20161013 worked out that I needed to make an array to have multiple lines!

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            final DataAdapter<Date, Double> DataAdaptor = new SimpleDataAdapter<>();

            for (String dateString : HM[i].keySet()) {
                Double value = HM[i].get(dateString);
                if (value!=0) {
                    Date date = null;
                    try {
                        date = dateFormat.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DataAdaptor.add(new DataPoint<>(date, value));
                }
            }

            shinobiChart.getSeries().get(i).setDataAdapter(DataAdaptor);
            i++;
        }
    }

}

