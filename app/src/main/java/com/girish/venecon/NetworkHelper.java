package com.girish.venecon;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by girish on 05/10/2016.
 */

public class NetworkHelper {

    public String getHTTP(String url) {
        OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getExchangeData(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_fx&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8"); // 20161014 When I change this to 2012 it doesn't work!
    }

    public String getReservesData(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_res&format=json&start=2010-10-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8"); // added this key on 20171110
    }

    public String getM2Data(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_m2&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8");
    }

    public String getBitcoinData(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_fx&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8");
    }

    public String getOilData(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_oil&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8");
    }

    public String getMWData(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_mw&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8");
    }

    public String getInflationData(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_inf2&format=json&start=2008-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8");
    }

    public String getCrudeProductionData(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_crudeproduction&format=json&start=2002-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8");
    }

    public String getUSOilData(){
        return getHTTP("https://api.venezuelaecon.com/output.php?table=ve_US&format=json&start=2002-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8");
    }


}

