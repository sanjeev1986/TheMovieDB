package com.sample.themoviedb.trending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.trending.TrendingApi
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrendingViewModel(
    private val inMemoryCache: InMemoryCache,
    private val api: TrendingApi
) : ViewModel() {

    private val _resultsLiveData = MutableLiveData<ViewModelResult<List<Movie>, Throwable>>()
    val resultsLiveData: LiveData<ViewModelResult<List<Movie>, Throwable>>
        get() = _resultsLiveData

    fun refresh() {
        inMemoryCache.get<List<Movie>>(TrendingViewModel::class.simpleName!!)?.apply {
            _resultsLiveData.value =
                ViewModelResult.Success(this)
        } ?: viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getTrending()
                }
                response.results?.apply {
                    inMemoryCache.put(TrendingViewModel::class.simpleName!!, this)
                }
                _resultsLiveData.value =
                    ViewModelResult.Success(response.results ?: emptyList())
            } catch (e: Exception) {
                _resultsLiveData.value = ViewModelResult.Failure(e)
            }
        }
    }
}
