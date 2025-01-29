package com.example.abrak.data.dataSource.remote

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abrak.network.api.ApiService
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherRemoteDataSourceImp(val apiService: ApiService) : WeatherRemoteDataSource {

    @SuppressLint("CheckResult")
    override fun getCurrentData(token: String, city: String): Single<CurrentWeatherData> {
        return apiService.getCurrentData(token, city)
    }

    @SuppressLint("CheckResult")
    override fun getForecastData(token: String, city: String): Single<ForecastWeatherData> {
        return apiService.getForecastData(token, city)
    }
}
