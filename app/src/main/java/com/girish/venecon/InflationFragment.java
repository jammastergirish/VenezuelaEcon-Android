package com.girish.venecon;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.girish.venecon.api.models.InflationData;
import com.girish.venecon.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import static com.girish.venecon.Utils.AnnualInflation;
import static com.girish.venecon.Utils.mContext;
import static java.lang.Math.abs;


public class InflationFragment extends Fragment {

    private final DateRange xDefaultRange;
    View myView;

    public InflationFragment() {

        GregorianCalendar calendar = new GregorianCalendar(2010, Calendar.JANUARY, 1);
        Date rangeMin = calendar.getTime();
        calendar.set(2017, Calendar.DECEMBER, 31);
        Date rangeMax = calendar.getTime();
        xDefaultRange = new DateRange(rangeMin, rangeMax);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.inflation_layout, container, false);

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
        NetworkHelper networkHelper = new NetworkHelper();
        networkHelper.getInflationDataRetrofit(new NetworkHelper.OnDataCallback<List<InflationData>>() {
            @Override
            public void onSuccess(List<InflationData> data) {
                fillUI(data);
            }

            @Override
            public void onFailure(String message) {
                Utils.handleError(getActivity(), message);
            }
        });

        return myView;
    }

    private void fillUI(List<InflationData> dataList) {
        LinkedHashMap<String, Double> Inflation = new LinkedHashMap<String, Double>();

        for (InflationData inflation : dataList) {
            Inflation.put(inflation.getDate(), inflation.getInf() / Constants.INFLATION_DIVIDER);
        }

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        NumberFormat numberFormat = NumberFormat.getInstance();

        TextView Inflation2016 = (TextView) myView.findViewById(R.id.Inflation2016);
        Inflation2016.setText("-");

        TextView Inflation2015 = (TextView) myView.findViewById(R.id.Inflation2015);
        Inflation2015.setText(numberFormat.format(abs(AnnualInflation(2015, Inflation))) + "%");

        TextView Inflation2014 = (TextView) myView.findViewById(R.id.Inflation2014);
        Inflation2014.setText(numberFormat.format(abs(AnnualInflation(2014, Inflation))) + "%");

        TextView Inflation2013 = (TextView) myView.findViewById(R.id.Inflation2013);
        Inflation2013.setText(numberFormat.format(abs(AnnualInflation(2013, Inflation))) + "%");

        TextView Inflation2012 = (TextView) myView.findViewById(R.id.Inflation2012);
        Inflation2012.setText(numberFormat.format(abs(AnnualInflation(2012, Inflation))) + "%");

        TextView Inflation2011 = (TextView) myView.findViewById(R.id.Inflation2011);
        Inflation2011.setText(numberFormat.format(abs(AnnualInflation(2011, Inflation))) + "%");

        TextView Inflation2010 = (TextView) myView.findViewById(R.id.Inflation2010);
        Inflation2010.setText(numberFormat.format(abs(AnnualInflation(2010, Inflation))) + "%");

        TextView Inflation2009 = (TextView) myView.findViewById(R.id.Inflation2009);
        Inflation2009.setText(numberFormat.format(abs(AnnualInflation(2009, Inflation))) + "%");

        ChartView chartView = (ChartView) myView.findViewById(R.id.Chart);

        ShinobiChart shinobiChart = chartView.getShinobiChart();

        shinobiChart.getStyle().setBackgroundColor(Color.parseColor("#000000"));
        shinobiChart.getStyle().setCanvasBackgroundColor(Color.parseColor("#000000"));
        shinobiChart.getStyle().setPlotAreaBackgroundColor(Color.parseColor("#000000"));

        DateTimeAxis xAxis = new DateTimeAxis();
        setupXAxis(xAxis);
        xAxis.setTitle(mContext.getString(R.string.date));
        xAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
        xAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
        xAxis.setMajorTickFrequency(new DateFrequency(3, DateFrequency.Denomination.YEARS)); // On Eddie's advice 20161025
        shinobiChart.addXAxis(xAxis);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setDefaultRange(new NumberRange(0.0, 1.05 * Collections.max(Inflation.values())));
        yAxis.setTitle(mContext.getString(R.string.inflation) + " (2007 = 1)");
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

        LinkedHashMap[] HMArray = {Inflation};

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

