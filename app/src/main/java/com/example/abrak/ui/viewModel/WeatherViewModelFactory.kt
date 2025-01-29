package com.example.abrak.ui.viewModel

import androidx.lifecycle.ViewModelProvider
import com.example.abrak.network.api.WeatherApiService

@Suppress("UNCHECKED_CAST")
class WeatherViewModelFactory(val weatherApiService: WeatherApiService) : ViewModelProvider.Factory {

//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return WeatherViewModel(apiService) as T
//    }
}