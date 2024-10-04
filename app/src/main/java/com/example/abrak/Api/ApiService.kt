package com.example.abrak.Api

import com.example.abrak.Data.CurrentWeatherData
import com.example.abrak.Data.ForecastWeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("?action=current")
    fun getCurrentData(
        @Query("token") token: String,
        @Query("city") city: String
    ): Call<CurrentWeatherData>

    @GET("?action=hourly")
    fun getForecastData(
        @Query("token") token: String,
        @Query("city") city: String
    ): Call<ForecastWeatherData>

}