package com.sample.themoviedb.browse.search

import androidx.paging.DataSource
import com.api.moviedb.Movie
import com.api.moviedb.search.SearchApi
import com.sample.themoviedb.browse.intheatres.SearchDataSource
import io.reactivex.disposables.CompositeDisposable

/**
 * Movie in theatres Search source factory
 */
class SearchDSFactory(
    private val region: String,
    private val api: SearchApi,
    private val disposable: CompositeDisposable,
    private val onError: (Throwable) -> Unit,
    private val onProgress: () -> Unit
) : DataSource.Factory<Int, Movie>() {
    var dataSource: SearchDataSource? = null
    override fun create(): DataSource<Int, Movie> =
        SearchDataSource(region, api, disposable, onError,onProgress).also { dataSource = it }
}