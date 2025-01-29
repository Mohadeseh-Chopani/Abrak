package com.example.abrak.network.api

import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import com.example.abrak.data.models.PrayerTimeData
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerTimeApiService {
    @GET("?en_num=false")
     fun getPrayerTime(
        @Query("token") token: String,
        @Query("city") city: String
    ): Single<PrayerTimeData>

}