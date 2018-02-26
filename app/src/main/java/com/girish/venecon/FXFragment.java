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

import com.crashlytics.android.Crashlytics;
import com.shinobicontrols.charts.ChartView;
import com.shinobicontrols.charts.ChartFragment;
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

import static com.girish.venecon.Utils.Compare;
import static com.girish.venecon.Utils.GetLatestNonZeroValue;
import static com.girish.venecon.Utils.MonthsAgo;
import static com.girish.venecon.Utils.YearsAgo;
import static com.girish.venecon.Utils.mContext;
import com.google.android.gms.ads.InterstitialAd; // 20171129
import com.google.android.gms.ads.AdRequest;
import android.os.*;
import android.widget.Toast;

/**
 * Created by girish on 02/10/2016.
 */

public class FXFragment extends Fragment {
    //public static String FRAGMENT_NAME = "Exchange Rates";

    View myView;

    public FXFragment() {

        GregorianCalendar calendar = new GregorianCalendar(2012, Calendar.JANUARY, 1);
        Date rangeMin = calendar.getTime();
        calendar.set(2017, Calendar.DECEMBER, 31);
        Date rangeMax = calendar.getTime();
        xDefaultRange = new DateRange(rangeMin, rangeMax);
    }






    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    myView = inflater.inflate(R.layout.fx_layout, container, false);

        //https://developers.google.com/admob/android/interstitial 20171129
        final InterstitialAd mInterstitialAd = new InterstitialAd(getActivity()); // https://stackoverflow.com/questions/37685388/getting-an-interstitial-ad-to-display-from-a-fragment
        mInterstitialAd.setAdUnitId("ca-app-pub-7175811277195688/6615701473");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        new Handler().postDelayed(new Runnable() { //https://stackoverflow.com/questions/17121248/missing-the-android-os-handler-object-from-android-studio https://stackoverflow.com/questions/31041884/execute-function-after-5-seconds-in-android
            @Override
            public void run() {
                //Crashlytics.getInstance().crash(); // Force a crash
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
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
            String UnparsedResult = NetHelp.getExchangeData();
            return UnparsedResult;
    }

    @Override
    protected void onPostExecute(String s) {

        if (TextUtils.isEmpty(s))
        {
            Toast.makeText(getActivity(),
                    "Something went wrong. Please check your connection.", Toast.LENGTH_SHORT).show();
        }
        else
            {


            JSONArray jsonArray;
            JSONObject jsonObject;


            String jsonDate;
            String jsonOfficial;
            LinkedHashMap<String, Double> Official = new LinkedHashMap<String, Double>();
            String jsonSupp;
            LinkedHashMap<String, Double> Supp = new LinkedHashMap<String, Double>();
            String jsonSitme;
            LinkedHashMap<String, Double> Sitme = new LinkedHashMap<String, Double>();
            String jsonSicad1;
            LinkedHashMap<String, Double> Sicad1 = new LinkedHashMap<String, Double>();
            String jsonSicad2;
            LinkedHashMap<String, Double> Sicad2 = new LinkedHashMap<String, Double>();
            String jsonSimadi;
            LinkedHashMap<String, Double> Dicom = new LinkedHashMap<String, Double>();
            String jsonDicom;
            LinkedHashMap<String, Double> Simadi = new LinkedHashMap<String, Double>();
            String jsonM2_Res;
            LinkedHashMap<String, Double> M2_Res = new LinkedHashMap<String, Double>();
            String jsonBM;
            LinkedHashMap<String, Double> BM = new LinkedHashMap<String, Double>();

            try {
                jsonArray = new JSONArray(s);

                for (int i = 0; i < jsonArray.length(); i++) {

                    jsonObject = jsonArray.getJSONObject(i);
                    jsonDate = jsonObject.getString("date");


                    jsonOfficial = jsonObject.getString("official");
                    Official.put(jsonDate, Double.valueOf(jsonOfficial));

                    jsonSupp = jsonObject.getString("supp");
                    Supp.put(jsonDate, Double.valueOf(jsonSupp));

                    jsonSitme = jsonObject.getString("sitme");
                    Sitme.put(jsonDate, Double.valueOf(jsonSitme));

                    jsonSicad1 = jsonObject.getString("sicad1");
                    Sicad1.put(jsonDate, Double.valueOf(jsonSicad1));

                    jsonSicad2 = jsonObject.getString("sicad2");
                    Sicad2.put(jsonDate, Double.valueOf(jsonSicad2));

                    jsonSimadi = jsonObject.getString("simadi");
                    Simadi.put(jsonDate, Double.valueOf(jsonSimadi));

                    jsonDicom = jsonObject.getString("dicom");
                    Dicom.put(jsonDate, Double.valueOf(jsonDicom));

                    jsonM2_Res = jsonObject.getString("m2_res");
                    M2_Res.put(jsonDate, Double.valueOf(jsonM2_Res));

                    jsonBM = jsonObject.getString("bm");
                    BM.put(jsonDate, Double.valueOf(jsonBM));

                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Wasn't JSON
            }

            // System.out.print(String.valueOf(GetLatestNonZeroValue(M2_Res, "2016-10-06")));

            SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
            NumberFormat numberFormat = NumberFormat.getInstance();


//            TextView OfficialTV = (TextView) myView.findViewById(R.id.OfficialVal);
//            OfficialTV.setText(Html.fromHtml("" + numberFormat.format(GetLatestNonZeroValue(Official, DateFormat.format(new Date()))) + " <small><small><small><small>BsF/$</small></small></small></small>"));

            TextView DicomTV = (TextView) myView.findViewById(R.id.DicomVal);
            DicomTV.setText(Html.fromHtml("" + numberFormat.format(GetLatestNonZeroValue(Dicom, DateFormat.format(new Date()))) + " <small><small><small><small>BsF/$</small></small></small></small>"));

            TextView BlackMarketTV = (TextView) myView.findViewById(R.id.BlackMarketVal);
            BlackMarketTV.setText(Html.fromHtml("" + numberFormat.format(GetLatestNonZeroValue(BM, DateFormat.format(new Date()))) + " <small><small><small><small>BsF/$</small></small></small></small>"));

            TextView M2_ResTV = (TextView) myView.findViewById(R.id.M2_ResVal);
            M2_ResTV.setText(Html.fromHtml("" + numberFormat.format(GetLatestNonZeroValue(M2_Res, DateFormat.format(new Date()))) + " <small><small><small><small>BsF/$</small></small></small></small>"));


//        TextView SimadiYesterday = (TextView) myView.findViewById(R.id.SimadiYesterday);
//        Compare(Simadi, DaysAgo(1), SimadiYesterday, "FX");

            TextView DicomMonthAgo = (TextView) myView.findViewById(R.id.DicomMonth);
            Compare(Dicom, MonthsAgo(1), DicomMonthAgo, "FX");

//        TextView SimadiYearAgo = (TextView) myView.findViewById(R.id.SimadiYear);
//        Compare(Simadi, YearsAgo(1), SimadiYearAgo, "FX");


//        TextView BMYesterday = (TextView) myView.findViewById(R.id.BlackMarketYesterday);
//        Compare(BM, DaysAgo(1), BMYesterday, "FX");

            TextView BMMonthAgo = (TextView) myView.findViewById(R.id.BlackMarketMonth);
            Compare(BM, MonthsAgo(1), BMMonthAgo, "FX");

            TextView BMYearAgo = (TextView) myView.findViewById(R.id.BlackMarketYear);
            Compare(BM, YearsAgo(1), BMYearAgo, "FX");

            TextView BM2YearsAgo = (TextView) myView.findViewById(R.id.BlackMarket2Years);
            Compare(BM, YearsAgo(2), BM2YearsAgo, "FX");

            TextView BM3YearsAgo = (TextView) myView.findViewById(R.id.BlackMarket3Years);
            Compare(BM, YearsAgo(3), BM3YearsAgo, "FX");

            TextView BM4YearsAgo = (TextView) myView.findViewById(R.id.BlackMarket4Years);
            Compare(BM, YearsAgo(4), BM4YearsAgo, "FX");

            TextView BM5YearsAgo = (TextView) myView.findViewById(R.id.BlackMarket5Years);
            Compare(BM, YearsAgo(5), BM5YearsAgo, "FX");



            ChartView chartView = (ChartView) myView.findViewById(R.id.chart);
            ShinobiChart shinobiChart = chartView.getShinobiChart();


                //https://www.shinobicontrols.com/docs/android/shinobicharts/latest/user-guide/qs-draw-simple-chart.html 20171212 changing view to fragment
//
//                ChartFragment chartFragment =
//                        (ChartFragment) getFragmentManager().findFragmentById(R.id.chart);
//                ShinobiChart shinobiChart = chartFragment.getShinobiChart();


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
            yAxis.setDefaultRange(new NumberRange(0.0, 1.2 * Collections.max(BM.values())));
            yAxis.setTitle(mContext.getString(R.string.exchange_rate) + " (BsF/$)");
            yAxis.setRangePaddingHigh(100.0);
            yAxis.getStyle().setLineColor(Color.parseColor("#FFFFFF"));
            yAxis.getStyle().getTitleStyle().setTextColor(Color.parseColor("#FFFFFF"));
            yAxis.getStyle().getTickStyle().setLabelColor(Color.parseColor("#FFFFFF"));
            yAxis.getStyle().getTickStyle().setLineColor(Color.parseColor("#FFFFFF"));
            yAxis.getTickMarkClippingModeHigh();
            yAxis.setMajorTickFrequency(20000.0); // 20171128
            shinobiChart.addYAxis(yAxis);


            final LineSeries OfficialLine = new LineSeries();
            OfficialLine.getStyle().setLineColor(Color.GREEN);
            OfficialLine.getStyle().setLineWidth((float) 2);
            shinobiChart.addSeries(OfficialLine);

            final LineSeries SimadiLine = new LineSeries();
            SimadiLine.getStyle().setLineColor(Color.YELLOW);
            SimadiLine.getStyle().setLineWidth((float) 2);
            shinobiChart.addSeries(SimadiLine);

            final LineSeries DicomLine = new LineSeries();
            DicomLine.getStyle().setLineColor(Color.BLUE);
            DicomLine.getStyle().setLineWidth((float) 2);
            shinobiChart.addSeries(DicomLine);

            final LineSeries BMLine = new LineSeries();
            BMLine.getStyle().setLineColor(Color.RED);
            BMLine.getStyle().setLineWidth((float) 2);
            shinobiChart.addSeries(BMLine);

            final LineSeries M2_ResLine = new LineSeries();
            M2_ResLine.getStyle().setLineColor(Color.WHITE);
            M2_ResLine.getStyle().setLineWidth((float) 2);
            shinobiChart.addSeries(M2_ResLine);

            LinkedHashMap[] HMArray = {Official, Simadi, BM, M2_Res};

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


