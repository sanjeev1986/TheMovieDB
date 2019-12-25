package com.sample.themoviedb.browse.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.api.moviedb.Movie
import com.api.moviedb.search.SearchApi
import com.sample.themoviedb.common.BaseViewModel
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.utils.MainThreadExecutor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * ViewModel uses a debounce time of 1 second to buffer the rapid typing of the user.
 * This avvoids an api call for every new char entered by the user and conserves data usage
 */
class SearchViewModel(application: Application, private val searchApi: SearchApi) :
    BaseViewModel<PagedList<Movie>>(application) {
    val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(20)
        .setPageSize(20).build()

    val executor = MainThreadExecutor()//Since we are using Rx Schdulers

    var _search: LiveData<PagedList<Movie>>? = null
    private val searchDeBouncer: PublishSubject<String?> by lazy(LazyThreadSafetyMode.NONE) {
        val subject = PublishSubject.create<String>()
        subject.debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .distinctUntilChanged()
            .subscribe({ query ->
                Timber.d(query)
                val factory = SearchDSFactory(
                    query,
                    searchApi,
                    disposables,
                    { error -> _resultsLiveData.value = ViewModelResult.Failure(error) },
                    { _resultsLiveData.value = ViewModelResult.Progress }
                )
                _search?.run { _resultsLiveData.removeSource(this) }
                _search =
                    LivePagedListBuilder<Int, Movie>(factory, config)
                        .setFetchExecutor(executor)
                        .build().also {
                            _resultsLiveData.addSource(it) {
                                _resultsLiveData.value = ViewModelResult.Success(it)
                            }
                        }
            }, {
                it.printStackTrace()
                _resultsLiveData.value = ViewModelResult.Failure(it)
            })
        subject
    }

    fun search(query: String) {
        searchDeBouncer.onNext(query)
    }
}