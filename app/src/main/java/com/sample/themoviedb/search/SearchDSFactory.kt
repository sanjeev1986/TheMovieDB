package com.sample.themoviedb.search

import androidx.paging.DataSource
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.search.SearchApi
import kotlinx.coroutines.CoroutineScope

/**
 * Movie in theatres Search source factory
 */
class SearchDSFactory(
    private val api: SearchApi,
    private val scope: CoroutineScope,
    private val onError: (Throwable) -> Unit,
    private val onProgress: () -> Unit
) : DataSource.Factory<Int, Movie>() {
    lateinit var query:String
    var dataSource: SearchDataSource? = null
    override fun create(): DataSource<Int, Movie> =
        SearchDataSource(query, api, scope, onError,onProgress).also { dataSource = it }
}