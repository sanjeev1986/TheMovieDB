package com.sample.themoviedb.common

/**
 * All results delivered to View are held in this type
 */
sealed class ViewModelResult<out Result, out Error : Throwable> {
    data class Success<out R>(val result: R) : ViewModelResult<R, Nothing>()
    data class Failure<out E : Throwable>(val error: E) : ViewModelResult<Nothing, E>()
    object Progress : ViewModelResult<Nothing, Nothing>()
}
