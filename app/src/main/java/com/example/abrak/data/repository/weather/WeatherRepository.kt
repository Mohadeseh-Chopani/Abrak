package com.example.abrak.data.repository.weather

import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import io.reactivex.rxjava3.core.Single

interface WeatherRepository {
     fun getCurrentData(token: String, city: String): Single<CurrentWeatherData>
     fun getForecastData(token: String, city: String): Single<ForecastWeatherData>
}