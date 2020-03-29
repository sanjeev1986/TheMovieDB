package com.sample.themoviedb.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.search.SearchApi
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.utils.MainThreadExecutor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * ViewModel uses a debounce time of 1 second to buffer the rapid typing of the user.
 * This avoids an api call for every new char entered by the user and conserves data usage
 */
@ExperimentalCoroutinesApi
class SearchViewModel(searchApi: SearchApi) : ViewModel() {
    private val _resultsLiveData =
        MediatorLiveData<ViewModelResult<PagedList<Movie>, Throwable>>()//TODO why use mediator
    val resultsLiveData: LiveData<ViewModelResult<PagedList<Movie>, Throwable>> = _resultsLiveData
    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(20)
        .setPageSize(20).build()

    private val executor = MainThreadExecutor()

    private var _search: LiveData<PagedList<Movie>>? = null

    private val factory = SearchDSFactory(
        searchApi,
        viewModelScope,
        { error -> _resultsLiveData.value = ViewModelResult.Failure(error) },
        { _resultsLiveData.value = ViewModelResult.Progress }
    )

    private val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    @UseExperimental(FlowPreview::class)
    internal val queryFlow = queryChannel.apply {
        viewModelScope.launch {
            this@apply.asFlow()
                .debounce(800)
                .catch {
                    it.printStackTrace()
                }.collect { query ->
                    factory.query = query
                    factory.dataSource?.invalidate()
                    _search?.run { _resultsLiveData.removeSource(this) }
                    _search =
                        LivePagedListBuilder<Int, Movie>(factory, config)
                            .setFetchExecutor(executor)
                            .build().also {
                                _resultsLiveData.addSource(it) {
                                    _resultsLiveData.value = ViewModelResult.Success(it)
                                }
                            }
                }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            queryChannel.offer(query)
        }

    }
}