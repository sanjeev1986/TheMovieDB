package com.sample.themoviedb.browse.search

import androidx.paging.PageKeyedDataSource
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.search.SearchApi
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

/**
 * Paginated data source for Search.
 */
class SearchDataSource(
    private val query: String,
    private val api: SearchApi,
    private val disposable: CompositeDisposable,
    private val onError: (Throwable) -> Unit,
    private val onProgress: () -> Unit
) : PageKeyedDataSource<Int, Movie>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        Timber.d("loadInitial")
        val currentPage = 1
        val nextPage = currentPage + 1
        onProgress()
        disposable.add(
            api.searchMovies(1, query)
                .subscribe({
                    callback.onResult(it.results ?: mutableListOf<Movie>(), currentPage, nextPage)
                }, {
                    onError(it)
                })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        Timber.d("loadBefore")
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        Timber.d("loadAfter")
        val currentPage = params.key
        val nextPage = currentPage + 1
        disposable.add(
            api.searchMovies(nextPage, query)
                .subscribe({
                    callback.onResult(it.results ?: mutableListOf<Movie>(), nextPage)
                }, {
                    onError(it)
                })
        )

    }


}