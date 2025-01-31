package com.example.abrak.ui.viewModel.weather

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abrak.network.api.weatherAPI.WeatherApiServiceProvider
import com.example.abrak.data.models.CurrentWeatherData
import com.example.abrak.data.models.ForecastWeatherData
import com.example.abrak.data.repository.weather.WeatherRepositoryImp
import com.example.abrak.utils.NetworkState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherViewModel(private val weatherRepositoryImp: WeatherRepositoryImp) : ViewModel() {

    private val progressBarLiveData = MutableLiveData<Boolean>()
    private val progressBarForecastLiveData = MutableLiveData<Boolean>()

    init {

    }

    val _CurrentState = MutableLiveData<NetworkState<CurrentWeatherData>>()
    val CurrentState: LiveData<NetworkState<CurrentWeatherData>> get() = _CurrentState

    @SuppressLint("CheckResult")
    fun getCurrentWeather(cityName: String) {
        _CurrentState.value = NetworkState.Loading
        weatherRepositoryImp.getCurrentData(WeatherApiServiceProvider.API_KEY, cityName)
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
    }


    val _state = MutableLiveData<NetworkState<ForecastWeatherData>>()
    val state: LiveData<NetworkState<ForecastWeatherData>> get() = _state

    @SuppressLint("CheckResult")
    fun getForecastWeather(cityName: String) {
        _state.value = NetworkState.Loading
        weatherRepositoryImp.getForecastData(token = WeatherApiServiceProvider.API_KEY, city = cityName)
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