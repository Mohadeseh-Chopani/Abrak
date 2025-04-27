package com.example.abrak.data.models

import android.os.Parcelable
import java.io.Serializable

data class PrayerTimeData(
    val result: PrayerResult? = null
) : Serializable

data class PrayerResult(
    val city: String,
    val azan_sobh: String,
    val toloe_aftab: String,
    val azan_zohre: String,
    val ghorob_aftab: String,
    val azan_maghreb: String,
    val nime_shabe_sharie: String,
    val month: String,
    val day: String,
    val longitude: String,
    val latitude: String
)
