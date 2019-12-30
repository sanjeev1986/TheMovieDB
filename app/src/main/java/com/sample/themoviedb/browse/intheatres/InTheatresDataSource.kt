package com.sample.themoviedb.browse.intheatres

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.movies.MovieApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Paginated data source for Movies in theatres.
 */
class InTheatresDataSource(
    private val region: String,
    private val genres: String?,
    private val api: MovieApi,
    private val disposable: CompositeDisposable,
    private val errorLiveData: MutableLiveData<Throwable>//TODO lamba for error callback
) : PageKeyedDataSource<Int, Movie>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        val currentPage = 1
        val nextPage = currentPage + 1
        disposable.add(
            api.fetchNowInTheatres(1, region, genres)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
            api.fetchNowInTheatres(nextPage, region, genres)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    callback.onResult(it.results ?: mutableListOf<Movie>(), nextPage)
                }, {
                    errorLiveData.value = it
                })
        )

    }


}