package com.girish.venecon.api;


import com.girish.venecon.api.models.BitcoinData;
import com.girish.venecon.api.models.CrudeProductionData;
import com.girish.venecon.api.models.ExchangeData;
import com.girish.venecon.api.models.InflationData;
import com.girish.venecon.api.models.M2Data;
import com.girish.venecon.api.models.MWData;
import com.girish.venecon.api.models.OilData;
import com.girish.venecon.api.models.ReserveData;
import com.girish.venecon.api.models.UsOilData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("output.php?table=ve_fx&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<ExchangeData>> getExchangeData();
    @GET("output.php?table=ve_res&format=json&start=2010-10-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<ReserveData>> getReserveData();
    @GET("output.php?table=ve_m2&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<M2Data>> getM2Data();
    @GET("output.php?table=ve_fx&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD")
    Call<List<BitcoinData>> getBitcoinData();
    @GET("output.php?table=ve_oil&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<OilData>> getOilData();
    @GET("output.php?table=ve_mw&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<MWData>> getMwData();
    @GET("output.php?table=ve_inf2&format=json&start=2008-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<InflationData>> getInflationData();
    @GET("output.php?table=ve_crudeproduction&format=json&start=2002-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<CrudeProductionData>> getCrudeProductionData();
    @GET("output.php?table=ve_US&format=json&start=2002-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<UsOilData>> getUsOilData();
    @GET("notifications.php")
    Call<Void> saveToken(@Query("id") String refreshedToken, @Query("lan") String languageCode);
}
