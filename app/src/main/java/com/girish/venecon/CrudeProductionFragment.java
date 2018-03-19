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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.girish.venecon.api.models.CrudeProductionData;
import com.girish.venecon.utils.Constants;
import com.google.android.gms.ads.AdView;
import com.shinobicontrols.charts.ChartView;
import com.shinobicontrols.charts.DataAdapter;
import com.shinobicontrols.charts.DataPoint;
import com.shinobicontrols.charts.DateFrequency;
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

import static com.girish.venecon.Utils.Compare;
import static com.girish.venecon.Utils.GetLatestNonZeroValue;
import static com.girish.venecon.Utils.YearsAgo;
import static com.girish.venecon.Utils.mContext;

import com.google.android.gms.ads.InterstitialAd; // 20171130
import com.google.android.gms.ads.AdRequest;
import android.os.*;
import android.widget.Toast;

/**
 * Created by girish on 02/10/2016.
 */

public class CrudeProductionFragment extends Fragment {
    //public static String FRAGMENT_NAME = "Oil Prices";

    View myView;
    private ChartView chartView;
    private ShinobiChart shinobiChart;

    public CrudeProductionFragment() {

        GregorianCalendar calendar = new GregorianCalendar(2002, Calendar.JANUARY, 1);
        Date rangeMin = calendar.getTime();
        calendar.set(2017, Calendar.DECEMBER, 31);
        Date rangeMax = calendar.getTime();
        xDefaultRange = new DateRange(rangeMin, rangeMax);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.crude_production_layout, container, false);

        initializeAds();

        chartView = myView.findViewById(R.id.Chart);
        shinobiChart = chartView.getShinobiChart();
        final LinearLayout topBannerLayout = myView.findViewById(R.id.topBannerLayout);
        Utils.setShinobiChartBackground(shinobiChart);

        final ProgressBar progressBar = myView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        NetworkHelper networkHelper = new NetworkHelper();
        networkHelper.getCrudeProductionDataRetrofit(new NetworkHelper.OnDataCallback<List<CrudeProductionData>>() {
            @Override
            public void onSuccess(List<CrudeProductionData> data) {
                fillUI(data);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String message) {
                Utils.handleError(getActivity(), message, topBannerLayout);
                progressBar.setVisibility(View.GONE);
            }
        });

        return myView;
    }

    private void initializeAds() {
        Utils.loadIntersitialAd(getActivity());
        AdView adView = myView.findViewById(R.id.adView);
        Utils.loadBannerAd(adView);
    }

    private void fillUI(List<CrudeProductionData> dataList) {
        LinkedHashMap<String, Double> direct = new LinkedHashMap<>();
        LinkedHashMap<String, Double> secondary = new LinkedHashMap<>();

        for (CrudeProductionData crude : dataList) {
            direct.put(crude.getDate(), crude.getDirect() / Constants.CRUDE_DIVIDER);
            secondary.put(crude.getDate(), crude.getSecondary() / Constants.CRUDE_DIVIDER);
        }

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        NumberFormat numberFormat = NumberFormat.getInstance();


        TextView directTV = (TextView) myView.findViewById(R.id.directVal);
        directTV.setText(Html.fromHtml(numberFormat.format(GetLatestNonZeroValue(direct, DateFormat.format(new Date()))) + "<small><small><small><small>m " + mContext.getString(R.string.barrelsper) + " " + mContext.getString(R.string.day) + "</small></small></small>"));

        TextView direct1year = (TextView) myView.findViewById(R.id.direct1year);
        Compare(direct, YearsAgo(1), direct1year, "notFX");

        TextView direct2year = (TextView) myView.findViewById(R.id.direct2year);
        Compare(direct, YearsAgo(2), direct2year, "notFX");

        TextView direct3year = (TextView) myView.findViewById(R.id.direct3year);
        Compare(direct, YearsAgo(3), direct3year, "notFX");

        TextView direct4year = (TextView) myView.findViewById(R.id.direct4year);
        Compare(direct, YearsAgo(4), direct4year, "notFX");

        TextView secondaryTV = (TextView) myView.findViewById(R.id.secondaryVal);
        secondaryTV.setText(Html.fromHtml(numberFormat.format(GetLatestNonZeroValue(secondary, DateFormat.format(new Date()))) + "<small><small><small><small>m " + mContext.getString(R.string.barrelsper) + " " + mContext.getString(R.string.day) + "</small></small></small>"));

        TextView secondary1year = (TextView) myView.findViewById(R.id.secondary1year);
        Compare(secondary, YearsAgo(1), secondary1year, "notFX");

        TextView secondary2year = (TextView) myView.findViewById(R.id.secondary2year);
        Compare(secondary, YearsAgo(2), secondary2year, "notFX");

        TextView secondary3year = (TextView) myView.findViewById(R.id.secondary3year);
        Compare(secondary, YearsAgo(3), secondary3year, "notFX");

        TextView secondary4year = (TextView) myView.findViewById(R.id.secondary4year);
        Compare(secondary, YearsAgo(4), secondary4year, "notFX");

        DateTimeAxis xAxis = new DateTimeAxis();
        setupXAxis(xAxis);
        xAxis.setTitle(mContext.getString(R.string.date));
        xAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
        xAxis.setMajorTickFrequency(new DateFrequency(4, DateFrequency.Denomination.YEARS)); // On Eddie's advice 20161025
        shinobiChart.addXAxis(xAxis);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setDefaultRange(new NumberRange(0.0, 1.05 * Collections.max(direct.values())));
        yAxis.setTitle(mContext.getString(R.string.crude_production) + " (m " + mContext.getString(R.string.barrelsper) + " " + mContext.getString(R.string.day) + ")");
        yAxis.setRangePaddingHigh(100.0);
        yAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
        yAxis.getTickMarkClippingModeHigh();
        shinobiChart.addYAxis(yAxis);

        final LineSeries directLine = new LineSeries();
        directLine.getStyle().setLineColor(Color.GREEN);
        directLine.getStyle().setLineWidth((float) 2);
        shinobiChart.addSeries(directLine);

        final LineSeries secondaryLine = new LineSeries();
        secondaryLine.getStyle().setLineColor(Color.RED);
        secondaryLine.getStyle().setLineWidth((float) 2);
        shinobiChart.addSeries(secondaryLine);

        LinkedHashMap[] HMArray = {direct, secondary};

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

