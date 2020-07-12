package com.sample.themoviedb.utils.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.sample.themoviedb.R
import com.sample.themoviedb.common.BaseActivity
import com.sample.themoviedb.common.BaseFragment

fun ImageView.loadImage(path: String) {
    val cropOptions = RequestOptions()
        .fallback(R.drawable.ic_baseline_photo_24px)
        .error(R.drawable.ic_baseline_photo_24px)
    Glide.with(this)
        .load("https://image.tmdb.org/t/p/w1280/$path")
        .apply(cropOptions)
        .centerCrop()
        .transition(withCrossFade())
        .into(this)
}

fun BaseFragment.baseActivity(): BaseActivity = activity as BaseActivity
