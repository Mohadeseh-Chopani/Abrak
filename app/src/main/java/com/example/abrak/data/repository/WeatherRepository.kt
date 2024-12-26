package com.example.abrak.data.repository

import androidx.lifecycle.LiveData
import com.example.abrak.data.models.CurrentWeatherData

interface WeatherRepository {
     fun getCurrentData(token: String, city: String): LiveData<CurrentWeatherData>
}