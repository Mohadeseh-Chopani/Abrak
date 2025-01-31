package com.example.abrak.ui.View.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abrak.network.api.weatherAPI.WeatherApiServiceProvider
import com.example.abrak.data.models.WeatherData
import com.example.abrak.R
import com.example.abrak.databinding.CardItemBinding
import com.squareup.picasso.Picasso

class ForecastAdapter(listData: List<WeatherData>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    var list: List<WeatherData> = listData
    private lateinit var binding: CardItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        Log.i("listSize", "getItemCount: " + list.size)
        return list.size
    }

    class ForecastViewHolder(private val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(items: WeatherData) {
            val date: List<String> = items.dt_txt.toString().split(" ")
            binding.timeForecast.setText(date.get(1))
            val temperature: Int = items.main.temp.toInt()
            binding.airGradeForecast.setText(temperature.toString() + " C°")
            Picasso.get()
                .load(
                    WeatherApiServiceProvider.BASE_URL + "?token=" + WeatherApiServiceProvider.API_KEY + "&action=icon&id=" +
                            items.weather.get(0).icon
                )
                .into(binding.imgForecast)
            binding.windSpeedForecast.setText(items.wind.speed.toString() + "Km/H")
        }
    }
}