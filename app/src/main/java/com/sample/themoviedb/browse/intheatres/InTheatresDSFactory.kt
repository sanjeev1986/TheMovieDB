package com.sample.themoviedb.browse.intheatres

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.movies.MovieApi
import io.reactivex.disposables.CompositeDisposable

/**
 * Movie in theatres data source factory
 */
class InTheatresDSFactory(
    private val region: String,
    private val api: MovieApi,
    private val disposable: CompositeDisposable,
    private val errorLiveData: MutableLiveData<Throwable>
) : DataSource.Factory<Int, Movie>() {
    var dataSource: InTheatresDataSource? = null
    override fun create(): DataSource<Int, Movie> =
        InTheatresDataSource(region, api, disposable, errorLiveData).also { dataSource = it }
}