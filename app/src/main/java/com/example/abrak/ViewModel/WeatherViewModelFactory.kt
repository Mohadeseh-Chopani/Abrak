package com.example.abrak.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abrak.Api.ApiService

@Suppress("UNCHECKED_CAST")
class WeatherViewModelFactory(val apiService: ApiService, val cityName: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelCurrentWeather(apiService, cityName) as T
    }
}