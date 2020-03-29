package com.sample.themoviedb.search

import androidx.paging.PageKeyedDataSource
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.search.SearchApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Paginated data source for Search.
 */
class SearchDataSource(
    private val query: String,
    private val api: SearchApi,
    private val scope: CoroutineScope,
    private val onError: (Throwable) -> Unit,
    private val onProgress: () -> Unit
) : PageKeyedDataSource<Int, Movie>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        Timber.d("loadInitial")
        val currentPage = 1
        val nextPage = currentPage + 1
        onProgress()
        scope.launch {
            try {
                callback.onResult(withContext(Dispatchers.IO) {
                    api.searchMovieDb(1, query).results
                } ?: mutableListOf<Movie>(), currentPage, nextPage)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        Timber.d("loadBefore")
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        Timber.d("loadAfter")
        val currentPage = params.key
        val nextPage = currentPage + 1
        scope.launch {
            try {
                callback.onResult(withContext(Dispatchers.IO) {
                    api.searchMovieDb(nextPage, query).results
                } ?: mutableListOf<Movie>(), nextPage)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }


}