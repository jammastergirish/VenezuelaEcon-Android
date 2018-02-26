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
import static com.girish.venecon.Utils.MonthsAgo;
import static com.girish.venecon.Utils.YearsAgo;
import static com.girish.venecon.Utils.mContext;

import com.google.android.gms.ads.InterstitialAd; // 20171130
import com.google.android.gms.ads.AdRequest;
import android.os.*;
import android.widget.Toast;

/**
 * Created by girish on 02/10/2016.
 */

public class OilFragment extends Fragment {
    public static String FRAGMENT_NAME = "Oil Prices";

    View myView;

    public OilFragment() {

        GregorianCalendar calendar = new GregorianCalendar(2012, Calendar.JANUARY, 1);
        Date rangeMin = calendar.getTime();
        calendar.set(2017, Calendar.DECEMBER, 31);
        Date rangeMax = calendar.getTime();
        xDefaultRange = new DateRange(rangeMin, rangeMax);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.oil_layout, container, false);

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
            String UnparsedResult = NetHelp.getOilData();
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

                String jsonWTI;
                LinkedHashMap<String, Double> WTI = new LinkedHashMap<String, Double>();

                String jsonBrent;
                LinkedHashMap<String, Double> Brent = new LinkedHashMap<String, Double>();

                String jsonOPEC;
                LinkedHashMap<String, Double> OPEC = new LinkedHashMap<String, Double>();

                String jsonVen;
                LinkedHashMap<String, Double> Ven = new LinkedHashMap<String, Double>();

                try {
                    jsonArray = new JSONArray(s);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        jsonObject = jsonArray.getJSONObject(i);
                        jsonDate = jsonObject.getString("date");

                        jsonWTI = jsonObject.getString("wti");
                        WTI.put(jsonDate, Double.valueOf(jsonWTI));

                        jsonOPEC = jsonObject.getString("opec");
                        OPEC.put(jsonDate, Double.valueOf(jsonOPEC));

                        jsonBrent = jsonObject.getString("brent");
                        Brent.put(jsonDate, Double.valueOf(jsonBrent));

                        jsonVen = jsonObject.getString("ven");
                        Ven.put(jsonDate, Double.valueOf(jsonVen));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Wasn't JSON
                }

                SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
                NumberFormat numberFormat = NumberFormat.getInstance();


                TextView WTITV = (TextView) myView.findViewById(R.id.WTIVal);
                WTITV.setText(Html.fromHtml("$" + numberFormat.format(GetLatestNonZeroValue(WTI, DateFormat.format(new Date()))) + "<small><small><small><small> / " + mContext.getString(R.string.barrel) + "</small></small></small>"));

                TextView WTIMonth = (TextView) myView.findViewById(R.id.WTIMonth);
                Compare(WTI, MonthsAgo(1), WTIMonth, "notFX");

                TextView WTIYear = (TextView) myView.findViewById(R.id.WTIYear);
                Compare(WTI, YearsAgo(1), WTIYear, "notFX");

                TextView WTI2Year = (TextView) myView.findViewById(R.id.WTI2Year);
                Compare(WTI, YearsAgo(2), WTI2Year, "notFX");


                TextView BrentTV = (TextView) myView.findViewById(R.id.BrentVal);
                BrentTV.setText(Html.fromHtml("$" + numberFormat.format(GetLatestNonZeroValue(Brent, DateFormat.format(new Date()))) + "<small><small><small><small> / " + mContext.getString(R.string.barrel) + "</small></small></small>"));

                TextView BrentMonth = (TextView) myView.findViewById(R.id.BrentMonth);
                Compare(Brent, MonthsAgo(1), BrentMonth, "notFX");

                TextView BrentYear = (TextView) myView.findViewById(R.id.BrentYear);
                Compare(Brent, YearsAgo(1), BrentYear, "notFX");

                TextView Brent2Year = (TextView) myView.findViewById(R.id.Brent2Year);
                Compare(Brent, YearsAgo(2), Brent2Year, "notFX");


                TextView OPECTV = (TextView) myView.findViewById(R.id.OPECVal);
                OPECTV.setText(Html.fromHtml("$" + numberFormat.format(GetLatestNonZeroValue(OPEC, DateFormat.format(new Date()))) + "<small><small><small><small> / " + mContext.getString(R.string.barrel) + "</small></small></small>"));

                TextView OPECMonth = (TextView) myView.findViewById(R.id.OPECMonth);
                Compare(OPEC, MonthsAgo(1), OPECMonth, "notFX");

                TextView OPECYear = (TextView) myView.findViewById(R.id.OPECYear);
                Compare(OPEC, YearsAgo(1), OPECYear, "notFX");

                TextView OPEC2Year = (TextView) myView.findViewById(R.id.OPEC2Year);
                Compare(OPEC, YearsAgo(2), OPEC2Year, "notFX");


                TextView VenTV = (TextView) myView.findViewById(R.id.VenVal);
                VenTV.setText(Html.fromHtml("$" + numberFormat.format(GetLatestNonZeroValue(Ven, DateFormat.format(new Date()))) + "<small><small><small><small> / " + mContext.getString(R.string.barrel) + "</small></small></small>"));

                TextView VenMonth = (TextView) myView.findViewById(R.id.VenMonth);
                Compare(Ven, MonthsAgo(1), VenMonth, "notFX");

                TextView VenYear = (TextView) myView.findViewById(R.id.VenYear);
                Compare(Ven, YearsAgo(1), VenYear, "notFX");

                TextView Ven2Year = (TextView) myView.findViewById(R.id.Ven2Year);
                Compare(Ven, YearsAgo(2), Ven2Year, "notFX");


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
                yAxis.setDefaultRange(new NumberRange(0.0, 1.05 * Collections.max(Brent.values())));
                yAxis.setTitle(mContext.getString(R.string.oil_prices) + " ($ / " + mContext.getString(R.string.barrel) + ")");
                yAxis.setRangePaddingHigh(100.0);
                yAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
                yAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
                yAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
                yAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
                yAxis.getTickMarkClippingModeHigh();
                shinobiChart.addYAxis(yAxis);

                final LineSeries WTILine = new LineSeries();
                WTILine.getStyle().setLineColor(Color.YELLOW);
                WTILine.getStyle().setLineWidth((float) 2);
                shinobiChart.addSeries(WTILine);

                final LineSeries BrentLine = new LineSeries();
                BrentLine.getStyle().setLineColor(Color.BLUE);
                BrentLine.getStyle().setLineWidth((float) 2);
                shinobiChart.addSeries(BrentLine);

                final LineSeries VenLine = new LineSeries();
                VenLine.getStyle().setLineColor(Color.GREEN);
                VenLine.getStyle().setLineWidth((float) 2);
                shinobiChart.addSeries(VenLine);

                final LineSeries OPECLine = new LineSeries();
                OPECLine.getStyle().setLineColor(Color.CYAN);
                OPECLine.getStyle().setLineWidth((float) 2);
                shinobiChart.addSeries(OPECLine);

                LinkedHashMap[] HMArray = {WTI, Brent, Ven, OPEC};

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

