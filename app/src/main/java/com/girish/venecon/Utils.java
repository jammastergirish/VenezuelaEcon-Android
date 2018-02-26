package com.girish.venecon;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by girish on 09/10/2016.
 */

public class Utils {

    public static Context mContext;

    public static Double PercDiff(Double OldVal, Double NewVal)
    {
        return 100*(OldVal-NewVal)/OldVal;
    }


    public static Double DevalDiff(Double OldVal, Double NewVal)
    {
        return 100*((1/OldVal)-(1/NewVal))/(1/OldVal);
    }




    public static Double GetLatestNonZeroValue(HashMap<String, Double> HM, String dateString)
    {
        Double value = HM.get(dateString);
        if ((value!=null)&&(value!=0)) {
            return value;
        }
        else
        {
            SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date dateDate;
            try {
                dateDate = DateFormat.parse(dateString);
                System.out.println(dateDate);
            } catch (ParseException e) {
                System.out.println("Unparseable using " + DateFormat);
                dateDate = null;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(dateDate);
            cal.add(Calendar.DAY_OF_YEAR,-1);
            Date DayBeforeDate = cal.getTime();

            value = HM.get(DateFormat.format(DayBeforeDate));
            if ((value!=null)&&(value!=0))
            {
                return value;
            }
            else {
                return GetLatestNonZeroValue(HM, DateFormat.format(DayBeforeDate));
            }
        }
    }





    public static String Today()
    {
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return DateFormat.format(new Date());
    }


    public static String DaysAgo(int num)
    {
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DateFormat.parse(Today()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DAY_OF_YEAR,-1*num);
        return DateFormat.format(cal.getTime());
    }

    public static String WeeksAgo(int num)
    {
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DateFormat.parse(Today()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DAY_OF_YEAR,-7*num);
        return DateFormat.format(cal.getTime());
    }

    public static String MonthsAgo(int num)
    {
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DateFormat.parse(Today()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DAY_OF_YEAR,-30*num);
        return DateFormat.format(cal.getTime());
    }

    public static String YearsAgo(int num)
    {
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DateFormat.parse(Today()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.YEAR,-num);
        return DateFormat.format(cal.getTime());
    }

    public static void Compare(HashMap hm, String date, TextView textview, String type)
    {
        String ComparisonString = "";

        if (date.equals(DaysAgo(1)))
        {
            ComparisonString = mContext.getString(R.string.yesterday);
        }
        if (date.equals(WeeksAgo(1)))
        {
            ComparisonString = mContext.getString(R.string.week);
        }
        if (date.equals(MonthsAgo(1)))
        {
            ComparisonString = mContext.getString(R.string.month);
        }
        if (date.equals(YearsAgo(1)))
        {
            ComparisonString = mContext.getString(R.string.year);
        }
        if (date.equals(YearsAgo(2)))
        {
            ComparisonString = mContext.getString(R.string.years2);
        }
        if (date.equals(YearsAgo(3)))
        {
            ComparisonString = mContext.getString(R.string.years3);
        }
        if (date.equals(YearsAgo(4)))
        {
            ComparisonString = mContext.getString(R.string.years4);
        }
        if (date.equals(YearsAgo(5)))
        {
            ComparisonString = mContext.getString(R.string.years5);
        }
        if (date.equals(YearsAgo(6)))
        {
            ComparisonString = mContext.getString(R.string.years6);
        }
        if (date.equals(YearsAgo(7)))
        {
            ComparisonString = mContext.getString(R.string.years7);
        }

        Double Comparison = GetLatestNonZeroValue(hm, date);

        DecimalFormat numberFormat = new DecimalFormat("0.00");

        if (type=="FX")
        {
            if (GetLatestNonZeroValue(hm,Today())==Comparison)
            {
                textview.setText("Same as " + ComparisonString);
            }
            else if (GetLatestNonZeroValue(hm,Today())>Comparison)
            {
                textview.setText(Html.fromHtml("BsF <font color=red>&#x25BC;</font> " + String.valueOf(numberFormat.format(Math.abs(DevalDiff(Comparison,GetLatestNonZeroValue(hm,Today()))))+"%") + " " + mContext.getString(R.string.in) + " " + ComparisonString));
            }
            else if (GetLatestNonZeroValue(hm,Today())<Comparison)
            {
                textview.setText(Html.fromHtml("BsF <font color=green>&#x25B2;</font> "+ String.valueOf(numberFormat.format(Math.abs(DevalDiff(Comparison,GetLatestNonZeroValue(hm,Today())))))+"%") + " " + mContext.getString(R.string.in) + " " + ComparisonString);
            }
        }
        else
        {
            if (GetLatestNonZeroValue(hm,Today())==Comparison)
            {
                textview.setText("Same as " + ComparisonString);
            }
            else if (GetLatestNonZeroValue(hm,Today())<Comparison)
            {
                textview.setText(Html.fromHtml("<font color=red>&#x25BC;</font> " + String.valueOf(numberFormat.format(Math.abs(PercDiff(Comparison,GetLatestNonZeroValue(hm,Today()))))+"%") + " " + mContext.getString(R.string.in) + " " + ComparisonString));
            }
            else if (GetLatestNonZeroValue(hm,Today())>Comparison)
            {
                textview.setText(Html.fromHtml("<font color=green>&#x25B2;</font> "+ String.valueOf(numberFormat.format(Math.abs(PercDiff(Comparison,GetLatestNonZeroValue(hm,Today())))))+"%") + " " + mContext.getString(R.string.in) + " " + ComparisonString);
            }
        }
    }



    public static Double AnnualInflation(int Year, HashMap<String, Double> HM) // written 20161024
    {
//        if (Year != 1)
//        {
        Double Old = HM.get(String.valueOf(Year-1)+"-12-31");
        Double New = HM.get(String.valueOf(Year)+"-12-31");
        return PercDiff(Old, New);
//        }
//        else
//        {
//            let old : Double = source[String(Year-1)+"-12-31"]!
//                let new : Double = source[Utils.shared.Today()]!
//            return abs(Utils.shared.PercDiff(old, new: new))
//        }
    }





}
