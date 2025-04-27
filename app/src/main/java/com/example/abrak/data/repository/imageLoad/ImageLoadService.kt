package com.example.abrak.data.repository.imageLoad

import android.widget.ImageView

interface ImageLoadService {
    fun loadImage(image: ImageView, url: String)
}