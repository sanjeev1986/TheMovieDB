package com.sample.themoviedb.intheatres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.storage.db.watchlist.WatchListDao
import com.sample.themoviedb.storage.db.watchlist.WatchListItem
import com.sample.themoviedb.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InTheatresViewModel(
    private val inMemoryCache: InMemoryCache,
    private val watchListDao: WatchListDao,
    private val api: MovieApi
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class InTheatresViewModelFactory @Inject constructor(
        private val inMemoryCache: InMemoryCache,
        private val watchListDao: WatchListDao,
        private val api: MovieApi
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return InTheatresViewModel(inMemoryCache, watchListDao, api) as T
        }
    }

    private val _resultsLiveData = MutableLiveData<ViewModelResult<List<Movie>, Throwable>>()
    val resultsLiveData: LiveData<ViewModelResult<List<Movie>, Throwable>>
        get() = _resultsLiveData

    fun refresh() {
        inMemoryCache.get<List<Movie>>(InTheatresViewModel::class.simpleName!!)?.apply {
            _resultsLiveData.value =
                ViewModelResult.Success(this)
        } ?: viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.fetchNowInTheatres(1, "NL", null)
                }
                response.results?.apply {
                    inMemoryCache.put(InTheatresViewModel::class.simpleName!!, this)
                }
                _resultsLiveData.value = ViewModelResult.Success(response.results ?: emptyList())
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
