package com.example.abrak.data.repository

import androidx.lifecycle.LiveData
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.dataSource.remote.WeatherRemoteDataSourceImp

class WeatherRepositoryImp(private val weatherRemoteDataSourceImp: WeatherRemoteDataSourceImp) : WeatherRepository {
    override fun getCurrentData(token: String, city: String): LiveData<CurrentWeatherData> {
        return weatherRemoteDataSourceImp.getCurrentData(token, city)
    }
}