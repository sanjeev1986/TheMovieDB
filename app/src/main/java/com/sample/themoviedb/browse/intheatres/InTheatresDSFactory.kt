package com.sample.themoviedb.browse.intheatres

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.movies.MovieApi
import kotlinx.coroutines.CoroutineScope

/**
 * Movie in theatres data source factory
 */
class InTheatresDSFactory(
    private val region: String,
    private val api: MovieApi,
    private val scope: CoroutineScope,
    private val errorLiveData: MutableLiveData<Throwable>
) : DataSource.Factory<Int, Movie>() {
    var dataSource: InTheatresDataSource? = null
    var genres: String? = null
    override fun create(): DataSource<Int, Movie> =
        InTheatresDataSource(region, genres, api, scope, errorLiveData).also {
            dataSource = it
        }
}