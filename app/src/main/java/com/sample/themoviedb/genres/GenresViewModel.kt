package com.sample.themoviedb.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sample.themoviedb.api.genres.Genre
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.platform.Disconnected
import com.sample.themoviedb.platform.NetworkManager
import com.sample.themoviedb.platform.NotConnectedToInternet
import com.sample.themoviedb.repositories.GenreRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class GenresViewModel(
    private val genreRepository: GenreRepository,
    private val networkManager: NetworkManager
) : ViewModel() {

    class GenresViewModelFactory @Inject constructor(
        private val genreRepository: GenreRepository,
        private val networkManager: NetworkManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return GenresViewModel(genreRepository, networkManager) as T
        }
    }

    private val _genresLiveData = MutableLiveData<ViewModelResult<List<Genre>, Throwable>>()
    val genresLiveData: LiveData<ViewModelResult<List<Genre>, Throwable>>
        get() = _genresLiveData

    val selectedGenres = MutableLiveData<Set<Genre>>()

    fun fetchGenres() {
        viewModelScope.launch {
            try {
                _genresLiveData.value = ViewModelResult.Success(genreRepository.fetchGenres())
            } catch (e: Exception) {
                if (e is NotConnectedToInternet) {
                    networkManager.listenToConnectivityChanges().collect {
                        Timber.d(it::class.java.simpleName)
                        if (it !is Disconnected) {
                            try {
                                _genresLiveData.value =
                                    ViewModelResult.Success(genreRepository.fetchGenres())
                            } catch (e: java.lang.Exception) {
                                _genresLiveData.value = ViewModelResult.Failure(e, emptyList())
                            }
                        }
                    }
                }
            }
        }
    }
}
