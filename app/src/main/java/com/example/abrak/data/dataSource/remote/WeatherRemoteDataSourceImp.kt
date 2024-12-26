package com.example.abrak.data.dataSource.remote

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abrak.network.api.ApiService
import com.example.abrak.data.models.CurrentWeatherData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherRemoteDataSourceImp(val apiService: ApiService) : WeatherRemoteDataSource {
    @SuppressLint("CheckResult")
    override fun getCurrentData(token: String, city: String): LiveData<CurrentWeatherData> {
        val liveData = MutableLiveData<CurrentWeatherData>()

        apiService.getCurrentData(token, city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    liveData.value = response
                    Log.i("request", "onResponse: $city")

                },
                { throwable ->
                    Log.i("request", "onFailure: ${throwable.message}")

                }
            )

        return liveData
    }
}
