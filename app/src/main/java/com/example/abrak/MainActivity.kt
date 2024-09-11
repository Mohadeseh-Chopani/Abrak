package com.example.abrak

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abrak.Adapter.ForecastAdapter
import com.example.abrak.View.PrayerTimeFragment
import com.example.abrak.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    lateinit var forecastAdapter: ForecastAdapter
    lateinit var recyclerViewForecast: RecyclerView  // Moved to lateinit, initialized later

    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerViewForecast = findViewById(R.id.recyclerview_forecast)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listData: MutableList<String> = mutableListOf()

        listData.add("12:00")
        listData.add("14:00")
        listData.add("16:00")
        listData.add("18:00")
        listData.add("20:00")
        listData.add("22:00")
        listData.add("24:00")

        // Initialize the adapter (ensure it's properly set up)
        forecastAdapter =
            ForecastAdapter(listData)  // Add necessary arguments for adapter if needed

        recyclerViewForecast.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewForecast.adapter = forecastAdapter


        findViewById<FloatingActionButton>(R.id.btn_prayer).setOnClickListener {
            val bottomSheet = PrayerTimeFragment()
            bottomSheet.show(supportFragmentManager, "MyBottomSheetFragment")
        }
    }
}
