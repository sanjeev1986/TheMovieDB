package com.sample.themoviedb.search

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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel uses a debounce time of 1 second to buffer the rapid typing of the user.
 * This avoids an api call for every new char entered by the user and conserves data usage
 */
@ExperimentalCoroutinesApi
class SearchViewModel(searchApi: SearchApi) : ViewModel() {
    companion object {
        object NoResultsFound : Throwable("No Results Found")
    }

    private val _resultsLiveData =
        MediatorLiveData<ViewModelResult<PagedList<Movie>, Throwable>>() // TODO why use mediator
    val resultsLiveData: LiveData<ViewModelResult<PagedList<Movie>, Throwable>> = _resultsLiveData

    private var _search: LiveData<PagedList<Movie>>? = null

    @OptIn(FlowPreview::class)
    private val queryChannel by lazy(LazyThreadSafetyMode.NONE) {
        BroadcastChannel<String>(Channel.CONFLATED).apply {
            this@apply.asFlow()
                .debounce(800)
                .distinctUntilChanged()
                .onEach { query ->
                    _search?.run { _resultsLiveData.removeSource(this) }
                    if (query.isNullOrBlank()) {
                        _resultsLiveData.value = ViewModelResult.Failure(NoResultsFound)
                    } else {
                        _search = LivePagedListBuilder<Int, Movie>(
                            SearchDSFactory(
                                query,
                                searchApi,
                                viewModelScope,
                                { error ->
                                    _resultsLiveData.value = ViewModelResult.Failure(error)
                                },
                                { _resultsLiveData.value = ViewModelResult.Progress }
                            ),
                            PagedList.Config.Builder()
                                .setEnablePlaceholders(false)
                                .setPageSize(20)
                                .setPrefetchDistance(20)
                                .build()
                        )
                            .setFetchExecutor(MainThreadExecutor())
                            .build().also {
                                _resultsLiveData.addSource(it) {
                                    _resultsLiveData.value = ViewModelResult.Success(it)
                                }
                            }
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            queryChannel.offer(query)
        }
    }
}
