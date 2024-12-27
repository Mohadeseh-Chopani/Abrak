package com.example.abrak.data.repository

import android.util.Log
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.dataSource.remote.WeatherRemoteDataSourceImp
import com.example.abrak.data.models.ForecastWeatherData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable

class WeatherRepositoryImp(private val weatherRemoteDataSourceImp: WeatherRemoteDataSourceImp) : WeatherRepository {
    private var currentCompositeDisposable: CompositeDisposable? = null
    private var forecastCompositeDisposable: CompositeDisposable? = null
    override fun getCurrentData(token: String, city: String): Single<CurrentWeatherData> {
        return weatherRemoteDataSourceImp.getCurrentData(token, city)
            .doOnSubscribe { disposable -> currentCompositeDisposable?.add(disposable) }
            .doOnSuccess {  }
            .doOnError { Log.i("request", "getCurrentData: ") }
            .doFinally {  }

    }

    override fun getForecastData(token: String, city: String): Single<ForecastWeatherData> {
        return weatherRemoteDataSourceImp.getForecastData(token, city)
            .doOnSubscribe { disposable -> forecastCompositeDisposable?.add(disposable) }
            .doOnSuccess { /* Optional: Log start */ }
            .doFinally { /* Optional: Log cleanup */ }
    }

    fun clearDisposables() {
        currentCompositeDisposable?.let { it.clear() }
        forecastCompositeDisposable?.let { it.clear() }
    }
}