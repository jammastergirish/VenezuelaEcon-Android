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

import com.girish.venecon.api.models.UsOilData;
import com.girish.venecon.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

import static com.girish.venecon.Utils.Compare;
import static com.girish.venecon.Utils.GetLatestNonZeroValue;
import static com.girish.venecon.Utils.YearsAgo;
import static com.girish.venecon.Utils.mContext;


/**
 * Created by girish on 02/10/2016.
 */

public class USOilFragment extends Fragment {
    //public static String FRAGMENT_NAME = "Oil Prices";

    private final DateRange xDefaultRange;
    View myView;
    private ChartView chartView;
    private ShinobiChart shinobiChart;

    public USOilFragment() {

        GregorianCalendar calendar = new GregorianCalendar(2004, Calendar.JANUARY, 1);
        Date rangeMin = calendar.getTime();
        calendar.set(2017, Calendar.DECEMBER, 31);
        Date rangeMax = calendar.getTime();
        xDefaultRange = new DateRange(rangeMin, rangeMax);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.usoil_layout, container, false);

        initializeAds();

        chartView = myView.findViewById(R.id.Chart);
        shinobiChart = chartView.getShinobiChart();
        final LinearLayout topBannerLayout = myView.findViewById(R.id.topBannerLayout);
        Utils.setShinobiChartBackground(shinobiChart);
        final ProgressBar progressBar = myView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        NetworkHelper networkHelper = new NetworkHelper();
        networkHelper.getUsOilDataRetrofit(new NetworkHelper.OnDataCallback<List<UsOilData>>() {
            @Override
            public void onSuccess(List<UsOilData> data) {
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
        //Utils.loadIntersitialAd(getActivity());
        AdView adView = myView.findViewById(R.id.adView);
        Utils.loadBannerAd(adView);
    }

    private void fillUI(List<UsOilData> dataList) {
        LinkedHashMap<String, Double> Exports = new LinkedHashMap<>();
        LinkedHashMap<String, Double> Imports = new LinkedHashMap<>();

        for (UsOilData usOilData : dataList) {
            Exports.put(usOilData.getDate(), usOilData.getExp() / Constants.US_OIL_DIVIDER);
            Imports.put(usOilData.getDate(), usOilData.getImp() / Constants.US_OIL_DIVIDER);
        }

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        NumberFormat numberFormat = NumberFormat.getInstance();

        TextView ImportsTV = (TextView) myView.findViewById(R.id.ImportsVal);
        ImportsTV.setText(Html.fromHtml(numberFormat.format(GetLatestNonZeroValue(Imports, DateFormat.format(new Date()))) + "<small><small><small><small>bn " + mContext.getString(R.string.barrelsper) + " " + mContext.getString(R.string.year2) + "</small></small></small>"));

        TextView Exports2year = (TextView) myView.findViewById(R.id.Exports2year);
        Compare(Exports, YearsAgo(2), Exports2year, "notFX");

        TextView Exports3year = (TextView) myView.findViewById(R.id.Exports3year);
        Compare(Exports, YearsAgo(3), Exports3year, "notFX");

        TextView Exports4year = (TextView) myView.findViewById(R.id.Exports4year);
        Compare(Exports, YearsAgo(4), Exports4year, "notFX");

        TextView Exports5year = (TextView) myView.findViewById(R.id.Exports5year);
        Compare(Exports, YearsAgo(5), Exports5year, "notFX");

        TextView Exports6year = (TextView) myView.findViewById(R.id.Exports6year);
        Compare(Exports, YearsAgo(6), Exports6year, "notFX");

        TextView ExportsTV = (TextView) myView.findViewById(R.id.ExportsVal);
        ExportsTV.setText(Html.fromHtml(numberFormat.format(GetLatestNonZeroValue(Exports, DateFormat.format(new Date()))) + "<small><small><small><small>bn " + mContext.getString(R.string.barrelsper) + " " + mContext.getString(R.string.year2) + "</small></small></small>"));

        TextView Imports2year = (TextView) myView.findViewById(R.id.Imports2year);
        Compare(Imports, YearsAgo(2), Imports2year, "notFX");

        TextView Imports3year = (TextView) myView.findViewById(R.id.Imports3year);
        Compare(Imports, YearsAgo(3), Imports3year, "notFX");

        TextView Imports4year = (TextView) myView.findViewById(R.id.Imports4year);
        Compare(Imports, YearsAgo(4), Imports4year, "notFX");

        TextView Imports5year = (TextView) myView.findViewById(R.id.Imports5year);
        Compare(Imports, YearsAgo(5), Imports5year, "notFX");

        TextView Imports6year = (TextView) myView.findViewById(R.id.Imports6year);
        Compare(Imports, YearsAgo(6), Imports6year, "notFX");

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
        yAxis.setDefaultRange(new NumberRange(0.0, 1.05 * Collections.max(Imports.values())));
        yAxis.setTitle(mContext.getString(R.string.us_oil) + " (bn " + mContext.getString(R.string.barrelsper) + " " + mContext.getString(R.string.year2) + ")");
        yAxis.setRangePaddingHigh(100.0);
        yAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
        yAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
        yAxis.getTickMarkClippingModeHigh();
        shinobiChart.addYAxis(yAxis);

        final LineSeries ExportsLine = new LineSeries();
        ExportsLine.getStyle().setLineColor(Color.GREEN);
        ExportsLine.getStyle().setLineWidth((float) 2);
        shinobiChart.addSeries(ExportsLine);

        final LineSeries ImportsLine = new LineSeries();
        ImportsLine.getStyle().setLineColor(Color.RED);
        ImportsLine.getStyle().setLineWidth((float) 2);
        shinobiChart.addSeries(ImportsLine);

        LinkedHashMap[] HMArray = {Exports, Imports};

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

