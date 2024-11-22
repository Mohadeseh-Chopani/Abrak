package com.example.abrak.View

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abrak.Adapter.ForecastAdapter
import com.example.abrak.Api.ApiServiceProvider
import com.example.abrak.Data.WeatherData
import com.example.abrak.R
import com.example.abrak.ViewModel.ViewModelWeather
import com.example.abrak.ViewModel.WeatherViewModelFactory
import com.google.android.gms.location.*
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private lateinit var viewModelWeather: ViewModelWeather
    private var cityName: String? = null

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
        viewModelWeather = ViewModelProvider(
            this,
            WeatherViewModelFactory(ApiServiceProvider.getApiService())
        ).get(ViewModelWeather::class.java)
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
        viewModelWeather.getCurrentWeather(city).observe(this) { liveData ->
            Log.d("requestTime", "fetchWeatherByCity: " + city)
            liveData.result?.let { result ->
                itemHolder.visibility = View.VISIBLE
                searchNotFoundLayout.visibility = View.GONE
                currentTemperature.text = "${result.main.temp.toInt()} °C"
                currentDescription.text = result.weather[0].description
                countryName.text = "${result.sys.country} / $city"
                currentTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                Picasso.get()
                    .load(
                        "${ApiServiceProvider.BASE_URL}?token=${ApiServiceProvider.API_KEY}&action=icon&id=${result.weather[0].icon}"
                    )
                    .into(currentIcon)

                viewModelWeather.getProgressBarCurrentVisible().observe(this, { status ->
                    showProgressBarCurrent(status)
                })
            } ?: run {
                itemHolder.visibility = View.GONE
                searchNotFoundLayout.visibility = View.VISIBLE
                showError("City not found")
            }
        }

        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayTime = formatter.format(calendar.time)

        viewModelWeather.getForecastWeather(city).observe(this) { liveData ->
            val result: MutableList<WeatherData> = ArrayList()
            Log.i("requestTime", "fetchWeatherByCity: ")
            liveData?.result?.list?.let { list ->
                for (i in 0 until list.size) {
                    val date: List<String> = list.get(i).dt_txt.split(" ")
                    if (date.get(0) == todayTime)
                        result.add(list.get(i))
                }

                forecastAdapter = ForecastAdapter(result)
                recyclerViewForecast.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewForecast.adapter = forecastAdapter

                viewModelWeather.getProgressBarForecastVisible().observe(this) { status ->
                    showProgressBarForecast(status)
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
            loading.visibility = View.GONE
        else
            loading.visibility = View.VISIBLE
    }

    private fun showProgressBarForecast(status: Boolean) {
        if (status)
            progressBarForecast.visibility = View.GONE
        else
            progressBarForecast.visibility = View.VISIBLE
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
