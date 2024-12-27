package com.example.abrak.data.repository

import android.widget.ImageView
import com.example.abrak.network.api.ApiServiceProvider
import com.squareup.picasso.Picasso

class ImageLoadServiceImp: ImageLoadService {
    override fun loadImage(image: ImageView, url: String) {
        Picasso.get()
            .load(url)
            .into(image)
    }
}