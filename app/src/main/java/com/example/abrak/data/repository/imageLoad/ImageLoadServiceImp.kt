package com.example.abrak.data.repository.imageLoad

import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageLoadServiceImp: ImageLoadService {
    override fun loadImage(image: ImageView, url: String) {
        Picasso.get()
            .load(url)
            .into(image)
    }
}