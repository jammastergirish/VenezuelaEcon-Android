package com.girish.venecon;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.girish.venecon.api.models.MWData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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

/**
 * Created by girish on 02/10/2016.
 */

public class MinWageFragment extends Fragment {


    private final DateRange xDefaultRange;
    View myView;
    private ChartView chartView;
    private ShinobiChart shinobiChart;

    public MinWageFragment() {

        GregorianCalendar calendar = new GregorianCalendar(2012, Calendar.JANUARY, 1);
        Date rangeMin = calendar.getTime();
        calendar.set(2017, Calendar.DECEMBER, 31);
        Date rangeMax = calendar.getTime();
        xDefaultRange = new DateRange(rangeMin, rangeMax);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.mw_layout, container, false);

        initializeAds();

        chartView = myView.findViewById(R.id.Chart);
        shinobiChart = chartView.getShinobiChart();
        final LinearLayout topBannerLayout = myView.findViewById(R.id.topBannerLayout);
        Utils.setShinobiChartBackground(shinobiChart);

        final ProgressBar progressBar = myView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        NetworkHelper networkHelper = new NetworkHelper();
        networkHelper.getMwDataRetrofit(new NetworkHelper.OnDataCallback<List<MWData>>() {
            @Override
            public void onSuccess(List<MWData> data) {
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

    private void fillUI(List<MWData> dataList) {

        LinkedHashMap<String, Double> MW = new LinkedHashMap<String, Double>();

        for (MWData mwData : dataList) {
            MW.put(mwData.getDate(), mwData.getUsd_bm());
        }

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        NumberFormat numberFormat = NumberFormat.getInstance();

        TextView MWTV = (TextView) myView.findViewById(R.id.MWVal);
        MWTV.setText(Html.fromHtml("$" + numberFormat.format(GetLatestNonZeroValue(MW, DateFormat.format(new Date()))) + "<small><small><small><small> / " + mContext.getString(R.string.month2) + ")</small></small></small>"));

        TextView MWYear = (TextView) myView.findViewById(R.id.MWYear);
        Compare(MW, YearsAgo(1), MWYear, "notFX");

        TextView MW2Year = (TextView) myView.findViewById(R.id.MW2Year);
        Compare(MW, YearsAgo(2), MW2Year, "notFX");

        TextView MW3Year = (TextView) myView.findViewById(R.id.MW3Year);
        Compare(MW, YearsAgo(3), MW3Year, "notFX");

        TextView MW4Year = (TextView) myView.findViewById(R.id.MW4Year);
        Compare(MW, YearsAgo(4), MW4Year, "notFX");

        DateTimeAxis xAxis = new DateTimeAxis();
        setupXAxis(xAxis);
        xAxis.setTitle(mContext.getString(R.string.date));
        xAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
        shinobiChart.addXAxis(xAxis);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setDefaultRange(new NumberRange(0.0, 1.05 * Collections.max(MW.values())));
        yAxis.setTitle(mContext.getString(R.string.minimum_wage) + " ($ / " + mContext.getString(R.string.month2) + ")");
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

        LinkedHashMap[] HMArray = {MW};

        populateChartWithData(HMArray, shinobiChart);
    }

    private void setupXAxis(DateTimeAxis xAxis) {
        xAxis.setDefaultRange(xDefaultRange);
        xAxis.enableGesturePanning(true);
        xAxis.enableGestureZooming(true);
        xAxis.enableMomentumPanning(true);
        xAxis.enableMomentumZooming(true);
    }

    private void populateChartWithData(LinkedHashMap<String, Double>[] HM, ShinobiChart shinobiChart) {

        int i = 0;
        int NumberOfHMs = HM.length;

        while (i < NumberOfHMs) { // 20161013 worked out that I needed to make an array to have multiple lines!

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            final DataAdapter<Date, Double> DataAdaptor = new SimpleDataAdapter<>();

            for (String dateString : HM[i].keySet()) {
                Double value = HM[i].get(dateString);
                if (value != 0) {
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

