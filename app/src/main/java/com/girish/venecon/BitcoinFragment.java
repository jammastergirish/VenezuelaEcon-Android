package com.girish.venecon;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

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

import com.google.android.gms.ads.InterstitialAd; // 20171130
import com.google.android.gms.ads.AdRequest;
import android.os.*;


import static com.girish.venecon.Utils.mContext;



/**
 * Created by girish on 02/10/2016.
 */

public class BitcoinFragment extends Fragment {
    public static String FRAGMENT_NAME = "Bitcoin";

    View myView;

    public BitcoinFragment() {

        GregorianCalendar calendar = new GregorianCalendar(2012, Calendar.JANUARY, 1);
        Date rangeMin = calendar.getTime();
        calendar.set(2017, Calendar.DECEMBER, 31);
        Date rangeMax = calendar.getTime();
        xDefaultRange = new DateRange(rangeMin, rangeMax);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.bitcoin_layout, container, false);



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
            String UnparsedResult = NetHelp.getBitcoinData();
            return UnparsedResult;
        }

        @Override
        protected void onPostExecute(String s) {

            JSONArray jsonArray;
            JSONObject jsonObject;


            String jsonDate;
            String jsonBitcoin;
            LinkedHashMap<String, Double> Bitcoin = new LinkedHashMap<String,Double>();

            try {
                jsonArray = new JSONArray(s);

                for (int i=0; i < jsonArray.length(); i++)
                {

                    jsonObject = jsonArray.getJSONObject(i);
                    jsonDate = jsonObject.getString("date");

                    jsonBitcoin = jsonObject.getString("bitcoin");
                    Bitcoin.put(jsonDate, Double.valueOf(jsonBitcoin)/1000000.0);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Wasn't JSON
            }

            SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
            NumberFormat numberFormat = NumberFormat.getInstance();

            TextView BitcoinTV = (TextView) myView.findViewById(R.id.BitcoinVal);
            BitcoinTV.setText(Html.fromHtml(numberFormat.format(Utils.GetLatestNonZeroValue(Bitcoin, DateFormat.format(new Date())))+"<small><small><small><small> "+mContext.getString(R.string.billion)+" BsF</small></small></small>"));

            TextView BitcoinMonth = (TextView) myView.findViewById(R.id.BitcoinMonth);
            Utils.Compare(Bitcoin, Utils.MonthsAgo(1), BitcoinMonth, "notFX");

            TextView BitcoinYear = (TextView) myView.findViewById(R.id.BitcoinYear);
            Utils.Compare(Bitcoin, Utils.YearsAgo(1), BitcoinYear, "notFX");

            TextView Bitcoin2Year = (TextView) myView.findViewById(R.id.Bitcoin2Year);
            Utils.Compare(Bitcoin, Utils.YearsAgo(2), Bitcoin2Year, "notFX");

            TextView Bitcoin3Year = (TextView) myView.findViewById(R.id.Bitcoin3Year);
            Utils.Compare(Bitcoin, Utils.YearsAgo(3), Bitcoin3Year, "notFX");

            TextView Bitcoin4Year = (TextView) myView.findViewById(R.id.Bitcoin4Year);
            Utils.Compare(Bitcoin, Utils.YearsAgo(4), Bitcoin4Year, "notFX");









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
            final DateFrequency dfMajor = new DateFrequency(2, DateFrequency.Denomination.YEARS); // 20171128 Emailing with Shinobi
            xAxis.setMajorTickFrequency(dfMajor);
            shinobiChart.addXAxis(xAxis);

            NumberAxis yAxis = new NumberAxis();
            yAxis.setDefaultRange(new NumberRange(0.0, 1.2*Collections.max(Bitcoin.values())));
            yAxis.setTitle("Bitcoin (million BsF)");
            yAxis.setRangePaddingHigh(100.0);
            yAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
            yAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
            yAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
            yAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
            yAxis.getTickMarkClippingModeHigh();
            shinobiChart.addYAxis(yAxis);

            final LineSeries ResLine = new LineSeries();
            ResLine.getStyle().setLineColor(Color.RED);
            ResLine.getStyle().setLineWidth((float)2);
            shinobiChart.addSeries(ResLine);

            LinkedHashMap[] HMArray = {Bitcoin};

            populateChartWithData(HMArray, shinobiChart);


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

