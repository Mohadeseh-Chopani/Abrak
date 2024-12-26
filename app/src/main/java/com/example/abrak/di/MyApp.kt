package com.example.abrak.di

import android.app.Application
import com.example.abrak.data.dataSource.remote.WeatherRemoteDataSourceImp
import com.example.abrak.data.repository.WeatherRepositoryImp
import com.example.abrak.network.api.ApiServiceProvider
import com.example.abrak.ui.viewModel.WeatherViewModel
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
            singleOf(ApiServiceProvider::getApiService)

            // Define RemoteDataSource
            factoryOf(::WeatherRemoteDataSourceImp)

            // Define Repository
            factoryOf(::WeatherRepositoryImp)

            // Define ViewModel
            viewModelOf(::WeatherViewModel)
        }


        startKoin {
            // Log Koin into Android logger
            androidLogger()
            androidContext(this@MyApp)
            modules(listOf(myModules))
        }
    }
}