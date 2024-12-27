package com.example.abrak.ui.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abrak.network.api.ApiServiceProvider
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import com.example.abrak.data.repository.WeatherRepositoryImp
import com.example.abrak.ui.View.activity.MainActivity
import com.example.abrak.utils.NetworkState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherViewModel(private val weatherRepositoryImp: WeatherRepositoryImp) : ViewModel() {

    private var currentLiveData: MutableLiveData<CurrentWeatherData> = MutableLiveData()
    private var forecastLiveData: MutableLiveData<ForecastWeatherData> = MutableLiveData()
    private val progressBarLiveData = MutableLiveData<Boolean>()
    private val progressBarForecastLiveData = MutableLiveData<Boolean>()

    init {

    }

    val _CurrentState = MutableLiveData<NetworkState<CurrentWeatherData>>()
    val CurrentState: LiveData<NetworkState<CurrentWeatherData>> get() = _CurrentState

    @SuppressLint("CheckResult")
    fun getCurrentWeather(cityName: String): MutableLiveData<CurrentWeatherData> {
        _CurrentState.value = NetworkState.Loading
        weatherRepositoryImp.getCurrentData(ApiServiceProvider.API_KEY, cityName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                _CurrentState.value = NetworkState.Success(response)
                Log.i("request", "onSuccess: ${cityName}")

            }, { throwable ->
                _CurrentState.value = NetworkState.Error(throwable)
                Log.i("request", "onFailure: ${throwable.message}")
            })

//        compositeDisposableCurrentData.add(disposableCurrentData)
//        MainActivity.setCompositeDisposableCurrent(compositeDisposableCurrentData)
        return currentLiveData
    }


    val _state = MutableLiveData<NetworkState<ForecastWeatherData>>()
    val state: LiveData<NetworkState<ForecastWeatherData>> get() = _state

    @SuppressLint("CheckResult")
    fun getForecastWeather(cityName: String): MutableLiveData<ForecastWeatherData>{
        _state.value = NetworkState.Loading
        weatherRepositoryImp.getForecastData(token = ApiServiceProvider.API_KEY, city = cityName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _state.value = NetworkState.Success(response)
                    Log.i("request", "onResponse: $cityName")
                },
                { throwable ->
                    _state.value = NetworkState.Error(throwable)
                    Log.i("request", "onFailure: ${throwable.message}")
                }
            )

//        compositeDisposableForecastLiveData.add(disposableForecastData)
//        MainActivity.setCompositeDisposableForecast(compositeDisposableForecastLiveData)

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

    override fun onCleared() {
        super.onCleared()
        weatherRepositoryImp.clearDisposables()
    }
//    fun changeData(weather: MutableLiveData<WeatherResponse>): LiveData<String> {
//
//        return weather.map { weather -> weather?.result?.main?.temp?.toString() + "C°" }
//    }
}