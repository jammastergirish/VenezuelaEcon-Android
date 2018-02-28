package com.girish.venecon.api;


import com.girish.venecon.api.models.BitcoinData;
import com.girish.venecon.api.models.ExchangeData;
import com.girish.venecon.api.models.M2Data;
import com.girish.venecon.api.models.ReserveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("output.php?table=ve_fx&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<ExchangeData>> getExchangeData();
    @GET("output.php?table=ve_res&format=json&start=2010-10-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<ReserveData>> getReserveData();
    @GET("output.php?table=ve_m2&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<M2Data>> getM2Data();
    @GET("output.php?table=ve_fx&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD")
    Call<List<BitcoinData>> getBitcoinData();
}
