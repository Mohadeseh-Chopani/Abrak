package com.example.abrak.ui.View.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abrak.data.models.PrayerTimeData
import com.example.abrak.data.models.WeatherData
import com.example.abrak.data.repository.imageLoad.ImageLoadServiceImp
import com.example.abrak.databinding.ActivityMainBinding
import com.example.abrak.network.api.weatherAPI.WeatherApiServiceProvider
import com.example.abrak.ui.View.adapter.ForecastAdapter
import com.example.abrak.ui.View.bottomNavigation.PrayerTimeFragment
import com.example.abrak.ui.viewModel.prayerTime.PrayerTimeViewModel
import com.example.abrak.ui.viewModel.weather.WeatherViewModel
import com.example.abrak.utils.NetworkState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var PrayerTimeLiveData: MutableLiveData<PrayerTimeData> = MutableLiveData()
    private var cityName: String? = null

    private lateinit var binding: ActivityMainBinding
    private val imageLoad: ImageLoadServiceImp by inject()
    private val weatherViewModel: WeatherViewModel by viewModel()
    private val prayerTimeViewModel: PrayerTimeViewModel by viewModel()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            Log.i("locationP", "Permissions granted")
            fetchUserLocation()
        } else {
            Log.w("locationP", "Location permissions denied")
            showError("Location access is required for weather updates.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationRequest()

        fetchWeatherByCity("تهران")
        fetchPrayerTimeByCity("تهران")
        checkLocationPermissions()

        binding.searchBtn.setOnClickListener {
            cityName = binding.searchEditText.text.toString()
            if (!cityName.isNullOrBlank()) {
                fetchWeatherByCity(cityName!!)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPrayer.setOnClickListener {
            val bottomSheet = PrayerTimeFragment(PrayerTimeLiveData)
            bottomSheet.show(supportFragmentManager, "MyBottomSheetFragment")
        }
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 100000L
        ).build()
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fetchUserLocation()
            }

            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchUserLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
//                    fetchCityNameAndWeather(lat, lon)
                } else {
                    Log.w("locationP", "Location is null")
                }
            }
            .addOnFailureListener {
                Log.e("locationP", "Failed to get location", it)
            }
    }

    private fun fetchCityNameAndWeather(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val city = addresses[0].locality
                    city?.let { fetchWeatherByCity("تهران") }
                } else {
                    Log.w("locationP", "No address found for coordinates")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchPrayerTimeByCity(city: String) {
        prayerTimeViewModel.getPrayerTime(city)
        prayerTimeViewModel.CurrentTime.observe(this) { state ->
            when (state) {
                is NetworkState.Loading -> {}
                is NetworkState.Success -> {
                    val data = state.data
                    PrayerTimeLiveData.value = data
                }

                is NetworkState.Error -> {}
            }
        }
    }

    private fun fetchWeatherByCity(city: String) {
        weatherViewModel.getCurrentWeather(cityName = city)
        weatherViewModel.CurrentState.observe(this) { state ->
            when (state) {
                is NetworkState.Loading -> {
                    showProgressBarCurrent(true)
                }

                is NetworkState.Success -> {
                    showProgressBarCurrent(false)
                    val data = state.data.result
                    data?.let { result ->
                        binding.itemHolder.visibility = View.VISIBLE
                        binding.searchNotFound.visibility = View.GONE
                        binding.currentTemperature.text = "${result.main.temp.toInt()} °C"
                        binding.currentDescription.text = result.weather.get(0).description
                        binding.countryName.text = "${result.sys.country} / $city"
                        binding.currentTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                        imageLoad.loadImage(
                            binding.currentIcon,
                            "${WeatherApiServiceProvider.BASE_URL}?token=${WeatherApiServiceProvider.API_KEY}&action=icon&id=${
                                result.weather.get(
                                    0
                                ).icon
                            }"
                        )

                        weatherViewModel.getProgressBarCurrentVisible().observe(this, { status ->
                            showProgressBarCurrent(status)
                        })
                    } ?: run {
                        binding.itemHolder.visibility = View.GONE
                        binding.searchNotFound.visibility = View.VISIBLE
                        showError("شهر مورد نظر یافت نشد")
                    }
                }

                is NetworkState.Error -> {
                    showProgressBarCurrent(false)
                    //showError message
                }
            }
        }


        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayTime = formatter.format(calendar.time)

        weatherViewModel.getForecastWeather(city)
        weatherViewModel.state.observe(this) { state ->
            when (state) {
                is NetworkState.Loading -> {
                    showProgressBarForecast(true)
                }

                is NetworkState.Success -> {
                    showProgressBarForecast(false)
                    val data = state?.data?.result?.list
                    val result: MutableList<WeatherData> = ArrayList()
                    Log.i("requestTime", "fetchWeatherByCity: ")
                    data?.forEach { weatherItem ->
                        val date: List<String> = weatherItem.dt_txt.split(" ")
                        if (date[0] == todayTime)
                            result.add(weatherItem)

                        forecastAdapter = ForecastAdapter(result)
                        binding.recyclerviewForecast.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerviewForecast.adapter = forecastAdapter

                        weatherViewModel.getProgressBarForecastVisible().observe(this) { status ->
                            showProgressBarForecast(status)
                        }
                    }
                }

                is NetworkState.Error -> {
                    showProgressBarForecast(false)
                    // Show error message
                }
            }
        }
    }

    private fun showProgressBarCurrent(status: Boolean) {
        if (status)
            binding.loading.visibility = View.VISIBLE
        else
            binding.loading.visibility = View.GONE
    }

    private fun showProgressBarForecast(status: Boolean) {
        if (status)
            binding.progressBarForecast.visibility = View.VISIBLE
        else
            binding.progressBarForecast.visibility = View.GONE
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
