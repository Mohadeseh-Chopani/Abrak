package com.example.abrak.data.repository

import androidx.lifecycle.LiveData
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.dataSource.remote.WeatherRemoteDataSourceImp
import com.example.abrak.data.models.ForecastWeatherData
import io.reactivex.rxjava3.core.Single

class WeatherRepositoryImp(private val weatherRemoteDataSourceImp: WeatherRemoteDataSourceImp) : WeatherRepository {
    override fun getCurrentData(token: String, city: String): LiveData<CurrentWeatherData> {
        return weatherRemoteDataSourceImp.getCurrentData(token, city)
    }

    override fun getForecastData(token: String, city: String): LiveData<ForecastWeatherData> {
        return weatherRemoteDataSourceImp.getForecastData(token, city)
    }
}