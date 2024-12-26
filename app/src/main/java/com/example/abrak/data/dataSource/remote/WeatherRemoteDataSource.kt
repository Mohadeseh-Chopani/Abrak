package com.example.abrak.data.dataSource.remote

import androidx.lifecycle.LiveData
import com.example.abrak.data.models.CurrentWeatherData

interface WeatherRemoteDataSource {
     fun getCurrentData(token: String, city: String): LiveData<CurrentWeatherData>
}