package com.sample.themoviedb.utils.ui

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(path: String) {
    Glide.with(this)
        .load("https://image.tmdb.org/t/p/w1280/$path")
        .into(this)
}