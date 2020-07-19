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

    private val _resultsLiveData =
        MutableLiveData<ViewModelResult<List<Pair<Movie, Boolean>>, Throwable>>()
    val resultsLiveData: LiveData<ViewModelResult<List<Pair<Movie, Boolean>>, Throwable>>
        get() = _resultsLiveData

    fun refresh() {
        inMemoryCache.get<List<Pair<Movie, Boolean>>>(DiscoverViewModel::class.simpleName!!)
            ?.apply {
                _resultsLiveData.value =
                    ViewModelResult.Success(this)
            } ?: viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val response = discoverApi.discover(1, "NL", null)
                    val watchLists = response.results?.map { it.id }?.toIntArray()
                        ?.let { watchListDao.getWatchLists() }
                    response.results?.map { movie ->
                        Pair(movie, watchLists?.find { it.movieId == movie.id } != null)
                    }
                }
                response?.apply {
                    inMemoryCache.put(DiscoverViewModel::class.simpleName!!, this)
                }

                _resultsLiveData.value =
                    ViewModelResult.Success(response ?: emptyList())
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
