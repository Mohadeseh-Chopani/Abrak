package com.example.abrak.network.api.prayerTimeAPI

import com.example.abrak.data.models.PrayerTimeData
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerTimeApiService {
    @GET("?en_num=true")
     fun getPrayerTime(
        @Query("token") token: String,
        @Query("city") city: String
    ): Single<PrayerTimeData>

}