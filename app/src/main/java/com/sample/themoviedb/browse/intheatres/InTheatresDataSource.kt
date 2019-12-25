package com.sample.themoviedb.browse.intheatres

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.api.moviedb.Movie
import com.api.moviedb.movies.MovieApi
import io.reactivex.disposables.CompositeDisposable

/**
 * Paginated data source for Movies in theatres.
 */
class InTheatresDataSource(
    private val region: String,
    private val api: MovieApi,
    private val disposable: CompositeDisposable,
    private val errorLiveData: MutableLiveData<Throwable>//TODO lamba for error callback
) : PageKeyedDataSource<Int, Movie>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        val currentPage = 1
        val nextPage = currentPage + 1
        disposable.add(
            api.getNowInThreatres(1, region)
                .subscribe({
                    callback.onResult(it.results ?: mutableListOf<Movie>(), currentPage, nextPage)
                }, {
                    errorLiveData.value = it
                })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {}

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        val currentPage = params.key
        val nextPage = currentPage + 1

        disposable.add(
            api.getNowInThreatres(nextPage, region)
                .subscribe({
                    callback.onResult(it.results ?: mutableListOf<Movie>(), nextPage)
                }, {
                    errorLiveData.value = it
                })
        )

    }


}