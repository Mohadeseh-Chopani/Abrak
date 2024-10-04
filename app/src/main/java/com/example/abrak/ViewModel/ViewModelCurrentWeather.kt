package com.example.abrak.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abrak.Api.ApiService
import com.example.abrak.Api.ApiServiceProvider
import com.example.abrak.Data.CurrentWeatherData
import com.example.abrak.Data.ForecastWeatherData
import retrofit2.Call
import retrofit2.Response

class ViewModelCurrentWeather(private val apiService: ApiService, private val cityName: String) : ViewModel() {

    private var currentLiveData: MutableLiveData<CurrentWeatherData> = MutableLiveData()
    private var forecastLiveData: MutableLiveData<ForecastWeatherData> = MutableLiveData()
    private val progressBarLiveData = MutableLiveData<Boolean>()

    fun getCurrentWeather(): MutableLiveData<CurrentWeatherData> {
        apiService.getCurrentData(ApiServiceProvider.API_KEY, city = "تهران")
            .enqueue(object : retrofit2.Callback<CurrentWeatherData?> {
                override fun onResponse(call: Call<CurrentWeatherData?>, response: Response<CurrentWeatherData?>) {
                    currentLiveData.value = response.body()
                    Log.i("request", "onResponse: ")
                    setProgressBarVisible(true)
                }

                override fun onFailure(call: Call<CurrentWeatherData?>, t: Throwable) {
                    // Handle failure
                    Log.i("request", "onFailure: " + t.message)
                    setProgressBarVisible(false)
                }
            })
        return currentLiveData
    }

    fun getForecastWeather(): MutableLiveData<ForecastWeatherData> {
        apiService.getForecastData(ApiServiceProvider.API_KEY, city = "تهران")
            .enqueue(object : retrofit2.Callback<ForecastWeatherData?> {
                override fun onResponse(call: Call<ForecastWeatherData?>, response: Response<ForecastWeatherData?>) {
                    forecastLiveData.value = response.body()
                }

                override fun onFailure(call: Call<ForecastWeatherData?>, t: Throwable) {
                    Log.i("request", "onFailure: " + t.message)
                }

            })
        return forecastLiveData
    }

    fun getProgressBarVisible(): LiveData<Boolean> {
        return progressBarLiveData
    }

    fun setProgressBarVisible(visible: Boolean) {
        progressBarLiveData.setValue(visible)
    }


//    fun changeData(weather: MutableLiveData<WeatherResponse>): LiveData<String> {
//
//        return weather.map { weather -> weather?.result?.main?.temp?.toString() + "C°" }
//    }
}