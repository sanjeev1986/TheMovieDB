package com.sample.themoviedb.browse.intheatres

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.genres.Genre
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.common.BaseViewModel
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.utils.MainThreadExecutor
import java.util.*

/**
 * Movies in Theatres ViewModel uses the phones Default locale to determine the region.
 * This can also be coded to you FusedLocationClient to get current location. But i have
 * kept it simple
 */
class InTheatresViewModel(application: Application, movieApi: MovieApi) :
    BaseViewModel<PagedList<Movie>>(application) {
    private val _error = MutableLiveData<Throwable>()

    private val factory = InTheatresDSFactory(
        Locale.getDefault().country,
        movieApi,
        viewModelScope,
        _error
    )
    private val _movies by lazy {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(20)
            .setPageSize(20).build()
        val executor = MainThreadExecutor()
        LivePagedListBuilder<Int, Movie>(factory, config)
            .setFetchExecutor(executor)
            .build()
    }

    init {
        _resultsLiveData.addSource(_movies) {
            _resultsLiveData.value = ViewModelResult.Success(it)
        }
        _resultsLiveData.addSource(_error) {
            _resultsLiveData.value = ViewModelResult.Failure(it)
        }
    }

    fun refresh(genres: Set<Genre>? = null) {
        factory.genres = genres?.asSequence()?.map { it.id }?.joinToString(separator = "|")  ?: kotlin.run { null}
        factory.dataSource?.invalidate()
        _resultsLiveData.value = ViewModelResult.Progress
    }

}

