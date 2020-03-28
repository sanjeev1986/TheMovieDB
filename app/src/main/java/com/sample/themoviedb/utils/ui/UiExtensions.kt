package com.sample.themoviedb.utils.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sample.themoviedb.R

fun ImageView.loadImage(path: String) {
    Glide.with(this)
        .load("https://image.tmdb.org/t/p/w1280/$path")
        .fallback(R.drawable.ic_baseline_photo_24px)
        .placeholder(R.drawable.ic_baseline_photo_24px)
        .error(R.drawable.ic_baseline_photo_24px)
        .into(this)
}