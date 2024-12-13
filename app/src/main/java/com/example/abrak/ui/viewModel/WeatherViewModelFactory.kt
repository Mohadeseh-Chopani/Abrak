package com.example.abrak.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abrak.network.api.ApiService

@Suppress("UNCHECKED_CAST")
class WeatherViewModelFactory(val apiService: ApiService) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelWeather(apiService) as T
    }
}