package com.example.abrak.data.dataSource.remote.prayerTime

import com.example.abrak.data.models.PrayerTimeData
import io.reactivex.rxjava3.core.Single

interface PraterTimeRemoteDataSource {
    fun getPrayerTime(token: String, city: String): Single<PrayerTimeData>
}