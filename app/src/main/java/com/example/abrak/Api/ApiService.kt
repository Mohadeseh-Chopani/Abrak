package com.example.abrak.Api

import com.example.abrak.Data.CurrentWeatherData
import com.example.abrak.Data.ForecastWeatherData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
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
    ): Observable<ForecastWeatherData>

}