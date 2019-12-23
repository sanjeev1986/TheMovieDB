package com.sample.themoviedb.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.sample.themoviedb.TheMovieDbApp
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

/**
 * All results delievered to View are held in this type
 */
sealed class ViewModelResult<out Result, out Error : Throwable> {
    data class Success<out R>(val result: R) : ViewModelResult<R, Nothing>()
    data class Failure<out E : Throwable>(val error: E) : ViewModelResult<Nothing, E>()
    object Progress : ViewModelResult<Nothing, Nothing>()
}

/**
 * Base View Model which has to be inherited
 */
abstract class BaseViewModel<T>(application: Application) : AndroidViewModel(application) {

    protected val _resultsLiveData = MediatorLiveData<ViewModelResult<T, Throwable>>()//TODO why use mediator
    val resultsLiveData: LiveData<ViewModelResult<T, Throwable>>
        get() = _resultsLiveData
    protected val disposables = CompositeDisposable()
    /**
     * Not used in the app in its current state.
     * but demonstrates how BaseViewModel can access
     * Android's platform abstractions
     */

    protected val networkManager by lazy { TheMovieDbApp.getInstance(application).platformManager.networkManager }

    /**
     * Not used in the app in its current state.
     */
    protected inline fun <T> checkNetworkAndDispatch(crossinline networkBlock: () -> Single<T>): Single<T> =
        Single.defer { networkManager.getNetworkStatus() }.flatMap { networkBlock() }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}