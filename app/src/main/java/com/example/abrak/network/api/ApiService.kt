package com.example.abrak.network.api

import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("?action=current")
    fun getCurrentData(
        @Query("token") token: String,
        @Query("city") city: String
    ): Single<CurrentWeatherData>

    @GET("?action=hourly")
    fun getForecastData(
        @Query("token") token: String,
        @Query("city") city: String
    ): Single<ForecastWeatherData>

}