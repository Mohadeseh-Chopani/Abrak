package com.example.abrak.ui.View.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abrak.network.api.WeatherApiServiceProvider
import com.example.abrak.data.models.WeatherData
import com.example.abrak.R
import com.squareup.picasso.Picasso

class ForecastAdapter(listData: List<WeatherData>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    var list: List<WeatherData> = listData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        return ForecastViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false))
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        Log.i("listSize", "getItemCount: " + list.size)
        return list.size
    }

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timeTxt: TextView = itemView.findViewById(R.id.time_forecast)
        var airGrade: TextView = itemView.findViewById(R.id.air_grade_forecast)
        var img: ImageView = itemView.findViewById(R.id.img_forecast)
        var windSpeed: TextView = itemView.findViewById(R.id.wind_speed_forecast)

        fun bind(items: WeatherData) {
            val date: List<String> = items.dt_txt.toString().split(" ")
            timeTxt.setText(date.get(1))
            val temperature: Int = items.main.temp.toInt()
            airGrade.setText(temperature.toString() + " C°")
            Picasso.get()
                .load(
                    WeatherApiServiceProvider.BASE_URL + "?token=" + WeatherApiServiceProvider.API_KEY + "&action=icon&id=" +
                            items.weather.get(0).icon
                )
                .into(img)
            windSpeed.setText(items.wind.speed.toString() + "Km/H")
        }
    }
}