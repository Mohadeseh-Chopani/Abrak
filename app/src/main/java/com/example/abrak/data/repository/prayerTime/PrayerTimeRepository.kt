package com.example.abrak.data.repository.prayerTime

import com.example.abrak.data.models.PrayerTimeData
import io.reactivex.rxjava3.core.Single

interface PrayerTimeRepository {
    fun getPrayerTime(token: String, city: String): Single<PrayerTimeData>

}