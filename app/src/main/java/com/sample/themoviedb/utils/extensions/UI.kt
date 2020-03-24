package com.sample.themoviedb.utils.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.requireAppCompatActivity() =
    requireActivity() as AppCompatActivity