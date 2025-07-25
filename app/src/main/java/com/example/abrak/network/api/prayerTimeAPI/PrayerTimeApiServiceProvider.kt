package com.example.abrak.network.api.prayerTimeAPI

import com.example.abrak.network.api.weatherAPI.WeatherApiService
import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PrayerTimeApiServiceProvider {
    companion object {
        val BASE_URL = "https://one-api.ir/owghat/"

        // Create a lenient Gson instance
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val API_KEY: String = "643704:66e9936b03835"

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()


        fun getApiService(): PrayerTimeApiService {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build()

            val prayerTimeApiService: PrayerTimeApiService = retrofit.create(PrayerTimeApiService::class.java)

            return prayerTimeApiService
        }
    }
}