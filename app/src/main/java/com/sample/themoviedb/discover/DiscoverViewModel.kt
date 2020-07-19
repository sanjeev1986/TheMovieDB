package com.sample.themoviedb.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.discover.DiscoverApi
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.storage.db.watchlist.WatchListDao
import com.sample.themoviedb.storage.db.watchlist.WatchListItem
import com.sample.themoviedb.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverViewModel(
    private val inMemoryCache: InMemoryCache,
    private val watchListDao: WatchListDao,
    private val discoverApi: DiscoverApi
) : ViewModel() {

    private val _resultsLiveData = MutableLiveData<ViewModelResult<List<Movie>, Throwable>>()
    val resultsLiveData: LiveData<ViewModelResult<List<Movie>, Throwable>>
        get() = _resultsLiveData

    fun refresh() {
        inMemoryCache.get<List<Movie>>(DiscoverViewModel::class.simpleName!!)?.apply {
            _resultsLiveData.value =
                ViewModelResult.Success(this)
        } ?: viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    discoverApi.discover(1, "NL", null)
                }
                response.results?.apply {
                    inMemoryCache.put(DiscoverViewModel::class.simpleName!!, this)
                }
                _resultsLiveData.value =
                    ViewModelResult.Success(response.results ?: emptyList())
            } catch (e: Exception) {
                _resultsLiveData.value = ViewModelResult.Failure(e)
            }
        }
    }

    fun addToWatchList(movie: Movie) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                watchListDao.insertWatchList(
                    WatchListItem(
                        movieId = movie.id,
                        title = movie.title ?: "Unavailable",
                        description = movie.overview ?: "Unavailable",
                        posterPath = movie.posterPath
                    )
                )
            }
        }
    }

    fun removeFromWatchList(movie: Movie) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                watchListDao.deleteWatchList(
                    WatchListItem(
                        movieId = movie.id,
                        title = movie.title ?: "Unavailable",
                        description = movie.overview ?: "Unavailable",
                        posterPath = movie.posterPath
                    )
                )
            }
        }
    }
}
