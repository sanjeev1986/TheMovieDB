package com.sample.themoviedb.storage

sealed class CacheResult<out Result, out Error : Throwable> {
    data class CacheHit<out R>(val result: R) : CacheResult<R, Nothing>()
    object CacheMiss : CacheResult<Nothing, Nothing>()
    data class CacheError<out E : Throwable>(val error: E) : CacheResult<Nothing, E>()
}
