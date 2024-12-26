package com.example.abrak.data.dataSource.remote

import androidx.lifecycle.LiveData
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import io.reactivex.rxjava3.core.Single

interface WeatherRemoteDataSource {
     fun getCurrentData(token: String, city: String): LiveData<CurrentWeatherData>
     fun getForecastData(token: String, city: String): LiveData<ForecastWeatherData>
}