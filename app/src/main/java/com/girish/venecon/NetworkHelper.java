package com.girish.venecon;

import android.util.Log;

import com.girish.venecon.api.ApiService;
import com.girish.venecon.api.models.BitcoinData;
import com.girish.venecon.api.models.CrudeProductionData;
import com.girish.venecon.api.models.ExchangeData;
import com.girish.venecon.api.models.InflationData;
import com.girish.venecon.api.models.M2Data;
import com.girish.venecon.api.models.MWData;
import com.girish.venecon.api.models.OilData;
import com.girish.venecon.api.models.ReserveData;
import com.girish.venecon.api.models.UsOilData;
import com.girish.venecon.utils.Constants;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by girish on 05/10/2016.
 */

public class NetworkHelper {

    public static final String TAG = "Firebase token";
    private final Retrofit retrofit;

    public NetworkHelper() {
        // This is the interceptor that logs all the requests
        // Keep in mind there's different interceptors we can add,
        // A good example is unauthorized interceptor, that checks each response
        // And if it's 302 (or whichever error code API returns when your login token expires)
        // We will log out the user, prevent him from seeing anything other than login screen
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    public void getExchangeDataRetrofit(final OnDataCallback<List<ExchangeData>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<ExchangeData>> call = apiService.getExchangeData();
        processCall(call, onDataCallback);
    }

    public void getReservesDataRetrofit(final OnDataCallback<List<ReserveData>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<ReserveData>> call = apiService.getReserveData();
        processCall(call, onDataCallback);
    }

    public void getM2DataRetrofit(final OnDataCallback<List<M2Data>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<M2Data>> call = apiService.getM2Data();
        processCall(call, onDataCallback);
    }

    public void getBitcoinDataRetrofit(final OnDataCallback<List<BitcoinData>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<BitcoinData>> call = apiService.getBitcoinData();
        processCall(call, onDataCallback);
    }

    public void getOilDataRetrofit(final OnDataCallback<List<OilData>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<OilData>> call = apiService.getOilData();
        processCall(call, onDataCallback);
    }

    public void getMwDataRetrofit(final OnDataCallback<List<MWData>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<MWData>> call = apiService.getMwData();
        processCall(call, onDataCallback);
    }

    public void getInflationDataRetrofit(final OnDataCallback<List<InflationData>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<InflationData>> call = apiService.getInflationData();
        processCall(call, onDataCallback);
    }

    public void getCrudeProductionDataRetrofit(final OnDataCallback<List<CrudeProductionData>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<CrudeProductionData>> call = apiService.getCrudeProductionData();
        processCall(call, onDataCallback);
    }

    public void getUsOilDataRetrofit(final OnDataCallback<List<UsOilData>> onDataCallback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<UsOilData>> call = apiService.getUsOilData();
        processCall(call, onDataCallback);
    }

    public void saveDeviceToken(final String refreshedToken) {
        ApiService apiService = retrofit.create(ApiService.class);
        String language = Locale.getDefault().getLanguage();
        Call<Void> call = apiService.saveToken(refreshedToken, language);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token saved successfully: " + refreshedToken);
                } else {
                    Log.e(TAG, "Response is not successful. Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Failure called.");
                t.printStackTrace();
            }
        });
    }

    // Since all the calls are processed exactly the same way,
    // We can extract it to a separate generic method like this
    // I invented this right now, so I learned something new which is great :D
    private <T> void processCall(Call<T> call, final OnDataCallback<T> onDataCallback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                if (response.isSuccessful()) {
                    onDataCallback.onSuccess(response.body());
                } else {
                    onDataCallback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                onDataCallback.onFailure(t.getLocalizedMessage());
            }
        });
    }

    // Very nice concept, generic interface, we use any type T that can be a custom object, a List<SomeClass> etc,
    // 1 interface for all callbacks. You'll see it's usage everywhere. Before I learned this, I created a separate
    // interface for each different response, making it take a parameter of response model class. This is way better
    public interface OnDataCallback<T> {
        void onSuccess(T data);

        void onFailure(String message);
    }
}

