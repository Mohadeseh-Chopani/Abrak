package com.example.abrak.data.repository

import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.dataSource.remote.RemoteDataSource
import io.reactivex.rxjava3.core.Single

class RepositoryImp(val remoteDataSource: RemoteDataSource): CurrentWeatherRepository {
    override fun getCurrentData(token: String, city: String): Single<CurrentWeatherData>  = remoteDataSource.getCurrentData()
}