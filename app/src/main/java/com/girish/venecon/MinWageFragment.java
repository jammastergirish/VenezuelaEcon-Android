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

public class MinWageFragment extends Fragment {


    View myView;

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

        new MyAsyncTask().execute();

        return myView;
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... empty) {
            NetworkHelper NetHelp = new NetworkHelper();
            String UnparsedResult = NetHelp.getMWData();
            return UnparsedResult;
        }

        @Override
        protected void onPostExecute(String s) {

            if (TextUtils.isEmpty(s)) {
                Toast.makeText(getActivity(),
                        "Something went wrong. Please check your connection.", Toast.LENGTH_SHORT).show();
            } else {

                JSONArray jsonArray;
                JSONObject jsonObject;


                String jsonDate;
                String jsonMW;
                LinkedHashMap<String, Double> MW = new LinkedHashMap<String, Double>();

                try {
                    jsonArray = new JSONArray(s);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        jsonObject = jsonArray.getJSONObject(i);
                        jsonDate = jsonObject.getString("date");

                        jsonMW = jsonObject.getString("usd_bm");
                        MW.put(jsonDate, Double.valueOf(jsonMW));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Wasn't JSON
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

        }
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

