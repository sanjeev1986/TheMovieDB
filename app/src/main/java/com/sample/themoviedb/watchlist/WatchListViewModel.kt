package com.sample.themoviedb.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.storage.db.watchlist.WatchListDao
import com.sample.themoviedb.storage.db.watchlist.WatchListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchListViewModel(private val watchListDao: WatchListDao) : ViewModel() {
    private val _resultsLiveData =
        MutableLiveData<ViewModelResult<List<WatchListItem>, Throwable>>()
    val resultsLiveData: LiveData<ViewModelResult<List<WatchListItem>, Throwable>>
        get() = _resultsLiveData

    private val _deleteLiveData =
        MutableLiveData<ViewModelResult<WatchListItem, Throwable>>()
    val deleteLiveData: LiveData<ViewModelResult<WatchListItem, Throwable>>
        get() = _deleteLiveData

    fun fetchWatchLists() {
        viewModelScope.launch {
            val watchList = withContext(Dispatchers.IO) {
                watchListDao.getWatchLists()
            }
            _resultsLiveData.value = ViewModelResult.Success(watchList)
        }
    }

    fun deleteFromFavourites(movie: WatchListItem) {
        viewModelScope.launch {
            val watchList = withContext(Dispatchers.IO) {
                watchListDao.deleteWatchList(movie)
            }
            if (watchList > 0) {
                _deleteLiveData.value = ViewModelResult.Success(movie)
            } else {
                _deleteLiveData.value = ViewModelResult.Failure(Exception())
            }

        }
    }
}