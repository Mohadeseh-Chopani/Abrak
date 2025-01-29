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
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abrak.network.api.ApiServiceProvider
import com.example.abrak.data.models.WeatherData
import com.example.abrak.R
import com.example.abrak.data.models.ForecastWeatherData
import com.example.abrak.data.repository.ImageLoadServiceImp
import com.example.abrak.ui.View.adapter.ForecastAdapter
import com.example.abrak.ui.viewModel.WeatherViewModel
import com.example.abrak.ui.viewModel.WeatherViewModelFactory
import com.example.abrak.utils.NetworkState
import com.google.android.gms.location.*
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var currentTemperature: TextView
    private lateinit var currentIcon: ImageView
    private lateinit var btnSearch: ImageView
    private lateinit var currentDescription: TextView
    private lateinit var countryName: TextView
    private lateinit var currentTime: TextView
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var recyclerViewForecast: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var loading: LinearLayout
    private lateinit var progressBarForecast: ProgressBar
    private lateinit var searchNotFoundLayout: View
    private lateinit var itemHolder: View
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
//    private val weatherViewModel: WeatherViewModel by viewModel()
    private var cityName: String? = null

    private val imageLoad: ImageLoadServiceImp by inject()
    private val weatherViewModel: WeatherViewModel by viewModel()

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
        setContentView(R.layout.activity_main)

        initializeViews()
        initializeWeatherViewModel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationRequest()

        btnSearch.setOnClickListener {
            cityName = searchEditText.text.toString()
            if (!cityName.isNullOrBlank()) {
                fetchWeatherByCity(cityName!!)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        fetchWeatherByCity("تهران")

        checkLocationPermissions()
    }

    private fun initializeViews() {
        currentTemperature = findViewById(R.id.current_temperature)
        currentIcon = findViewById(R.id.current_icon)
        currentDescription = findViewById(R.id.current_description)
        currentTime = findViewById(R.id.current_time)
        countryName = findViewById(R.id.country_name)
        btnSearch = findViewById(R.id.searchBtn)
        searchEditText = findViewById(R.id.search_edit_text)
        recyclerViewForecast = findViewById(R.id.recyclerview_forecast)
        loading = findViewById(R.id.loading)
        searchNotFoundLayout = findViewById(R.id.search_not_found)
        itemHolder = findViewById(R.id.item_holder)
        progressBarForecast = findViewById(R.id.progressBar_forecast)
    }

    private fun initializeWeatherViewModel() {
//        weatherViewModel = ViewModelProvider(
//            this,
//            WeatherViewModelFactory(ApiServiceProvider.getApiService())
//        ).get(WeatherViewModel::class.java)
//        val weatherViewModel: WeatherViewModel by inject()
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
                        itemHolder.visibility = View.VISIBLE
                        searchNotFoundLayout.visibility = View.GONE
                        currentTemperature.text = "${result.main.temp.toInt()} °C"
                        currentDescription.text = result.weather.get(0).description
                        countryName.text = "${result.sys.country} / $city"
                        currentTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                        imageLoad.loadImage(currentIcon,
                            "${ApiServiceProvider.BASE_URL}?token=${ApiServiceProvider.API_KEY}&action=icon&id=${result.weather.get(0).icon}"
                        )

                        weatherViewModel.getProgressBarCurrentVisible().observe(this, { status ->
                            showProgressBarCurrent(status)
                        })
                    } ?: run {
                        itemHolder.visibility = View.GONE
                        searchNotFoundLayout.visibility = View.VISIBLE
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
                    data?.forEach{ weatherItem ->
                            val date: List<String> = weatherItem.dt_txt.split(" ")
                            if (date[0] == todayTime)
                                result.add(weatherItem)

                        forecastAdapter = ForecastAdapter(result)
                        recyclerViewForecast.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        recyclerViewForecast.adapter = forecastAdapter

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

    override fun onDestroy() {
        compositeCurrentData?.clear()
        compositeForecastData?.clear()
        super.onDestroy()
    }

    private fun showProgressBarCurrent(status: Boolean) {
        if (status)
            loading.visibility = View.VISIBLE
        else
            loading.visibility = View.GONE
    }

    private fun showProgressBarForecast(status: Boolean) {
        if (status)
            progressBarForecast.visibility = View.VISIBLE
        else
            progressBarForecast.visibility = View.GONE
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        var compositeCurrentData: CompositeDisposable? = null
        var compositeForecastData: CompositeDisposable? = null

        fun setCompositeDisposableCurrent(compositeDisposable: CompositeDisposable) {
            compositeCurrentData = compositeDisposable
        }

        fun setCompositeDisposableForecast(compositeDisposable: CompositeDisposable) {
            compositeForecastData = compositeDisposable
        }
    }
}
