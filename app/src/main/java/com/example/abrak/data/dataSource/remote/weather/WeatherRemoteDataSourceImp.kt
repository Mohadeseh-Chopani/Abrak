package com.example.abrak.data.dataSource.remote.weather

import android.annotation.SuppressLint
import com.example.abrak.network.api.weatherAPI.WeatherApiService
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import io.reactivex.rxjava3.core.Single

class WeatherRemoteDataSourceImp(val weatherApiService: WeatherApiService) :
    WeatherRemoteDataSource {

    @SuppressLint("CheckResult")
    override fun getCurrentData(token: String, city: String): Single<CurrentWeatherData> {
        return weatherApiService.getCurrentData(token, city)
    }

    @SuppressLint("CheckResult")
    override fun getForecastData(token: String, city: String): Single<ForecastWeatherData> {
        return weatherApiService.getForecastData(token, city)
    }
}
