package com.example.abrak.di

import android.app.Application
import com.example.abrak.data.dataSource.remote.prayerTime.PrayerTimeRemoteDataSourceImp
import com.example.abrak.data.dataSource.remote.weather.WeatherRemoteDataSourceImp
import com.example.abrak.data.repository.imageLoad.ImageLoadServiceImp
import com.example.abrak.data.repository.prayerTime.PrayerTimeRepositoryImp
import com.example.abrak.data.repository.weather.WeatherRepositoryImp
import com.example.abrak.network.api.prayerTimeAPI.PrayerTimeApiServiceProvider
import com.example.abrak.network.api.weatherAPI.WeatherApiServiceProvider
import com.example.abrak.ui.viewModel.weather.WeatherViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class MyApp: Application() {


    override fun onCreate() {
        super.onCreate()

        val myModules = module {
            singleOf(WeatherApiServiceProvider::getApiService)
            singleOf(::ImageLoadServiceImp)
            // Define RemoteDataSource
            factoryOf(::WeatherRemoteDataSourceImp)

            // Define Repository
            factoryOf(::WeatherRepositoryImp)

            // Define ViewModel
            viewModelOf(::WeatherViewModel)


            singleOf(PrayerTimeApiServiceProvider::getApiService)
            factoryOf(::PrayerTimeRemoteDataSourceImp)
            factoryOf(::PrayerTimeRepositoryImp)
        }


        startKoin {
            // Log Koin into Android logger
            androidLogger()
            androidContext(this@MyApp)
            modules(listOf(myModules))
        }
    }
}