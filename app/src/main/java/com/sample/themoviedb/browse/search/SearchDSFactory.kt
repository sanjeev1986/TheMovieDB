package com.sample.themoviedb.browse.intheatres

import androidx.paging.DataSource
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.search.SearchApi
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