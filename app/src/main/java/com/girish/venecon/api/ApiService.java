package com.girish.venecon.api;


import com.girish.venecon.api.models.ExchangeData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("output.php?table=ve_fx&format=json&start=2010-01-01&key=dsCHiQDZc2HvYFNYrQrMhQOczHUDHAUSHD8")
    Call<List<ExchangeData>> getExchangeData();
}
