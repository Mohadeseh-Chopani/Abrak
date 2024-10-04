package com.example.abrak.View

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abrak.Adapter.ForecastAdapter
import com.example.abrak.Api.ApiServiceProvider
import com.example.abrak.Data.WeatherData
import com.example.abrak.R
import com.example.abrak.ViewModel.ViewModelCurrentWeather
import com.example.abrak.ViewModel.WeatherViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var currentTemperature: TextView
    lateinit var currentIcon: ImageView
    lateinit var currentDescription: TextView
    lateinit var countryName: TextView
    lateinit var currentTime: TextView
    lateinit var forecastAdapter: ForecastAdapter
    lateinit var recyclerViewForecast: RecyclerView
    lateinit var searchEditText: EditText
    lateinit var loading: LinearLayout
    lateinit var locationManager: LocationManager
    var cityName: String = "tehran"
    lateinit var viewModelCurrentWeather: ViewModelCurrentWeather
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var isLocationUpdatesActive = false
    lateinit var locationCallback: LocationCallback

    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerViewForecast = findViewById(R.id.recyclerview_forecast)
        searchEditText = findViewById(R.id.search_edit_text)
        currentTemperature = findViewById(R.id.current_temperature)
        currentIcon = findViewById(R.id.current_icon)
        currentDescription = findViewById(R.id.current_description)
        currentTime = findViewById(R.id.current_time)
        countryName = findViewById(R.id.country_name)
        loading = findViewById(R.id.loading)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<FloatingActionButton>(R.id.btn_prayer).setOnClickListener {
            val bottomSheet = PrayerTimeFragment()
            bottomSheet.show(supportFragmentManager, "MyBottomSheetFragment")
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 100000 // Update interval in milliseconds
            fastestInterval = 50000 // Fastest updateinterval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        checkPermissions()
//        getUserLocation(baseContext)
    }

    private fun getUserLocation(mContext: Context) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1001
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val lat: Double = location!!.latitude
                val lon: Double = location!!.longitude
                getCityName(lat!!, lon!!)
                Log.d("locationP_", "getUserLocation: " + lat + "," + lon)
            }
        getWeatherInfoByUserLocation(cityName)
    }


    private fun getCityName(latitude: Double?, longitude: Double?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(baseContext, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 10)
                withContext(Dispatchers.Main) {
                    for (address in addresses!!) {
                        if (address != null) {
                            val city = address.locality // Use 'locality' for city name
                            getWeatherInfoByUserLocation(city)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace() // Handle the error appropriately
            }
        }
    }

    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = formatter.format(calendar.time)

    private fun getWeatherInfoByUserLocation(cityName: String) {
        viewModelCurrentWeather =
            ViewModelProvider(this, WeatherViewModelFactory(ApiServiceProvider.getApiService(), "tehran"))
                .get(ViewModelCurrentWeather::class.java)

        viewModelCurrentWeather.getCurrentWeather().observe(this, Observer { liveData ->
            val temperature: Int = liveData.result.main.temp.toInt()
            currentTemperature.setText(temperature.toString() + " C°")
            currentDescription.setText(liveData.result.weather.get(0).description)
            countryName.setText(liveData.result.sys.country + "/" + cityName)
            currentTime.setText(time)

            Picasso.get()
                .load(
                    ApiServiceProvider.BASE_URL + "?token=" + ApiServiceProvider.API_KEY + "&action=icon&id=" +
                            liveData.result.weather.get(0).icon
                )
                .into(currentIcon)

            viewModelCurrentWeather.getProgressBarVisible().observe(this, Observer { status ->
                showProgressBar(status)
            })
        })

        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayTime = formatter.format(calendar.time)

        viewModelCurrentWeather.getForecastWeather().observe(this) { liveData ->
            val result: MutableList<WeatherData> = ArrayList()

            for (i in 0 until liveData.result.list.size) {
                val date: List<String> = liveData.result.list.get(i).dt_txt.split(" ")
                if (date.get(0) == todayTime)
                    result.add(liveData.result.list.get(i))
            }

            forecastAdapter = ForecastAdapter(result)
            recyclerViewForecast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewForecast.adapter = forecastAdapter
        }
    }

    fun checkPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) &&
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            ) {
                // Precise location access granted.
                Log.i("locationP", "checkPermissions: " + "location access granted")
                checkLocationSettingsAndRequestUpdates()
//                getUserLocation(this)
            } else {
                // No location access granted.
                Log.i("locationP", "checkPermissions: " + "No location access granted")
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        checkRequestCode()
    }

    fun checkRequestCode() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
//                performAction(...)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
//                showInContextUI(...)
            }

            else -> {
                // You can directly ask for the permission.
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    1001
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                Log.d("locationP", "onRequestPermissionsResult: ")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun checkLocationSettingsAndRequestUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            startLocationUpdates()
            getUserLocation(this)
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Location settings are now satisfied, start location updatesstartLocationUpdates()
                    getUserLocation(this)
                }

                Activity.RESULT_CANCELED -> {
                    // User canceled the dialog, handle accordingly (e.g., show a message)
                }
            }
        }
    }

    companion object {
        var PERMISION_CODE: Int = 1001
        val REQUEST_CHECK_SETTINGS: Int = 1002
    }

    override fun onResume() {
        super.onResume()
        if (isLocationUpdatesActive) startLocationUpdates()
    }

    private fun startLocationUpdates() {

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Handle location updates here
                    val lat = location.latitude
                    val lon = location.longitude
                    Log.i("locationP", "onLocationCallBack: " + lat + ", " + lon)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1001
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        isLocationUpdatesActive = true
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Handle location updates here
                    val lat = location.latitude
                    val lon = location.longitude
                    Log.i("locationP", "onLocationCallBack: " + lat + ", " + lon)
                }
            }
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
        isLocationUpdatesActive = false
    }

    fun showProgressBar(status: Boolean) {
        if (status)
            loading.visibility = View.GONE
        else
            loading.visibility = View.VISIBLE
    }
}
