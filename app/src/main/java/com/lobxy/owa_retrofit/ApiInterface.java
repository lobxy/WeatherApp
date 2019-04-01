package com.lobxy.owa_retrofit;

import com.lobxy.owa_retrofit.Model.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
//    https://samples.openweathermap.org/data/2.5/weather?q=London&appid=b6907d289e10d714a6e88b30761fae22

    @GET("weather")
    Call<Model> getData(@Query("q") String name, @Query("appid") String appId, @Query("units") String metric);

}
