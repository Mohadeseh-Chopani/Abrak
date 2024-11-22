package com.example.abrak.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abrak.Api.ApiService
import com.example.abrak.Api.ApiServiceProvider
import com.example.abrak.Data.CurrentWeatherData
import com.example.abrak.Data.ForecastWeatherData
import com.example.abrak.View.MainActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ViewModelWeather(private val apiService: ApiService) : ViewModel() {

    private var currentLiveData: MutableLiveData<CurrentWeatherData> = MutableLiveData()
    private var forecastLiveData: MutableLiveData<ForecastWeatherData> = MutableLiveData()
    private val progressBarLiveData = MutableLiveData<Boolean>()
    private val progressBarForecastLiveData = MutableLiveData<Boolean>()

    private val compositeDisposableCurrentData = CompositeDisposable()
    private val compositeDisposableForecastLiveData = CompositeDisposable()

    fun getCurrentWeather(cityName: String): MutableLiveData<CurrentWeatherData> {
        val disposableCurrentData = apiService.getCurrentData(ApiServiceProvider.API_KEY, cityName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    currentLiveData.value = response
                    Log.i("request", "onResponse: $cityName")
                    setProgressBarCurrentVisible(true)
                },
                { throwable ->
                    Log.i("request", "onFailure: ${throwable.message}")
                    setProgressBarCurrentVisible(false)
                }
            )
        compositeDisposableCurrentData.add(disposableCurrentData)
        MainActivity.setCompositeDisposableCurrent(compositeDisposableCurrentData)
        return currentLiveData
    }


    @SuppressLint("CheckResult")
    fun getForecastWeather(cityName: String): MutableLiveData<ForecastWeatherData> {

        val disposableForecastData = apiService.getForecastData(ApiServiceProvider.API_KEY, city = cityName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                forecastLiveData.value = response
                Log.i("request", "onResponse: $cityName")
                setProgressBarForecastVisible(true)

            }, { throwable ->
                Log.i("request", "onFailure: ${throwable.message}")
                setProgressBarForecastVisible(false)
            }
            )

        compositeDisposableForecastLiveData.add(disposableForecastData)
        MainActivity.setCompositeDisposableForecast(compositeDisposableForecastLiveData)
        return forecastLiveData
    }

    fun getProgressBarCurrentVisible(): LiveData<Boolean> {
        return progressBarLiveData
    }

    fun setProgressBarCurrentVisible(visible: Boolean) {
        progressBarLiveData.postValue(visible)
    }

    fun setProgressBarForecastVisible(visible: Boolean) {
        progressBarForecastLiveData.postValue(visible)
    }

    fun getProgressBarForecastVisible(): LiveData<Boolean> {
        return progressBarForecastLiveData
    }

//    fun changeData(weather: MutableLiveData<WeatherResponse>): LiveData<String> {
//
//        return weather.map { weather -> weather?.result?.main?.temp?.toString() + "C°" }
//    }
}