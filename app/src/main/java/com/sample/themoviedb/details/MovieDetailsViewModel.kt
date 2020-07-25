package com.sample.themoviedb.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sample.themoviedb.api.movies.MovieDetailsResponse
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.repositories.MovieDetailsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MovieDetailsViewModel(
    private val movieDetailsRepository: MovieDetailsRepository
) : ViewModel() {

    class MovieDetailsViewModelFactory @Inject constructor(private val movieDetailsRepository: MovieDetailsRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MovieDetailsViewModel(movieDetailsRepository) as T
        }
    }

    private val _movieDetailsLiveData =
        MutableLiveData<ViewModelResult<MovieDetailsResponse, Throwable>>()
    val movieDetailsLiveData: LiveData<ViewModelResult<MovieDetailsResponse, Throwable>>
        get() = _movieDetailsLiveData

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val movieDetails = ViewModelResult.Success(
                    movieDetailsRepository.fetchMovieDetails(movieId)
                )
                _movieDetailsLiveData.value = movieDetails
            } catch (e: Exception) {
                _movieDetailsLiveData.value = ViewModelResult.Failure(e)
            }
        }
    }
}
