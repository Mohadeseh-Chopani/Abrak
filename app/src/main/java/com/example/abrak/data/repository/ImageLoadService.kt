package com.example.abrak.data.repository

import android.widget.ImageView

interface ImageLoadService {
    fun loadImage(image: ImageView, url: String)
}