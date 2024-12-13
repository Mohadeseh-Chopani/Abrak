package com.example.abrak.data.dataSource.remote

import com.example.abrak.network.api.ApiService
import com.example.abrak.data.models.CurrentWeatherData
import io.reactivex.rxjava3.core.Single

class RemoteDataSource(apiService: ApiService): CurrentWeatherRemoteDataSource {
    override fun getCurrentData(token: String, city: String): Single<CurrentWeatherData> {
        TODO("Not yet implemented")
    }
}