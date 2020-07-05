package com.sample.themoviedb.browse

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.movies.MovieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Paginated data source for Movies in theatres.
 */
class InTheatresDataSource(
    private val region: String,
    private val genres: String?,
    private val api: MovieApi,
    private val scope: CoroutineScope,
    private val errorLiveData: MutableLiveData<Throwable> // TODO lamba for error callback
) : PageKeyedDataSource<Int, Movie>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        val currentPage = 1
        val nextPage = currentPage + 1
        runBlocking {
            try {
                val result = withContext(Dispatchers.IO) {
                    api.fetchNowInTheatres(1, region, genres).results
                } ?: emptyList()
                callback.onResult(result, 0, result.size, currentPage, nextPage)
            } catch (e: Exception) {
                errorLiveData.value = e
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {}

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        val currentPage = params.key
        val nextPage = currentPage + 1
        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    api.fetchNowInTheatres(nextPage, region, genres).results
                }
                callback.onResult(result ?: mutableListOf<Movie>(), nextPage)
            } catch (e: Exception) {
                errorLiveData.value = e
            }
        }
    }
}
