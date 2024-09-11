package com.example.abrak.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abrak.R

class ForecastAdapter(listData: List<String>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    var list: List<String> = listData

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

    class ForecastViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var timeTxt:TextView = itemView.findViewById(R.id.time_forecast)
        var airGrade:TextView = itemView.findViewById(R.id.air_grade_forecast)
        var img:ImageView = itemView.findViewById(R.id.img_forecast)
        var windSpeed:TextView = itemView.findViewById(R.id.wind_speed_forecast)


        fun bind(item:String) {
            timeTxt.setText(item)
        }
    }
}