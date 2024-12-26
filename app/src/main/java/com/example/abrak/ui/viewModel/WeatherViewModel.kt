package com.example.abrak.ui.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abrak.network.api.ApiServiceProvider
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import com.example.abrak.data.repository.WeatherRepositoryImp
import com.example.abrak.ui.View.activity.MainActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable

class WeatherViewModel(private val weatherRepositoryImp: WeatherRepositoryImp) : ViewModel() {

    private var currentLiveData: MutableLiveData<CurrentWeatherData> = MutableLiveData()
    private var forecastLiveData: MutableLiveData<ForecastWeatherData> = MutableLiveData()
    private val progressBarLiveData = MutableLiveData<Boolean>()
    private val progressBarForecastLiveData = MutableLiveData<Boolean>()

    private val compositeDisposableCurrentData = CompositeDisposable()
    private val compositeDisposableForecastLiveData = CompositeDisposable()

    init {

    }
    fun getCurrentWeather(cityName: String): MutableLiveData<CurrentWeatherData> {
        setProgressBarCurrentVisible(false)

        val data = weatherRepositoryImp.getCurrentData(ApiServiceProvider.API_KEY, cityName)
        data.observeForever { value ->
            currentLiveData.value = value
        }

//        compositeDisposableCurrentData.add(disposableCurrentData)
        MainActivity.setCompositeDisposableCurrent(compositeDisposableCurrentData)
        return currentLiveData
    }


    @SuppressLint("CheckResult")
    fun getForecastWeather(cityName: String): MutableLiveData<ForecastWeatherData> {
        val data = weatherRepositoryImp.getForecastData(token = ApiServiceProvider.API_KEY, city = cityName)
        data.observeForever { value ->
            forecastLiveData.value = value
        }

//        compositeDisposableForecastLiveData.add(disposableForecastData)
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