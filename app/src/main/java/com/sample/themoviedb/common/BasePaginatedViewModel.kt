package com.sample.themoviedb.common

import android.app.Application

//TODO extract the pagination boiler plate and move it here
abstract class BasePaginatedViewModel<T>(application: Application) : BaseViewModel<T>(application) {

}