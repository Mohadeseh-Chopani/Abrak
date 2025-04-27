package com.example.abrak.data.dataSource.remote.prayerTime

import com.example.abrak.data.models.PrayerTimeData
import com.example.abrak.network.api.prayerTimeAPI.PrayerTimeApiService
import io.reactivex.rxjava3.core.Single

class PrayerTimeRemoteDataSourceImp(val prayerTimeApiService: PrayerTimeApiService): PraterTimeRemoteDataSource {
    override fun getPrayerTime(token: String, city: String): Single<PrayerTimeData> {
        return prayerTimeApiService.getPrayerTime(token, city)
    }
}